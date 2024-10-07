package io.helidon.examples.inject;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import io.helidon.common.types.TypeName;
import io.helidon.service.inject.InjectRegistryManager;
import io.helidon.service.inject.api.InjectRegistrySpi;
import io.helidon.service.inject.api.Injection;
import io.helidon.service.inject.api.Scope;
import io.helidon.service.registry.ServiceInfo;

/**
 * An example that illustrates usages of {@link Injection.Scope}.
 */
class CustomScopeExample {

    /**
     * A custom scope annotation.
     */
    @Injection.Scope
    public @interface MyScope {
        TypeName TYPE = TypeName.create(MyScope.class);
    }

    /**
     * A service that implements {@link Injection.ScopeHandler} to support {@link MyScope}.
     */
    @Injection.Singleton
    static class MyScopeControl implements Injection.ScopeHandler<MyScope> {

        private static final AtomicReference<Scope> CURRENT_SCOPE = new AtomicReference<>();

        private final InjectRegistrySpi registry;

        @Injection.Inject
        MyScopeControl(InjectRegistrySpi registry) {
            this.registry = registry;
        }

        Scope start(String id, Map<ServiceInfo, Object> bindings) {
            var scope = registry.createScope(MyScope.TYPE, id, bindings, sc -> CURRENT_SCOPE.set(null));
            CURRENT_SCOPE.set(scope);
            return scope;
        }

        @Override
        public Optional<Scope> currentScope() {
            return Optional.ofNullable(CURRENT_SCOPE.get());
        }
    }

    /**
     * A service that uses the custom scope.
     */
    @MyScope
    static class MyScopedService {

        String sayHello() {
            return "Hello World!";
        }
    }

    /**
     * A singleton service that consumes a service in the custom scope.
     *
     * @param contract contract supplier
     */
    @Injection.Singleton
    record MyService(Supplier<MyScopedService> contract) {
    }

    public static void main(String[] args) {
        var registry = InjectRegistryManager.create().registry();
        var myService = registry.get(MyService.class);

        InjectRegistrySpi spi = registry.get(InjectRegistrySpi.class);
        try(var ignored = spi.createScope(MyScope.TYPE, "id", Map.of())) {
            System.out.println(myService.contract.get().sayHello());
        }
    }
}
