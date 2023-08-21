package io.playqd.mediaserver.service.playlist;

import io.playqd.mediaserver.model.AudioFile;

import java.util.List;

public interface PlaylistService {

    List<AudioFile> getPlaylistAudioFiles(String playlistId);
}
