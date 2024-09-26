package io.helidon.examples.inject;

import io.helidon.service.inject.InjectRegistryManager;
import io.helidon.service.inject.api.Injection;

class SingletonExample {

    @Injection.Singleton
    static class MySingleton {
    }

    public static void main(String[] args) {
        var registry = InjectRegistryManager.create().registry();
        var myService1 = registry.get(MySingleton.class);
        var myService2 = registry.get(MySingleton.class);

        System.out.printf("%s - %s", System.identityHashCode(myService1), System.identityHashCode(myService2));
    }
}
