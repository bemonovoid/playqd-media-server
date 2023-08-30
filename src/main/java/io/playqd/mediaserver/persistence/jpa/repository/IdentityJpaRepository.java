package io.playqd.mediaserver.persistence.jpa.repository;

import io.playqd.mediaserver.exception.DatabaseEntityNotFoundException;
import io.playqd.mediaserver.exception.PlayqdException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface IdentityJpaRepository<T> extends JpaRepository<T, Long> {

    default T get(long id) {
        try {
            return getReferenceById(id);
        } catch (EntityNotFoundException e) {
            return findById(id).orElseThrow(() -> new DatabaseEntityNotFoundException(e.getMessage(), e));
        }
    }
}
