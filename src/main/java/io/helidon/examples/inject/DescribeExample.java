package io.helidon.examples.inject;

import io.helidon.service.inject.InjectConfig;
import io.helidon.service.inject.InjectRegistryManager;
import io.helidon.service.inject.api.Injection;
import io.helidon.service.registry.Service;

class DescribeExample {

    @Injection.Describe
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
        var injectConfig = InjectConfig.builder()
                .putContractInstance(MyContract.class, new MyContractImpl())
                .build();
        var registry = InjectRegistryManager.create(injectConfig).registry();

        var myContract = registry.get(MyContract.class);
        System.out.println(myContract.sayHello());
    }
}
