package io.playqd.mediaserver.persistence.jpa.entity;

import io.playqd.mediaserver.persistence.jpa.dao.BrowsableObjectSetter;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(of = "location", callSuper = false)
@Entity
@Table(name = BrowsableObjectEntity.TABLE_NAME,  indexes = {
        @Index(name = "object_id_idx", columnList = "object_id")
})
public class BrowsableObjectEntity extends PersistableAuditableEntity implements BrowsableObjectSetter {

    static final String TABLE_NAME = "upnp_browsable_object";

    private static final String COL_OBJECT_ID = "object_id";

    @Column(name = COL_OBJECT_ID, nullable = false)
    private String objectId;

    @Column(name = "title", nullable = false)
    private String dcTitle;

    @Column(nullable = false)
    private String location;

    @Column(name = "children_count_transient")
    private long childrenCountTransient;

    @Column(name = "parent_id", insertable=false, updatable=false)
    private Long parentId;

    @ManyToOne(fetch = FetchType.LAZY)
    private BrowsableObjectEntity parent;

    @OneToMany(
            mappedBy = "parent",
            fetch = FetchType.LAZY,
            cascade = { CascadeType.ALL}
    )
    private List<BrowsableObjectEntity> children;

    public final long getChildCount() {
        return !CollectionUtils.isEmpty(getChildren()) ? getChildren().size() : getChildrenCountTransient();
    }

}
