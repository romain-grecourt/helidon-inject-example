package io.helidon.examples.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.helidon.service.inject.InjectRegistryManager;
import io.helidon.service.inject.api.GeneratedInjectService;
import io.helidon.service.inject.api.Injection;
import io.helidon.service.inject.api.Interception;
import io.helidon.service.inject.api.InvocationContext;
import io.helidon.service.registry.Service;

class InterceptorExample {

    @Interception.Trigger
    @Target(ElementType.METHOD)
    @interface Intercepted {
    }

    @Injection.Singleton
    static class MyService {

        @Intercepted
        String sayHello(String name) {
            return "Hello %s!".formatted(name);
        }
    }

    @Injection.Singleton
    @Injection.NamedByClass(Intercepted.class)
    static class MyServiceInterceptor implements Interception.Interceptor {
        static final List<String> INVOKED = new ArrayList<>();

        @Override
        public <V> V proceed(InvocationContext ctx, Chain<V> chain, Object... args) throws Exception {
            INVOKED.add("%s.%s: %s".formatted(
                    ctx.serviceInfo().serviceType().declaredName(),
                    ctx.elementInfo().elementName(),
                    Arrays.stream(args)
                            .map(Object::toString)
                            .collect(Collectors.joining(","))));
            return chain.proceed(args);
        }
    }

    @Interception.Delegate
    @Service.Contract
    interface MyContract {

        @Intercepted
        String sayHello(String name);
    }

    @Injection.Singleton
    static class MyContractSupplier implements Supplier<MyContract> {

        private final GeneratedInjectService.InterceptionMetadata interceptMeta;

        @Injection.Inject
        MyContractSupplier(GeneratedInjectService.InterceptionMetadata interceptMeta) {
            this.interceptMeta = interceptMeta;
        }

        private MyContract wrap(MyContract delegate) {
            return InterceptorExample_MyContract__InterceptedDelegate.create(interceptMeta,
                    InterceptorExample_MyContractSupplier__ServiceDescriptor.INSTANCE,
                    delegate);
        }

        @Override
        public MyContract get() {
            return wrap("Hello %s!"::formatted);
        }
    }

    public static void main(String[] args) {
        var registry = InjectRegistryManager.create().registry();
        var myService = registry.get(MyService.class);
        var myContract = registry.get(MyContract.class);

        System.out.println(myService.sayHello("Joe"));
        System.out.println(myService.sayHello("Jack"));
        System.out.println(myContract.sayHello("Julia"));
        System.out.println(myContract.sayHello("Jeanne"));
        MyServiceInterceptor.INVOKED.forEach(System.out::println);
    }
}
