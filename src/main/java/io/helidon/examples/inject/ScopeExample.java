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
import io.helidon.service.inject.api.ScopedRegistry;
import io.helidon.service.registry.ServiceInfo;

class ScopeExample {

    @Injection.Scope
    public @interface MyScope {
        TypeName TYPE = TypeName.create(MyScope.class);
    }

    @Injection.Singleton
    static class MyScopeControl implements Injection.ScopeHandler<MyScope> {

        private static final AtomicReference<Scope> CURRENT_SCOPE = new AtomicReference<>();

        private final InjectRegistrySpi registry;

        @Injection.Inject
        MyScopeControl(InjectRegistrySpi registry) {
            this.registry = registry;
        }

        Scope start(String id, Map<ServiceInfo, Object> bindings) {
            var services = registry.createForScope(MyScope.TYPE, id, bindings);
            var scope = new MyScopeImpl(services);
            CURRENT_SCOPE.set(scope);
            scope.services.activate();
            return scope;
        }

        @Override
        public Optional<Scope> currentScope() {
            return Optional.ofNullable(CURRENT_SCOPE.get());
        }

        private record MyScopeImpl(ScopedRegistry services) implements Scope {

            @Override
            public void close() {
                services.deactivate();
                CURRENT_SCOPE.set(null);
            }
        }
    }

    @MyScope
    @Injection.Instance
    static class MyContract {

        String sayHello() {
            return "Hello World!";
        }
    }

    @Injection.Singleton
    record MyService(Supplier<MyContract> contract) {
    }

    public static void main(String[] args) {
        var registry = InjectRegistryManager.create().registry();
        var myService = registry.get(MyService.class);
        var scopeControl = registry.get(MyScopeControl.class);

        try (Scope ignored = scopeControl.start("my-scope-1", Map.of())) {
            System.out.println(myService.contract.get().sayHello());
        }
    }
}
