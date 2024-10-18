package io.helidon.examples.inject;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import io.helidon.common.types.TypeName;
import io.helidon.service.inject.InjectRegistryManager;
import io.helidon.service.inject.api.Injection;
import io.helidon.service.inject.api.Scope;
import io.helidon.service.inject.api.Scopes;

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
    @Injection.NamedByType(MyScope.class)
    static class MyScopeControl implements Injection.ScopeHandler {

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
    @Injection.Singleton
    record MyService(Supplier<MyScopedService> contract) {
    }

    public static void main(String[] args) {
        var registry = InjectRegistryManager.create().registry();
        var myService = registry.get(MyService.class);

        var scopes = registry.get(Scopes.class);
        try(var ignored = scopes.createScope(MyScope.TYPE, "id", Map.of())) {
            System.out.println(myService.contract.get().sayHello());
        }
    }
}
