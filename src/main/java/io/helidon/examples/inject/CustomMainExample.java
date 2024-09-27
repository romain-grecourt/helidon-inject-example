package io.helidon.examples.inject;

import java.util.concurrent.atomic.AtomicReference;

import io.helidon.service.inject.api.Injection;
import io.helidon.service.inject.InjectConfig;
import io.helidon.service.inject.InjectionMain;
import io.helidon.service.registry.Service;

/**
 * An example that illustrates usages of {@link InjectionMain}.
 */
class CustomMainExample extends InjectionMain {

    /**
     * A described greeting to justify the usage of {@link #serviceDescriptors(InjectConfig.Builder)},
     */
    @Injection.Describe
    @Service.Contract
    interface Greeting {
        String sayHello();
    }

    /**
     * A non-service implementation of the described greeting.
     *
     * @param name name
     */
    record GreetingImpl(String name) implements Greeting {

        @Override
        public String sayHello() {
            return "Hello %s!".formatted(name);
        }
    }

    /**
     * Illustrates a successful startup.
     */
    static final AtomicReference<String> GREETING = new AtomicReference<>();

    /**
     * A run level service to act as an entry point.
     *
     * @param greeting greeting
     */
    @Injection.Singleton
    @Injection.RunLevel(Injection.RunLevel.STARTUP)
    record MyStartupService(Greeting greeting) {

        @Service.PostConstruct
        void postConstruct() {
            GREETING.set(greeting.sayHello());
        }
    }

    @Override
    protected boolean discoverServices() {
        return true;
    }

    @Override
    protected void serviceDescriptors(InjectConfig.Builder builder) {
        builder.putContractInstance(Greeting.class, new GreetingImpl("World"));
    }

    static void main0() {
        new CustomMainExample().start(new String[0]);
    }

    public static void main(String[] args) {
        main0();
        System.out.println(GREETING.get());
    }
}
