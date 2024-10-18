package io.helidon.examples.inject;

import java.util.Map;
import java.util.function.Supplier;

import io.helidon.service.inject.InjectRegistryManager;
import io.helidon.service.inject.api.Injection;
import io.helidon.service.inject.api.Scope;
import io.helidon.service.inject.api.Scopes;

/**
 * An example that illustrates usages of {@link Injection.PerRequest}.
 */
class PerRequestExample {

    /**
     * A service in request scope.
     */
    @Injection.PerRequest
    static class MyRequestScopeService {

        String sayHello() {
            return "Hello World!";
        }
    }

    /**
     * A singleton service that consumes a service in request scope.
     *
     * @param contract request scope supplier
     */
    @Injection.Singleton
    record MyService(Supplier<MyRequestScopeService> contract) {
    }

    public static void main(String[] args) {
        var registry = InjectRegistryManager.create().registry();
        var myService = registry.get(MyService.class);
        var scopes = registry.get(Scopes.class);

        try (Scope ignored = scopes.createScope(Injection.PerRequest.TYPE, "test-1", Map.of())) {
            System.out.println(myService.contract().get().sayHello());
        }
    }
}
