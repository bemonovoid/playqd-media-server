package io.playqd.mediaserver.config.upnp;

import io.playqd.mediaserver.config.properties.PlayqdProperties;
import io.playqd.mediaserver.persistence.AudioFileDao;
import io.playqd.mediaserver.persistence.BrowsableObjectDao;
import io.playqd.mediaserver.persistence.MediaSourceDao;
import io.playqd.mediaserver.service.metadata.ImageService;
import io.playqd.mediaserver.service.playlist.PlaylistService;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.BrowsableObjectFinder;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.impl.AlbumsByArtistFinder;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.impl.AlbumsByGenreFinder;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.impl.ArtistsByGenreFinder;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.impl.ArtistsFinder;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.impl.GenresFinder;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.impl.MediaSourceContentFinder;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.impl.MediaSourcesFinder;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.impl.MusicLibraryChildrenFinder;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.impl.PlaylistFilesFinder;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.impl.RootContainersFinder;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.impl.TracksByArtistAlbumFinder;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.impl.TracksByArtistFinder;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.impl.TracksByPlaylistFinder;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.impl.TracksMostPlayedFinder;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.impl.TracksRecentlyAddedFinder;
import io.playqd.mediaserver.service.upnp.service.contentdirectory.impl.TracksRecentlyPlayedFinder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(UpnpDaoContextConfiguration.class)
@ConditionalOnProperty(prefix = "playqd.upnp", name = "enabled", havingValue = "true", matchIfMissing = true)
public class UpnpBrowsableObjectFinderContextConfiguration {

  @Bean
  BrowsableObjectFinder rootContainersFinder(MediaSourceDao mediaSourceDao) {
    return new RootContainersFinder(mediaSourceDao);
  }

  @Bean
  BrowsableObjectFinder musicLibraryChildrenFinder(AudioFileDao audioFileDao) {
    return new MusicLibraryChildrenFinder(audioFileDao);
  }

  @Bean
  BrowsableObjectFinder musicLibraryArtistsFinder(AudioFileDao audioFileDao) {
    return new ArtistsFinder(audioFileDao);
  }

  @Bean
  BrowsableObjectFinder musicLibraryArtistAlbumsFinder(AudioFileDao audioFileDao) {
    return new AlbumsByArtistFinder(audioFileDao);
  }

  @Bean
  BrowsableObjectFinder musicLibraryGenresFinder(AudioFileDao audioFileDao) {
    return new GenresFinder(audioFileDao);
  }

  @Bean
  BrowsableObjectFinder musicLibraryGenreArtistsFinder(AudioFileDao audioFileDao) {
    return new ArtistsByGenreFinder(audioFileDao);
  }

  @Bean
  BrowsableObjectFinder musicLibraryGenreAlbumsFinder(AudioFileDao audioFileDao) {
    return new AlbumsByGenreFinder(audioFileDao);
  }

  @Bean
  BrowsableObjectFinder mediaSourcesFinder(MediaSourceDao mediaSourceDao, BrowsableObjectDao browsableObjectDao) {
    return new MediaSourcesFinder(mediaSourceDao, browsableObjectDao);
  }

  @Bean
  BrowsableObjectFinder mediaSourceContentFinder(PlayqdProperties playqdProperties,
                                                 AudioFileDao audioFileDao,
                                                 BrowsableObjectDao browsableObjectDao) {
    return new MediaSourceContentFinder(playqdProperties, audioFileDao, browsableObjectDao);
  }

  @Bean
  BrowsableObjectFinder playlistFilesFinder(PlayqdProperties playqdProperties) {
    return new PlaylistFilesFinder(playqdProperties);
  }

  @Bean
  BrowsableObjectFinder tracksByArtistAlbumFinder(PlayqdProperties playqdProperties,
                                                  AudioFileDao audioFileDao,
                                                  ImageService imageService) {
    return new TracksByArtistAlbumFinder(playqdProperties, audioFileDao, imageService);
  }

  @Bean
  BrowsableObjectFinder tracksByArtistFinder(PlayqdProperties playqdProperties,
                                             AudioFileDao audioFileDao,
                                             ImageService imageService) {
    return new TracksByArtistFinder(playqdProperties, audioFileDao, imageService);
  }

  @Bean
  BrowsableObjectFinder tracksByPlaylistFinder(PlayqdProperties playqdProperties,
                                               AudioFileDao audioFileDao,
                                               ImageService imageService,
                                               PlaylistService playlistService) {
    return new TracksByPlaylistFinder(playqdProperties, audioFileDao, imageService, playlistService);
  }

  @Bean
  BrowsableObjectFinder racksMostPlayedFinder(PlayqdProperties playqdProperties,
                                               AudioFileDao audioFileDao,
                                               ImageService imageService) {
    return new TracksMostPlayedFinder(playqdProperties, audioFileDao, imageService);
  }

  @Bean
  BrowsableObjectFinder tracksRecentlyAddedFinder(PlayqdProperties playqdProperties,
                                                  AudioFileDao audioFileDao,
                                                  ImageService imageService) {
    return new TracksRecentlyAddedFinder(playqdProperties, audioFileDao, imageService);
  }

  @Bean
  BrowsableObjectFinder tracksRecentlyPlayedFinder(PlayqdProperties playqdProperties,
                                                   AudioFileDao audioFileDao,
                                                   ImageService imageService) {
    return new TracksRecentlyPlayedFinder(playqdProperties, audioFileDao, imageService);
  }

}
