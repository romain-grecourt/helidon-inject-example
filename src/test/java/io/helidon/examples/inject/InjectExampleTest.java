package io.helidon.examples.inject;

import java.util.List;
import java.util.Map;

import io.helidon.service.inject.InjectConfig;
import io.helidon.service.inject.InjectRegistryManager;
import io.helidon.service.inject.api.RequestScopeControl;
import io.helidon.service.inject.api.Scope;
import io.helidon.service.registry.ServiceDiscovery;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

class InjectExampleTest {

    @Test
    void testCreateFor() {
        var registry = InjectRegistryManager.create().registry();
        var circles = registry.get(CreateForExample.Circles.class);

        assertThat(circles.blue().name(), is("blue"));
        assertThat(circles.blue().color().hexCode(), is("0000FF"));
        assertThat(circles.green().name(), is("green"));
        assertThat(circles.green().color().hexCode(), is("008000"));
    }

    @Test
    void testDescribe() {
        var discovery = ServiceDiscovery.create();
        var injectConfig = InjectConfig.builder()
                .putServiceInstance(
                        discovery.descriptor(DescribeExample.MyContract.class).orElseThrow(),
                        new DescribeExample.MyContractImpl())
                .build();
        var registry = InjectRegistryManager.create(injectConfig, discovery).registry();
        var myService = registry.get(DescribeExample.MyService.class);

        assertThat(myService.myContract().sayHello(), is("Hello World!"));
    }

    @Test
    void testInstance() {
        var registry = InjectRegistryManager.create().registry();
        var myInstance1 = registry.get(InstanceExample.MyInstance.class);
        var myInstance2 = registry.get(InstanceExample.MyInstance.class);
        var mySingleton = registry.get(InstanceExample.MySingleton.class);

        assertThat(System.identityHashCode(myInstance1),
                is(not(System.identityHashCode(myInstance2))));

        assertThat(System.identityHashCode(mySingleton.instance().get()),
                is(not(System.identityHashCode(mySingleton.instance().get()))));
    }

    @Test
    void testInterceptor() {
        var registryManager = InjectRegistryManager.create();
        var registry = registryManager.registry();
        var myService = registry.get(InterceptorExample.MyService.class);
        var myContract = registry.get(InterceptorExample.MyContract.class);

        assertThat(myService.sayHello("Joe"), is("Hello Joe!"));
        assertThat(myService.sayHello("John"), is("Hello John!"));
        assertThat(myContract.sayHello("Julia"), is("Hello Julia!"));
        assertThat(myContract.sayHello("Jeanne"), is("Hello Jeanne!"));
        assertThat(InterceptorExample.MyServiceInterceptor.INVOKED, is(List.of(
                "%s.sayHello: Joe".formatted(InterceptorExample.MyService.class.getName()),
                "%s.sayHello: John".formatted(InterceptorExample.MyService.class.getName()),
                "%s.sayHello: Julia".formatted(InterceptorExample.MyContractSupplier.class.getName()),
                "%s.sayHello: Jeanne".formatted(InterceptorExample.MyContractSupplier.class.getName()))));
    }

    @Test
    void testNamedByClass() {
        var registry = InjectRegistryManager.create().registry();
        var blueCircle = registry.get(NamedByClassExample.BlueSquare.class);
        var greenCircle = registry.get(NamedByClassExample.GreenSquare.class);

        assertThat(blueCircle.color().hexCode(), is("0000FF"));
        assertThat(greenCircle.color().hexCode(), is("008000"));
    }

    @Test
    void testNamed() {
        var registry = InjectRegistryManager.create().registry();
        var blueCircle = registry.get(NamedExample.BlueCircle.class);
        var greenCircle = registry.get(NamedExample.GreenCircle.class);

        assertThat(blueCircle.color().hexCode(), is("0000FF"));
        assertThat(greenCircle.color().hexCode(), is("008000"));
    }

    @Test
    void testWeighted() {
        var registry = InjectRegistryManager.create().registry();
        var symbol = registry.get(WeightedExample.Symbol.class);

        assertThat(symbol.color().name(), is("green"));
        assertThat(symbol.shape().isPresent(), is(false));
    }

    @Test
    void testRequestScope() {
        var registry = InjectRegistryManager.create().registry();
        var myService = registry.get(RequestScopeExample.MyService.class);
        var scopeControl = registry.get(RequestScopeControl.class);

        try (Scope ignored = scopeControl.startRequestScope("test-1", Map.of())) {
            assertThat(myService.contract().get().sayHello(), is("Hello World!"));
        }
    }

    @Test
    void testScope() {
        var registry = InjectRegistryManager.create().registry();
        var myService = registry.get(ScopeExample.MyService.class);
        var scopeControl = registry.get(ScopeExample.MyScopeControl.class);

        try (Scope ignored = scopeControl.start("test-1", Map.of())) {
            assertThat(myService.contract().get().sayHello(), is("Hello World!"));
        }
    }
}
