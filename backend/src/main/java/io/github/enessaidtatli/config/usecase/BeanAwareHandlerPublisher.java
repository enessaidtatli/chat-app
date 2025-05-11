package io.github.enessaidtatli.config.usecase;

import io.github.enessaidtatli.exception.SourceNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

@Component
public class BeanAwareHandlerPublisher {

    private static final Log log = LogFactory.getLog(BeanAwareHandlerPublisher.class);

    @SuppressWarnings("unchecked")
    public <R, T extends UseCase> R publish(Class<R> returnClass, T useCase){
        UseCaseHandler<R, T> handler = (UseCaseHandler<R, T>) UseCaseRegistry.INSTANCE.fetchUseCaseHandlerFromRegistry(useCase.getClass());
        validateUseCaseHandler(useCase, handler);
        return handler.handle(useCase);
    }

    @SuppressWarnings("unchecked")
    public <T extends UseCase> void publish(T useCase){
        VoidUseCaseHandler<T> handler = (VoidUseCaseHandler<T>) UseCaseRegistry.INSTANCE.fetchUseCaseHandlerFromRegistry(useCase.getClass());
        validateVoidUseCaseHandler(useCase, handler);
        handler.handle(useCase);
    }

    @SuppressWarnings("unchecked")
    public <R, T> R publish(T input){
        NoUseCaseHandler<R, T> handler = (NoUseCaseHandler<R, T>) UseCaseRegistry.INSTANCE.fetchNoUseCaseHandlerFromRegistry(input.getClass());
        validateNoUseCaseHandler(input, handler);
        return handler.handle(input);
    }

    private <T extends UseCase> void validateVoidUseCaseHandler(T useCase, VoidUseCaseHandler<T> handler) {
        if(handler == null){
            log.error("No handler found for use case = " + useCase.getClass().getSimpleName());
            throw new SourceNotFoundException("No handler found for use case = " + useCase.getClass().getSimpleName());
        }
    }

    private <T extends UseCase, R> void validateUseCaseHandler(T useCase, UseCaseHandler<R,T> handler) {
        if(handler == null){
            log.error("No handler found for use case = " + useCase.getClass().getSimpleName());
            throw new SourceNotFoundException("No handler found for use case = " + useCase.getClass().getSimpleName());
        }
    }

    private <R, T> void validateNoUseCaseHandler(T useCase, NoUseCaseHandler<R,T> handler) {
        if(handler == null){
            log.error("No handler found no use case = " + useCase.getClass().getSimpleName());
            throw new SourceNotFoundException("No handler found for no use case = " + useCase.getClass().getSimpleName());
        }
    }
}
