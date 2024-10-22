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
    interface MyContract {

        @Traced
        String sayHello(String name);
    }

    /**
     * An abstract class contract with an intercepted method.
     */
    @Service.Contract
    static abstract class MyAbstractClassContract {

        @Traced
        abstract String sayHello(String name);

        @Traced
        String sayHelloDirect(String name) {
            return "Hello %s!".formatted(name);
        }
    }

    /**
     * An abstract class contract with an intercepted method.
     */
    @Service.Contract
    @Interception.Delegate
    static abstract class MyOtherAbstractClassContract {

        @Traced
        abstract String sayHello(String name);
    }

    /**
     * Another contract with an intercepted method.
     */
    @Service.Contract
    interface MyOtherContract {

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
     * A singleton service with an intercepted constructor and an intercepted method.
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
    static class MyContractImpl implements MyContract {

        @Override
        public String sayHello(String name) {
            return "Hello %s!".formatted(name);
        }
    }

    /**
     * A service that extends an abstract class contract with an intercepted method.
     */
    @Injection.Singleton
    static class MyAbstractClassContractImpl extends MyAbstractClassContract {

        @Override
        public String sayHello(String name) {
            return "Hello %s!".formatted(name);
        }
    }

    /**
     * A service that implements a provider of a contract with an intercepted method.
     */
    @Injection.Singleton
    static class MyContractProvider implements Supplier<MyOtherContract> {
        @Override
        public MyOtherContract get() {
            return "Hello %s!"::formatted;
        }
    }

    @Injection.Singleton
    static class MyAbstractContractProvider implements Supplier<MyOtherAbstractClassContract> {

        @Override
        public MyOtherAbstractClassContract get() {
            return new MyOtherAbstractClassContract(){

                @Override
                String sayHello(String name) {
                    return "Hello %s!".formatted(name);
                }
            };
        }
    }

    public static void main(String[] args) {
        var registry = InjectRegistryManager.create().registry();
        var myService = registry.get(MyConcreteService.class);
        var myIFaceContract = registry.get(MyContract.class);
        var myAbstractClassContract = registry.get(MyAbstractClassContract.class);
        var myIFaceProvidedContract = registry.get(MyOtherContract.class);
        var myAbstractClassProvidedContract = registry.get(MyOtherAbstractClassContract.class);

        System.out.println(myService.sayHello("Joe"));
        System.out.println(myService.sayHello("Jack"));
        System.out.println(myIFaceContract.sayHello("Julia"));
        System.out.println(myIFaceContract.sayHello("Jeanne"));
        System.out.println(myAbstractClassContract.sayHello("Jessica"));
        System.out.println(myAbstractClassContract.sayHello("Juliet"));
        System.out.println(myIFaceProvidedContract.sayHello("Jennifer"));
        System.out.println(myIFaceProvidedContract.sayHello("Josephine"));
        System.out.println(myAbstractClassProvidedContract.sayHello("Joceline"));
        System.out.println(myAbstractClassProvidedContract.sayHello("Jacqueline"));
        MyServiceInterceptor.INVOKED.forEach(System.out::println);
    }
}
