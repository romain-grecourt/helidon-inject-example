package io.helidon.examples.inject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import io.helidon.service.inject.InjectRegistryManager;
import io.helidon.service.inject.api.Injection;
import io.helidon.service.registry.Service;

/**
 * An example that illustrates usages of {@link Injection.Inject}.
 */
class InjectionPointsExample {

    /**
     * A service to be injected.
     */
    @Injection.Instance
    static class Greeter {

        Function<String, String> filter = Function.identity();

        /**
         * Set the filter.
         *
         * @param filter filter
         * @return this instance
         */
        Greeter filter(Function<String, String> filter) {
            this.filter = filter;
            return this;
        }

        /**
         * Produce a greeting.
         *
         * @param prefix prefix
         * @param name   name
         * @return greeting
         */
        String greet(String prefix, String name) {
            return filter.apply("%s: Hello %s!".formatted(prefix, name));
        }
    }

    /**
     * A greeting to be implemented by all variations.
     */
    @Service.Contract
    interface Greeting {

        /**
         * Get the greeter.
         *
         * @return greeter
         */
        Greeter greeter();

        /**
         * Greet someone.
         *
         * @param name name
         * @return greeting
         */
        default String greet(String name) {
            return greeter().greet(getClass().getSimpleName(), name);
        }
    }

    /**
     * A service that uses constructor injection.
     * There is only one constructor, thus {@link Injection.Inject} is optional.
     */
    @Injection.Singleton
    static class GreetingWithImplicitCtorInjection implements Greeting {

        private final Greeter greeter;

        GreetingWithImplicitCtorInjection(Greeter greeter) {
            this.greeter = greeter;
        }

        @Override
        public Greeter greeter() {
            return greeter;
        }
    }

    /**
     * A service multiple constructors that uses constructor injection.
     * {@link Injection.Inject} is required.
     */
    @Injection.Singleton
    static class GreetingWithExplicitCtorInjection implements Greeting {

        private final Greeter greeter;

        @Injection.Inject
        GreetingWithExplicitCtorInjection(Greeter greeter) {
            this.greeter = greeter;
        }

        @SuppressWarnings("unused")
        GreetingWithExplicitCtorInjection() {
            this(new Greeter());
        }

        @Override
        public Greeter greeter() {
            return greeter;
        }
    }

    /**
     * A service that uses field injection.
     * The visibility of the field must be at minimum package private.
     */
    @Injection.Singleton
    static class GreetingWithFieldInjection implements Greeting {

        @Injection.Inject
        protected Greeter greeter;

        @Override
        public Greeter greeter() {
            return greeter;
        }
    }

    /**
     * A service that uses method injection.
     * The visibility of the method must be at minimum package private.
     */
    @Injection.Singleton
    static class GreetingWithMethodInjection implements Greeting {

        private Greeter greeter;

        @Injection.Inject
        void setGreeter(Greeter greeter) {
            this.greeter = greeter;
        }

        @Override
        public Greeter greeter() {
            return greeter;
        }
    }

    /**
     * A service that extends a base class with field injection.
     */
    @Injection.Singleton
    static class GreetingWithInheritedFieldInjection extends GreetingWithFieldInjection {
    }

    /**
     * A service as a record with constructor injection.
     *
     * @param greeter greeter
     */
    @Injection.Singleton
    record GreetingWithRecord(Greeter greeter) implements Greeting {
    }

    /**
     * A service as a record with a compact constructor.
     * {@link Injection.Inject} is optional.
     *
     * @param greeter greeter
     */
    @Injection.Singleton
    record GreetingWithRecordCompactCtor(Greeter greeter) implements Greeting {

        @Injection.Inject
        GreetingWithRecordCompactCtor {
        }
    }

    /**
     * A service as a record with a canonical constructor.
     * The greeter filter is updated to illustrate the need for a canonical constructor.
     *
     * @param greeter greeter
     */
    @Injection.Singleton
    record GreetingWithRecordCanonicalCtor(Greeter greeter) implements Greeting {

        @Injection.Inject
        GreetingWithRecordCanonicalCtor(Greeter greeter) {
            this.greeter = greeter.filter(String::toUpperCase);
        }
    }

    /**
     * A service as a record with a custom constructor.
     * {@link Injection.Inject} is required.
     *
     * @param greeter greeter
     * @param suffix  a suffix to illustrate the need for a custom constructor
     */
    @Injection.Singleton
    record GreetingWithRecordCustomCtor(Greeter greeter, String suffix) implements Greeting {

        @Injection.Inject
        GreetingWithRecordCustomCtor(Greeter greeter) {
            this(greeter, "!!");
        }

        @Override
        public String greet(String name) {
            return Greeting.super.greet(name) + suffix;
        }
    }

    /**
     * A service that uses an {@link Optional optional} as an injection point.
     * A non-existing qualifier is used to illustrate what happens when the resolution fails.
     *
     * @param optionalGreeter optional greeter
     */
    @Injection.Singleton
    record GreetingWithOptionalIP(@Injection.Named("non-existing") Optional<Greeter> optionalGreeter) implements Greeting {

        @Override
        public Greeter greeter() {
            return optionalGreeter.orElseGet(Greeter::new);
        }
    }

    /**
     * A service that uses a {@link List list} as an injection point.
     *
     * @param greetings all the resolved greetings
     */
    @Injection.Singleton
    record Greetings(List<Greeting> greetings) {

        public static final List<String> NAMES = List.of(
                "Joe", "Jack", "Julia", "Jeanne", "Jessica",
                "Juliet", "Jennifer", "Josephine", "John", "Jacqueline");

        List<String> greet() {
            var result = new ArrayList<String>();
            var it = greetings.listIterator();
            var index = 0;
            while (it.hasNext()) {
                if (it.nextIndex() >= NAMES.size()) {
                    index = 0;
                }
                result.add(it.next().greet(NAMES.get(index++)));
            }
            return result;
        }
    }

    /**
     * A service with a cyclic dependency that broken-up using a {@link Supplier}.
     *
     * @param dependency dependency
     */
    @Injection.Singleton
    record GreetingWithCyclicDep1(Supplier<GreetingWithCyclicDep2> dependency) implements Greeting {

        @Override
        public Greeter greeter() {
            return dependency.get().greeter();
        }
    }

    /**
     * A service with a cyclic dependency.
     *
     * @param dependency dependency
     * @param greeter    greeter
     */
    @Injection.Singleton
    record GreetingWithCyclicDep2(GreetingWithCyclicDep1 dependency, Greeter greeter) implements Greeting {

        @Override
        public String greet(String name) {
            return dependency.greet(name);
        }
    }

    public static void main(String[] args) {
        var registry = InjectRegistryManager.create().registry();
        var greetings = registry.get(Greetings.class);

        greetings.greet().forEach(System.out::println);
    }
}
