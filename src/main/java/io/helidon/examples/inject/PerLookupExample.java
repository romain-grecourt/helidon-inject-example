package io.helidon.examples.inject;

import java.util.function.Supplier;

import io.helidon.service.registry.ServiceRegistryManager;
import io.helidon.service.registry.Service;

/**
 * An example that illustrates usages of {@link Service.PerLookup} without scope.
 */
class PerLookupExample {

    /**
     * A service with the per-lookup scope.
     */
    @Service.PerLookup
    static class MyInstance {
    }

    /**
     * A singleton service.
     *
     * @param instance supplier of the service
     */
    @Service.Singleton
    record MySingleton(Supplier<MyInstance> instance) {
    }

    public static void main(String[] args) {
        var registry = ServiceRegistryManager.create().registry();
        var myInstance1 = registry.get(MyInstance.class);
        var myInstance2 = registry.get(MyInstance.class);

        System.out.printf("%s - %s%n",
                System.identityHashCode(myInstance1),
                System.identityHashCode(myInstance2));

        var mySingleton = registry.get(MySingleton.class);

        System.out.printf("%s - %s%n",
                System.identityHashCode(mySingleton.instance().get()),
                System.identityHashCode(mySingleton.instance().get()));
    }
}
