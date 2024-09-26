package io.helidon.examples.inject;

import java.util.Map;
import java.util.function.Supplier;

import io.helidon.service.inject.InjectRegistryManager;
import io.helidon.service.inject.api.Injection;
import io.helidon.service.inject.api.RequestScopeControl;
import io.helidon.service.inject.api.Scope;

class RequestScopeExample {

    @Injection.RequestScope
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
        var scopeControl = registry.get(RequestScopeControl.class);

        try (Scope ignored = scopeControl.startRequestScope("test-1", Map.of())) {
            System.out.println(myService.contract().get().sayHello());
        }
    }
}
