package io.helidon.examples.inject;

import java.util.function.Supplier;

import io.helidon.service.inject.InjectRegistryManager;
import io.helidon.service.inject.api.Injection;

class InstanceExample {

    @Injection.Instance
    static class MyInstance {
    }

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
