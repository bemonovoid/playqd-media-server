package io.playqd.mediaserver.persistence.jpa.entity;

import io.playqd.mediaserver.service.upnp.service.StateVariableName;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = StateVariableJpaEntity.TABLE_NAME)
public class StateVariableJpaEntity extends PersistableAuditableEntity {

    static final String TABLE_NAME = "upnp_state_variables";

    private static final String COL_VARIABLE_NAME = "variable_name";
    private static final String COL_VALUE = "value";

    @Enumerated(EnumType.STRING)
    @Column(name = COL_VARIABLE_NAME, nullable = false, unique = true)
    private StateVariableName key;

    @Column(name = COL_VALUE, nullable = false)
    private String value;

}
