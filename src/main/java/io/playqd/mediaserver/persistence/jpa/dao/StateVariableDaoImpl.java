package io.playqd.mediaserver.persistence.jpa.dao;

import io.playqd.mediaserver.model.StateVariables;
import io.playqd.mediaserver.persistence.StateVariableDao;
import io.playqd.mediaserver.persistence.jpa.entity.StateVariableJpaEntity;
import io.playqd.mediaserver.persistence.jpa.repository.StateVariableRepository;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Optional;

@Component
class StateVariableDaoImpl implements StateVariableDao {

    private final StateVariableRepository repository;

    StateVariableDaoImpl(StateVariableRepository repository) {
        this.repository = repository;
    }

    @Override
    public <T> Optional<T> get(StateVariables stateVariable) {
        return repository.findFirstByKey(stateVariable)
                .map(entity -> {
                    //noinspection unchecked
                    return (T) stateVariable.getDeserializer().apply(entity.getValue());
                });
    }

    @Override
    public <T extends Serializable> T set(StateVariables stateVariable, T value) {
        var entity = repository.findFirstByKey(stateVariable).orElseGet(() -> {
            var e = new StateVariableJpaEntity();
            e.setKey(stateVariable);
            return e;
        });
        entity.setValue(stateVariable.getSerializer().apply(value));
        repository.saveAndFlush(entity);
        return value;
    }

}
