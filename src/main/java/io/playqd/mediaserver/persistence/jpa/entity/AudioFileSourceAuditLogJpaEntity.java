package io.playqd.mediaserver.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = AudioFileSourceAuditLogJpaEntity.TABLE_NAME)
public class AudioFileSourceAuditLogJpaEntity extends PersistableAuditableEntity {

    static final String TABLE_NAME = "audio_file_source_audit_log";

    private static final String COL_LAST_ADDED_DATE = "last_added_date";

    @Column(name = COL_LAST_ADDED_DATE)
    private LocalDateTime lastAddedDate;

}
