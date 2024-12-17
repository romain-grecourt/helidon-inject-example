package io.helidon.examples.inject;

import java.util.Map;
import java.util.function.Supplier;

import io.helidon.service.registry.ServiceRegistryManager;
import io.helidon.service.registry.Service;
import io.helidon.service.registry.Scope;
import io.helidon.service.registry.Scopes;

/**
 * An example that illustrates usages of {@link Service.PerRequest}.
 */
class PerRequestExample {

    /**
     * A service in request scope.
     */
    @Service.PerRequest
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
    @Service.Singleton
    record MyService(Supplier<MyRequestScopeService> contract) {
    }

    public static void main(String[] args) {
        var registry = ServiceRegistryManager.create().registry();
        var myService = registry.get(MyService.class);
        var scopes = registry.get(Scopes.class);

        try (Scope ignored = scopes.createScope(Service.PerRequest.TYPE, "test-1", Map.of())) {
            System.out.println(myService.contract().get().sayHello());
        }
    }
}
