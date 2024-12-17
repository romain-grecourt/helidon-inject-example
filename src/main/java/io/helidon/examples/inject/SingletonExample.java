package io.helidon.examples.inject;

import io.helidon.service.registry.ServiceRegistryManager;
import io.helidon.service.registry.Service;

/**
 * An example that illustrates usages of {@link Service.Singleton}.
 */
class SingletonExample {

    /**
     * A singleton service.
     */
    @Service.Singleton
    static class MySingleton {
    }

    public static void main(String[] args) {
        var registry = ServiceRegistryManager.create().registry();
        var myService1 = registry.get(MySingleton.class);
        var myService2 = registry.get(MySingleton.class);

        System.out.printf("%s - %s%n", System.identityHashCode(myService1), System.identityHashCode(myService2));
    }
}
