package io.playqd.mediaserver.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

@Getter
@Setter
@EqualsAndHashCode(of = "id", callSuper = false)
@MappedSuperclass
public abstract class PersistableAuditableEntity extends AuditableEntity implements Persistable<Long> {

    public static final String COL_PK_ID = "id";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COL_PK_ID, updatable = false, nullable = false)
    private Long id;

    @Override
    public boolean isNew() {
        return getId() == null;
    }

    public boolean exists() {
        return getId() != null;
    }

}
