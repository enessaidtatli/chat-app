package io.github.enessaidtatli.config.usecase;

public interface VoidUseCaseHandler<T extends UseCase> {
    void handle(T useCase);
}
