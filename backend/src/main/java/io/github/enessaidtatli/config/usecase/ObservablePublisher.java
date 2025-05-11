package io.github.enessaidtatli.config.usecase;

public class ObservablePublisher extends BeanAwareHandlerPublisher {

    public <R, T extends UseCase> void register(Class<T> useCase, UseCaseHandler<R, T> handler){
        UseCaseRegistry.INSTANCE.registerForUseCaseHandler(useCase, handler);
    }

    public <T extends UseCase> void register(Class<T> useCase, VoidUseCaseHandler<T> handler){
        UseCaseRegistry.INSTANCE.registerForVoidUseCaseHandler(useCase, handler);
    }

    public <R, T> void register(Class<T> useCase, NoUseCaseHandler<R, T> handler){
        UseCaseRegistry.INSTANCE.registerForNoUseCaseHandler(useCase, handler);
    }

}
