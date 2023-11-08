package io.playqd.mediaserver.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@EqualsAndHashCode(of = "name", callSuper = true)
@Entity
@Table(name = MediaSourceInfoEntity.TABLE_NAME)
public class MediaSourceInfoEntity extends PersistableAuditableEntity {

  static final String TABLE_NAME = "media_source_info";

  private static final String COL_NAME = "name";
  private static final String SIZE_IN_BYTES = "size";
  private static final String LAST_SCAN_DATE = "last_scan_date";

  @Column(name = COL_NAME)
  private String name;

  @Column(name = SIZE_IN_BYTES)
  private int sizeInBytes;

  @Column(name = LAST_SCAN_DATE)
  private Instant lastScanDate;

}
