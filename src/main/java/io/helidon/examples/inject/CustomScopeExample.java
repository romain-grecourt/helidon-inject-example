package io.helidon.examples.inject;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import io.helidon.common.types.TypeName;
import io.helidon.service.registry.ServiceRegistryManager;
import io.helidon.service.registry.Service;
import io.helidon.service.registry.Scope;
import io.helidon.service.registry.Scopes;

/**
 * An example that illustrates usages of {@link Service.Scope}.
 */
class CustomScopeExample {

    /**
     * A custom scope annotation.
     */
    @Service.Scope
    public @interface MyScope {
        TypeName TYPE = TypeName.create(MyScope.class);
    }

    /**
     * A service that implements {@link Service.ScopeHandler} to support {@link MyScope}.
     */
    @Service.Singleton
    @Service.NamedByType(MyScope.class)
    static class MyScopeControl implements Service.ScopeHandler {

        private final AtomicReference<Scope> currentScope = new AtomicReference<>();

        @Override
        public Optional<Scope> currentScope() {
            return Optional.ofNullable(currentScope.get());
        }

        @Override
        public void activate(Scope scope) {
            if (!currentScope.compareAndSet(null, scope)) {
                throw new IllegalStateException("Scope already set");
            }
            scope.registry().activate();
        }

        @Override
        public void deactivate(Scope scope) {
            if (!currentScope.compareAndSet(scope, null)) {
                throw new IllegalStateException("Scope mismatch");
            }
            scope.registry().deactivate();
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
    @Service.Singleton
    record MyService(Supplier<MyScopedService> contract) {
    }

    public static void main(String[] args) {
        var registry = ServiceRegistryManager.create().registry();
        var myService = registry.get(MyService.class);

        var scopes = registry.get(Scopes.class);
        try(var ignored = scopes.createScope(MyScope.TYPE, "id", Map.of())) {
            System.out.println(myService.contract.get().sayHello());
        }
    }
}
