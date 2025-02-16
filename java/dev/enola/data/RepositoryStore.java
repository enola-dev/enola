package dev.enola.data;

public interface RepositoryStore<R extends RepositoryStore<R, T>, T>
        extends RepositoryRW<R, T>, Store<R, T> {}
