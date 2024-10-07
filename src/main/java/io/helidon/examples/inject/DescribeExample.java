package io.helidon.examples.inject;

import io.helidon.service.inject.InjectConfig;
import io.helidon.service.inject.InjectRegistryManager;
import io.helidon.service.inject.api.Injection;
import io.helidon.service.registry.Service;

/**
 * An example that illustrates usages of {@link Injection.Describe}.
 */
class DescribeExample {

    /**
     * A service that needs to be described separately.
     */
    @Injection.Describe
    @Service.Contract
    interface MyContract {
        String sayHello();
    }

    /**
     * A non-service implementation of the contract.
     * It is instantiated manually and passed to the registry manager config.
     */
    static class MyContractImpl implements MyContract {

        @Override
        public String sayHello() {
            return "Hello World!";
        }
    }

    /**
     * A singleton service that injects the described contract.
     *
     * @param myContract myContract
     */
    @Injection.Singleton
    record MyService(MyContract myContract) {
    }

    public static void main(String[] args) {
        var injectConfig = InjectConfig.builder()
                // pass the non managed instance of the described contract
                .putContractInstance(MyContract.class, new MyContractImpl())
                .build();
        var registry = InjectRegistryManager.create(injectConfig).registry();

        var myContract = registry.get(MyContract.class);
        System.out.println(myContract.sayHello());
    }
}
