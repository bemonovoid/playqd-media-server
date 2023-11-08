package io.playqd.mediaserver.persistence.jpa.dao;

import io.playqd.mediaserver.persistence.MediaSourceInfoDao;
import io.playqd.mediaserver.persistence.jpa.repository.MediaSourceInfoRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JpaMediaSourceInfoDao implements MediaSourceInfoDao {

  private final MediaSourceInfoRepository repository;

  public JpaMediaSourceInfoDao(MediaSourceInfoRepository repository) {
    this.repository = repository;
  }


}
