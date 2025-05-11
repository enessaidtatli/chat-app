package io.github.enessaidtatli.config.usecase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

public class UseCaseRegistry {

    private static final Log log = LogFactory.getLog(UseCaseRegistry.class);

    Map<Class<? extends UseCase> , UseCaseHandler<?, ? extends UseCase>> registryForUseCaseHandler;
    Map<Class<?>, NoUseCaseHandler<?, ?>> registryForNoUseCaseHandler;
    Map<Class<? extends UseCase>, VoidUseCaseHandler<? extends UseCase>> registryForVoidCaseHandler;

    public static UseCaseRegistry INSTANCE = new UseCaseRegistry();

    private UseCaseRegistry(){
        registryForUseCaseHandler = new HashMap<>();
        registryForNoUseCaseHandler = new HashMap<>();
        registryForVoidCaseHandler = new HashMap<>();
    }

    public <R, T extends UseCase> void registerForUseCaseHandler(Class<T> key, UseCaseHandler<R, T> handler){
        registryForUseCaseHandler.put(key, handler);
        log.info("Use case = " + key.getSimpleName() + " with handler = " + handler.getClass().getSimpleName() + " registered successfully !");
    }

    public <T extends UseCase> void registerForVoidUseCaseHandler(Class<T> key, VoidUseCaseHandler<T> handler){
        registryForVoidCaseHandler.put(key, handler);
        log.info("Use case = " + key.getSimpleName() + " with handler = " + handler.getClass().getSimpleName() + " registered successfully !");
    }

    public <R, T> void registerForNoUseCaseHandler(Class<T> key, NoUseCaseHandler<R, T> handler){
        registryForNoUseCaseHandler.put(key, handler);
        log.info("No use case = " + key.getSimpleName() + " with handler = " + handler.getClass().getSimpleName() + " registered successfully !");
    }

    public UseCaseHandler<?, ? extends UseCase> fetchUseCaseHandlerFromRegistry(Class<? extends UseCase> key){
        UseCaseHandler<?, ? extends UseCase> handler = registryForUseCaseHandler.get(key);
        log.info("Use case handler = " + handler.getClass().getSimpleName() + " with use case = " + key.getSimpleName() + " fetched successfully !");
        return handler;
    }

    public VoidUseCaseHandler<? extends UseCase> fetchVoidUseCaseHandlerFromRegistry(Class<? extends UseCase> key){
        VoidUseCaseHandler<? extends UseCase> handler = registryForVoidCaseHandler.get(key);
        log.info("Use case handler = " + handler.getClass().getSimpleName() + " with use case = " + key.getSimpleName() + " fetched successfully !");
        return handler;
    }

    public NoUseCaseHandler<?, ?> fetchNoUseCaseHandlerFromRegistry(Class<?> key){
        NoUseCaseHandler<?, ?> handler = registryForNoUseCaseHandler.get(key);
        log.info("No use case handler = " + handler.getClass().getSimpleName() + " with no use case = " + key.getSimpleName() + " fetched successfully !");
        return handler;
    }

}
