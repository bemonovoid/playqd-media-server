package io.playqd.mediaserver.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableEntity {

    public static final String COL_CREATED_BY = "created_by";
    public static final String COL_CREATED_DATE = "created_date";
    public static final String COL_LAST_MODIFIED_BY = "last_modified_by";
    public static final String COL_LAST_MODIFIED_DATE = "last_modified_date";

    @CreatedBy
    @Column(name = COL_CREATED_BY)
    private String createdBy;

    @CreatedDate
    @Column(name = COL_CREATED_DATE)
    private LocalDateTime createdDate;

    @LastModifiedBy
    @Column(name = COL_LAST_MODIFIED_BY)
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = COL_LAST_MODIFIED_DATE)
    private LocalDateTime lastModifiedDate;
}
