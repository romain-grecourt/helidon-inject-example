package io.helidon.examples.inject;

import io.helidon.service.registry.Service;
import io.helidon.service.registry.ServiceRegistryConfig;
import io.helidon.service.registry.ServiceRegistryManager;

/**
 * An example that illustrates usages of {@link Service.Describe}.
 */
class DescribeExample {

    public static void main(String[] args) {
        var injectConfig = ServiceRegistryConfig.builder()
                // pass the non managed instance of the described contract
                .putContractInstance(MyContract.class, new MyContractImpl())
                .build();
        var registry = ServiceRegistryManager.create(injectConfig).registry();

        var myContract = registry.get(MyContract.class);
        System.out.println(myContract.sayHello());
    }

    /**
     * A service that needs to be described separately.
     */
    @Service.Describe
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
    @Service.Singleton
    record MyService(MyContract myContract) {
    }
}
