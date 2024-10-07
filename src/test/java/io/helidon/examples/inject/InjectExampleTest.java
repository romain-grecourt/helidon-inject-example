package io.helidon.examples.inject;

import java.util.List;
import java.util.Map;

import io.helidon.service.inject.InjectConfig;
import io.helidon.service.inject.InjectRegistryManager;
import io.helidon.service.inject.api.PerRequestScopeControl;
import io.helidon.service.inject.api.Scope;

import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.ExpectedToFail;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

class InjectExampleTest {

    @Test
    void testCreateFor() {
        var registry = InjectRegistryManager.create().registry();
        var circles = registry.get(PerInstanceExample.Circles.class);

        assertThat(circles.blue().name(), is("blue"));
        assertThat(circles.blue().color().hexCode(), is("0000FF"));
        assertThat(circles.green().name(), is("green"));
        assertThat(circles.green().color().hexCode(), is("008000"));
    }

    @Test
    void testDescribe() {
        var injectConfig = InjectConfig.builder()
                .putContractInstance(DescribeExample.MyContract.class, new DescribeExample.MyContractImpl())
                .build();
        var registry = InjectRegistryManager.create(injectConfig).registry();

        var myContract = registry.get(DescribeExample.MyContract.class);
        assertThat(myContract.sayHello(), is("Hello World!"));
    }

    @Test
    void testInstance() {
        var registry = InjectRegistryManager.create().registry();
        var myInstance1 = registry.get(PerLookupExample.MyInstance.class);
        var myInstance2 = registry.get(PerLookupExample.MyInstance.class);
        var mySingleton = registry.get(PerLookupExample.MySingleton.class);

        assertThat(System.identityHashCode(myInstance1),
                   is(not(System.identityHashCode(myInstance2))));

        assertThat(System.identityHashCode(mySingleton.instance().get()),
                   is(not(System.identityHashCode(mySingleton.instance().get()))));
    }

    @Test
    void testInterceptor() {
        var registryManager = InjectRegistryManager.create();
        var registry = registryManager.registry();
        var myService = registry.get(InterceptorExample.MyConcreteService.class);
        var myIFaceContract = registry.get(InterceptorExample.MyIFaceContract.class);
        var myIFaceDelegatedContract = registry.get(InterceptorExample.MyIFaceDelegatedContract.class);
        var myIFaceProvidedContract = registry.get(InterceptorExample.MyIFaceProvidedContract.class);

        assertThat(myService.sayHello("Joe"), is("Hello Joe!"));
        assertThat(myService.sayHello("John"), is("Hello John!"));
        assertThat(myIFaceContract.sayHello("Julia"), is("Hello Julia!"));
        assertThat(myIFaceContract.sayHello("Jeanne"), is("Hello Jeanne!"));
        assertThat(myIFaceDelegatedContract.sayHello("Jessica"), is("Hello Jessica!"));
        assertThat(myIFaceDelegatedContract.sayHello("Juliet"), is("Hello Juliet!"));
        assertThat(myIFaceProvidedContract.sayHello("Jennifer"), is("Hello Jennifer!"));
        assertThat(myIFaceProvidedContract.sayHello("Josephine"), is("Hello Josephine!"));
        assertThat(InterceptorExample.MyServiceInterceptor.INVOKED, is(List.of(
                "%s.<init>: []".formatted(InterceptorExample.MyConcreteService.class.getName()),
                "%s.sayHello: [Joe]".formatted(InterceptorExample.MyConcreteService.class.getName()),
                "%s.sayHello: [John]".formatted(InterceptorExample.MyConcreteService.class.getName()),
                "%s.sayHello: [Julia]".formatted(InterceptorExample.MyIFaceContractImpl.class.getName()),
                "%s.sayHello: [Jeanne]".formatted(InterceptorExample.MyIFaceContractImpl.class.getName()),
                "%s.sayHello: [Jessica]".formatted(InterceptorExample.MyIFaceDelegatedContractImpl.class.getName()),
                "%s.sayHello: [Juliet]".formatted(InterceptorExample.MyIFaceDelegatedContractImpl.class.getName()),
                "%s.sayHello: [Jennifer]".formatted(InterceptorExample.MyIFaceProvidedContractSupplier.class.getName()),
                "%s.sayHello: [Josephine]".formatted(InterceptorExample.MyIFaceProvidedContractSupplier.class.getName()))));
    }

    @Test
    void testNamedByClass() {
        var registry = InjectRegistryManager.create().registry();
        var blueCircle = registry.get(NamedByTypeExample.BlueSquare.class);
        var greenCircle = registry.get(NamedByTypeExample.GreenSquare.class);

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
        var color = registry.get(WeightedExample.Color.class);

        assertThat(color.name(), is("green"));
    }

    @Test
    void testRequestScope() {
        var registry = InjectRegistryManager.create().registry();
        var myService = registry.get(PerRequestExample.MyService.class);
        var scopeControl = registry.get(PerRequestScopeControl.class);

        try (Scope ignored = scopeControl.startRequestScope("test-1", Map.of())) {
            assertThat(myService.contract().get().sayHello(), is("Hello World!"));
        }
    }

    @Test
    void testCustomScope() {
        var registry = InjectRegistryManager.create().registry();
        var myService = registry.get(CustomScopeExample.MyService.class);
        var scopeControl = registry.get(CustomScopeExample.MyScopeControl.class);

        try (Scope ignored = scopeControl.start("test-1", Map.of())) {
            assertThat(myService.contract().get().sayHello(), is("Hello World!"));
        }
    }

    @Test
    void testInjectionPoints() {
        var registry = InjectRegistryManager.create().registry();
        var greetings = registry.get(InjectionPointsExample.Greetings.class);

        assertThat(greetings.greet(), containsInAnyOrder(
                "%s: Hello Joe!".formatted(InjectionPointsExample.GreetingWithCyclicDep1.class.getSimpleName()),
                "%s: Hello Jack!".formatted(InjectionPointsExample.GreetingWithCyclicDep1.class.getSimpleName()),
                "%s: Hello Julia!".formatted(InjectionPointsExample.GreetingWithExplicitCtorInjection.class.getSimpleName()),
                "%s: Hello Jeanne!".formatted(InjectionPointsExample.GreetingWithFieldInjection.class.getSimpleName()),
                "%s: Hello Jessica!".formatted(InjectionPointsExample.GreetingWithImplicitCtorInjection.class.getSimpleName()),
                "%s: Hello Juliet!".formatted(InjectionPointsExample.GreetingWithInheritedFieldInjection.class.getSimpleName()),
                "%s: Hello Jennifer!".formatted(InjectionPointsExample.GreetingWithMethodInjection.class.getSimpleName()),
                "%s: Hello Josephine!".formatted(InjectionPointsExample.GreetingWithOptionalIP.class.getSimpleName()),
                "%s: Hello John!".formatted(InjectionPointsExample.GreetingWithRecord.class.getSimpleName()),
                "%s: Hello Jacqueline!".formatted(InjectionPointsExample.GreetingWithRecordCanonicalCtor.class.getSimpleName()).toUpperCase(),
                "%s: Hello Joe!".formatted(InjectionPointsExample.GreetingWithRecordCompactCtor.class.getSimpleName()),
                "%s: Hello Joe!!!".formatted(InjectionPointsExample.GreetingWithRecordCustomCtor.class.getSimpleName())
        ));
    }

    @Test
    void testExternalContract() {
        var registry = InjectRegistryManager.create().registry();
        var name = registry.get(CharSequence.class);
        assertThat(ExternalContractExample.RandomName.NAMES, hasItem(name.toString()));
    }

    @Test
    void testRunLevel() {
        var registry = InjectRegistryManager.create().registry();
        RunLevelExample.startRunLevels(registry);
        assertThat(RunLevelExample.STARTUP_EVENTS, hasItems("level1", "level2"));
    }

    @Test
    @ExpectedToFail
    void testGenerics() {
        var registry = InjectRegistryManager.create().registry();
        var myService = registry.get(GenericsExample.MyService.class);

        assertThat(myService.blueCircle().name(), is("blue circle"));
        assertThat(myService.greenCircle().name(), is("green circle"));
        assertThat(myService.circleNames(), is(List.of("blue circle", "green circle")));
    }
}
