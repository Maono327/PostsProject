package com.postsproject.repository.interfaces;

import java.util.Optional;

public interface BaseRepository<T, ID> {

    T save(T entity);

    T update(T entity);

    Optional<T> findById(ID id);

    void remove(ID id);
}
