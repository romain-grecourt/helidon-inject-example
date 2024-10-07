package io.helidon.examples.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import io.helidon.service.inject.InjectRegistryManager;
import io.helidon.service.inject.api.Injection;
import io.helidon.service.inject.api.Interception;
import io.helidon.service.inject.api.InterceptionContext;
import io.helidon.service.registry.Service;

/**
 * An example that illustrates usages of {@link Interception.Interceptor}.
 */
class InterceptorExample {

    /**
     * An annotation to mark methods to be intercepted.
     */
    @Interception.Intercepted
    @Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
    @interface Traced {
    }

    /**
     * A contract with an intercepted method.
     */
    @Service.Contract
    interface MyIFaceContract {

        @Traced
        String sayHello(String name);
    }

    /**
     * A contract that is intercepted by delegation.
     */
    @Interception.Delegate
    @Service.Contract
    interface MyIFaceDelegatedContract {

        @Traced
        String sayHello(String name);
    }

    /**
     * Another contract with methods intercepted by delegation.
     */
    @Interception.Delegate
    @Service.Contract
    interface MyIFaceProvidedContract {

        @Traced
        String sayHello(String name);
    }

    /**
     * An interceptor implementation that supports {@link Traced}.
     */
    @Injection.Singleton
    @Injection.NamedByType(Traced.class)
    static class MyServiceInterceptor implements Interception.Interceptor {
        static final List<String> INVOKED = new ArrayList<>();

        @Override
        public <V> V proceed(InterceptionContext ctx, Chain<V> chain, Object... args) throws Exception {
            INVOKED.add("%s.%s: %s".formatted(
                    ctx.serviceInfo().serviceType().declaredName(),
                    ctx.elementInfo().elementName(),
                    Arrays.asList(args)));
            return chain.proceed(args);
        }
    }

    /**
     * A singleton service with an intercepted constructed and an intercepted method.
     */
    @Injection.Singleton
    static class MyConcreteService {

        @Traced
        MyConcreteService() {
        }

        @Traced
        String sayHello(String name) {
            return "Hello %s!".formatted(name);
        }
    }

    /**
     * A service that implements a contract with intercepted methods.
     */
    @Injection.Singleton
    static class MyIFaceContractImpl implements MyIFaceContract {

        @Override
        public String sayHello(String name) {
            return "Hello %s!".formatted(name);
        }
    }

    /**
     * A service that implements a contract with methods intercepted by delegation.
     */
    @Injection.Singleton
    static class MyIFaceDelegatedContractImpl implements MyIFaceDelegatedContract {

        @Override
        public String sayHello(String name) {
            return "Hello %s!".formatted(name);
        }
    }

    /**
     * A service that implements a provider of a contract with methods intercepted by delegation.
     */
    @Injection.Singleton
    static class MyIFaceProvidedContractSupplier implements Supplier<MyIFaceProvidedContract> {
        @Override
        public MyIFaceProvidedContract get() {
            return "Hello %s!"::formatted;
        }
    }

    public static void main(String[] args) {
        var registry = InjectRegistryManager.create().registry();
        var myService = registry.get(MyConcreteService.class);
        var myIFaceContract = registry.get(MyIFaceContract.class);
        var myIFaceDelegatedContract = registry.get(MyIFaceDelegatedContract.class);
        var myIFaceProvidedContract = registry.get(MyIFaceProvidedContract.class);

        System.out.println(myService.sayHello("Joe"));
        System.out.println(myService.sayHello("Jack"));
        System.out.println(myIFaceContract.sayHello("Julia"));
        System.out.println(myIFaceContract.sayHello("Jeanne"));
        System.out.println(myIFaceDelegatedContract.sayHello("Jessica"));
        System.out.println(myIFaceDelegatedContract.sayHello("Juliet"));
        System.out.println(myIFaceProvidedContract.sayHello("Jennifer"));
        System.out.println(myIFaceProvidedContract.sayHello("Josephine"));
        MyServiceInterceptor.INVOKED.forEach(System.out::println);
    }
}
