package io.github.enessaidtatli.config.usecase;

public interface UseCaseHandler<R, T extends UseCase> {
    R handle(T useCase);
}
