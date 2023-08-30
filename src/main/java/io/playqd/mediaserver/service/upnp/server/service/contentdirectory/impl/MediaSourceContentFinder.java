package io.playqd.mediaserver.service.upnp.server.service.contentdirectory.impl;

import io.playqd.mediaserver.api.soap.data.Browse;
import io.playqd.mediaserver.exception.PlayqdException;
import io.playqd.mediaserver.model.*;
import io.playqd.mediaserver.persistence.AudioFileDao;
import io.playqd.mediaserver.persistence.BrowsableObjectDao;
import io.playqd.mediaserver.persistence.jpa.dao.BrowsableObjectSetter;
import io.playqd.mediaserver.persistence.jpa.dao.BrowseResult;
import io.playqd.mediaserver.persistence.jpa.dao.PersistedBrowsableObject;
import io.playqd.mediaserver.service.upnp.server.service.UpnpActionHandlerException;
import io.playqd.mediaserver.service.upnp.server.service.contentdirectory.*;
import lombok.extern.slf4j.Slf4j;
import org.jupnp.model.types.ErrorCode;
import org.jupnp.support.model.ProtocolInfo;
import org.jupnp.util.MimeType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
final class MediaSourceContentFinder implements BrowsableObjectFinder {

    private final AudioFileDao audioFileDao;
    private final BrowsableObjectDao browsableObjectDao;

    MediaSourceContentFinder(AudioFileDao audioFileDao, BrowsableObjectDao browsableObjectDao) {
        this.audioFileDao = audioFileDao;
        this.browsableObjectDao = browsableObjectDao;
    }

    @Override
    public BrowseResult find(BrowseContext context) {
        var parentObject = getParent(context);
        if (Files.isDirectory(parentObject.location())) {
            // Prepare children object if the parentObject has not been visited yet
            if (!browsableObjectDao.hasChildren(parentObject.id())) {
                buildChildrenObjects(parentObject);
            }
            return queryChildrenInternal(parentObject.id(), context.getRequest());
        }
        // This shouldn't really happen, because the renderers must not browse a file, unless the object type was
        // mistakenly set to container type
        log.error("Browsing a file is illegal.");
        throw new UpnpActionHandlerException(ErrorCode.ARGUMENT_VALUE_INVALID);
    }

    private PersistedBrowsableObject getParent(BrowseContext context) {
        return browsableObjectDao.getOneByObjectId(context.getObjectId())
                .orElseThrow(() -> {
                    log.error("Browsable object with objectId '{}' was not found.", context.getObjectId());
                    return new UpnpActionHandlerException(ErrorCode.ARGUMENT_VALUE_INVALID);
                });
    }

    private BrowseResult queryChildrenInternal(long parentId, Browse browseRequest) {
        var startingIndex = browseRequest.getStartingIndex();
        var requestedCount = browseRequest.getRequestedCount();
        var totalCount = (int) browsableObjectDao.countChildren(parentId);

        if (startingIndex > totalCount) {
            return BrowseResult.empty();
        } else if (startingIndex > 0) {
            var pageRequest = PageRequest.of(0, totalCount + 1, Sort.by(Sort.Order.by("dcTitle")));
            var children = browsableObjectDao.getChildren(parentId, pageRequest);
            var childrenOffset = children.stream().skip(startingIndex).toList();

            var result = buildBrowsableObjects(browseRequest, childrenOffset.stream().limit(requestedCount).toList());

            return new BrowseResult(childrenOffset.size(), result.size(), result);
        } else {
            var pageRequest = PageRequest.of(0, requestedCount, Sort.by(Sort.Order.by("dcTitle")));
            var page = browsableObjectDao.getChildren(parentId, pageRequest);
            var result = buildBrowsableObjects(browseRequest, page.getContent());
            return new BrowseResult(page.getSize(), page.getSize(), result);
        }
    }

    private List<BrowsableObject> buildBrowsableObjects(Browse browseRequest, List<PersistedBrowsableObject> objects) {
        Map<Boolean, List<PersistedBrowsableObject>> dirsAndFiles = objects.stream()
                .collect(Collectors.groupingBy(obj -> Files.isDirectory(obj.location())));
        var dirs = dirsAndFiles.get(true);
        var result = Collections.<BrowsableObject>emptyList();
        if (!CollectionUtils.isEmpty(dirs)) {
            var containerObjects = dirs.stream().map(obj -> buildContainerObject(browseRequest, obj)).toList();
            result = new ArrayList<>(containerObjects);
        }
        var files = dirsAndFiles.get(false);
        if (!CollectionUtils.isEmpty(files)) {

            // true -> audio files; false -> all the rest supported files
            var mediaFileLocations = files.stream()
                    .collect(Collectors.groupingBy(obj -> SupportedAudioFiles.isSupportedAudioFile(obj.location())));

            var audioFileLocations = mediaFileLocations.getOrDefault(true, Collections.emptyList()).stream()
                    .map(obj -> obj.location().toString()).toList();

            // Collect audio files metadata to build content directory item objects
            var pathToAudioFileMap = audioFileDao.getAudioFilesByLocationIn(audioFileLocations).stream()
                    .collect(Collectors.toMap(AudioFile::path, v -> v));

            var itemObjects = files.stream()
//                    .filter(obj -> pathToAudioFileMap.containsKey(obj.location()))
                    .map(obj -> buildItemObject(browseRequest, obj, pathToAudioFileMap.get(obj.location())))
                    .toList();
            if (CollectionUtils.isEmpty(result)) {
                return new ArrayList<>(itemObjects);
            }
            result.addAll(itemObjects);
        }
        return result;
    }

    private void buildChildrenObjects(PersistedBrowsableObject parent) {
        try (Stream<Path> dirs = Files.list(parent.location())) { //TODO check if exists
            var childrenSetters = dirs
                    .map(path -> {
                        if (Files.isDirectory(path)) {
                            return Tuple.from(path, UpnpClass.storageFolder);
                        }
                        var fileExtension = FileUtils.getFileExtension(path.toString());
                        if (SupportedAudioFiles.isSupportedAudioFile(fileExtension)) {
                            return Tuple.from(path, UpnpClass.audioItem);
                        }
                        if (SupportedImageFiles.isSupportedImageFile(fileExtension)) {
                            return Tuple.from(path, UpnpClass.image);
                        }
                        return Tuple.from(path, UpnpClass.item);
                    })
                    .filter(tuple -> UpnpClass.item != tuple.right())
                    .map(this::createBrowsableObjectSetter)
                    .collect(Collectors.toList());
            browsableObjectDao.save(parent, childrenSetters);
        } catch (IOException e) {
            throw new PlayqdException(e);
        }
    }

    private Consumer<BrowsableObjectSetter> createBrowsableObjectSetter(Tuple<Path, UpnpClass> tuple) {
        var isStorageFolder = UpnpClass.storageFolder == tuple.right();
        return setter -> {
            setter.setDcTitle(resolveDcTitle(tuple.left(), isStorageFolder));
            setter.setLocation(tuple.left().toString());
            setter.setUpnpClass(tuple.right());
            setter.setChildrenCountTransient(isStorageFolder ? countChildren(tuple.left()) : 0);
        };
    }

    private static long countChildren(Path path) {
        try (Stream<Path> dirItems = Files.list(path)) {
            return dirItems.count();
        } catch (IOException e) {
            throw new PlayqdException(String.format("Failed counting container children at %s", path), e);
        }
    }

    private static String resolveDcTitle(Path path, boolean isDirectory) {
        var fileName = path.getFileName().toString();
        if (isDirectory) {
            return fileName;
        }
        return FileUtils.getFileNameWithoutExtension(fileName);
    }

    private static BrowsableObject buildContainerObject(Browse browseRequest, PersistedBrowsableObject object) {
        return BrowsableObjectImpl.builder()
                .objectId(object.objectId())
                .parentObjectId(browseRequest.getObjectID())
                .searchable(true)
                .childCount(object.childrenCount().get())
                .dc(buildDcTagValues(object))
                .upnp(buildUpnpTagValues(object))
                .build();
    }

    private static BrowsableObject buildItemObject(Browse browseRequest,
                                                   PersistedBrowsableObject object,
                                                   AudioFile audioFile) {
        return BrowsableObjectImpl.builder()
                .objectId(object.objectId())
                .parentObjectId(browseRequest.getObjectID())
                .dc(buildDcTagValues(object, audioFile))
                .upnp(buildUpnpTagValues(object, audioFile))
                .resources(buildResources(object, audioFile))
                .build();
    }

    private static DcTagValues buildDcTagValues(PersistedBrowsableObject object) {
        return buildDcTagValues(object, null);
    }

    private static DcTagValues buildDcTagValues(PersistedBrowsableObject object, AudioFile audioFile) {
        var builder = DcTagValues.builder();
        if (audioFile != null) {
            builder = builder.creator(audioFile.artistName());
        }
        return builder
                .title(object.dcTitle())
                .build();
    }

    private static UpnpTagValues buildUpnpTagValues(PersistedBrowsableObject object) {
        return buildUpnpTagValues(object, null);
    }

    private static UpnpTagValues buildUpnpTagValues(PersistedBrowsableObject object, AudioFile audioFile) {
        var builder = UpnpTagValues.builder();
        if (audioFile == null) {
            return builder.upnpClass(UpnpClass.storageFolder).build();
        }
        return builder
                .artist(audioFile.artistName())
                .album(audioFile.albumName())
                .genre(audioFile.genre())
                .originalTrackNumber(audioFile.trackNumber())
                .upnpClass(UpnpClass.musicTrack)
                .playbackCount(audioFile.playbackCount())
                .lastPlaybackTime(AudioFile.getLastPlaybackTimeFormatted(audioFile).orElse(null))
                .build();
    }

    private static List<ResTag> buildResources(PersistedBrowsableObject object, AudioFile audioFile) {
        return List.of(ResTag.builder()
                .id(Long.toString(audioFile.id()))
                .uri(audioFile.getAudioStreamUri())
                .protocolInfo(new ProtocolInfo(MimeType.valueOf(audioFile.mimeType())).toString())
                .bitsPerSample(Integer.toString(audioFile.bitsPerSample()))
                .bitRate(audioFile.bitRate())
                .sampleFrequency(audioFile.sampleRate())
                .size(Long.toString(audioFile.size()))
                .duration(ResTag.formatDLNADuration(audioFile.preciseTrackLength()))
                .build());
    }

}