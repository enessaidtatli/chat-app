package io.github.enessaidtatli.config.usecase;

public interface NoUseCaseHandler<R, T> {
    R handle(T input);
}
