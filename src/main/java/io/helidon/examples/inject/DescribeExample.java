package io.helidon.examples.inject;

import io.helidon.service.inject.InjectConfig;
import io.helidon.service.inject.InjectRegistryManager;
import io.helidon.service.inject.api.Injection;
import io.helidon.service.registry.Service;
import io.helidon.service.registry.ServiceDiscovery;

class DescribeExample {

    @Injection.Describe(Injection.Singleton.class)
    @Service.Contract
    interface MyContract {
        String sayHello();
    }

    static class MyContractImpl implements MyContract {

        @Override
        public String sayHello() {
            return "Hello World!";
        }
    }

    @Injection.Singleton
    record MyService(MyContract myContract) {
    }

    public static void main(String[] args) {
        var discovery = ServiceDiscovery.create();
        var injectConfig = InjectConfig.builder()
                .putServiceInstance(discovery.descriptor(MyContract.class).orElseThrow(), new MyContractImpl())
                .build();
        var registry = InjectRegistryManager.create(injectConfig, discovery).registry();
        var myService = registry.get(MyService.class);
        System.out.println(myService.myContract.sayHello());
    }
}
