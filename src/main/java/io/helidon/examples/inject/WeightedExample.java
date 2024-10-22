package io.helidon.examples.inject;

import io.helidon.common.Weight;
import io.helidon.service.inject.InjectRegistryManager;
import io.helidon.service.inject.api.Injection;

/**
 * An example that illustrates usages of {@link Weight} to order services.
 */
class WeightedExample {

    /**
     * A contract to be implemented by weighted services.
     */
    interface Color {
        String name();
    }

    /**
     * A weighted service.
     */
    @Weight(1)
    @Injection.Singleton
    static class Blue implements Color {

        @Override
        public String name() {
            return "blue";
        }
    }

    /**
     * A weighted service.
     */
    @Weight(2)
    @Injection.Singleton
    static class Green implements Color {
        @Override
        public String name() {
            return "green";
        }
    }

    public static void main(String[] args) {
        var registry = InjectRegistryManager.create().registry();
        var color = registry.get(Color.class);

        System.out.printf("color name: %s%n", color.name());
    }
}
