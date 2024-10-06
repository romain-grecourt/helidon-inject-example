package io.helidon.examples.inject;

import java.util.function.Supplier;

import io.helidon.service.inject.InjectRegistryManager;
import io.helidon.service.inject.api.Injection;

/**
 * An example that illustrates usages of {@link Injection.PerLookup} without scope.
 */
class PerLookupExample {

    /**
     * A service with the default scope.
     */
    @Injection.PerLookup
    static class MyInstance {
    }

    /**
     * A singleton service.
     *
     * @param instance supplier of the service
     */
    @Injection.Singleton
    record MySingleton(Supplier<MyInstance> instance) {
    }

    public static void main(String[] args) {
        var registry = InjectRegistryManager.create().registry();
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
