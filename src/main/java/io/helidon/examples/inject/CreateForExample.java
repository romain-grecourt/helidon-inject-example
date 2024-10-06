package io.helidon.examples.inject;

import io.helidon.service.inject.InjectRegistryManager;
import io.helidon.service.inject.api.Injection;
import io.helidon.service.registry.Service;

/**
 * An example that illustrates usages of {@link Injection.PerInstance}.
 */
class CreateForExample {

    /**
     * A service to be implemented by named services.
     */
    @Service.Contract
    interface Color {
        String hexCode();
    }

    /**
     * A named service.
     */
    @Injection.Named("blue")
    @Injection.Singleton
    static class Blue implements Color {

        @Override
        public String hexCode() {
            return "0000FF";
        }
    }

    /**
     * A named service.
     */
    @Injection.Named("green")
    @Injection.Singleton
    static class Green implements Color {

        @Override
        public String hexCode() {
            return "008000";
        }
    }

    /**
     * A service that is created for each named instance of {@link Color}.
     *
     * @param name  the matched name
     * @param color the matched color
     */
    @Injection.PerInstance(Color.class)
    record Circle(@Injection.InstanceName String name, Color color) {
    }

    /**
     * A service that illustrates the inherited names.
     *
     * @param blue  blue circle
     * @param green green circle
     */
    @Injection.Singleton
    record Circles(@Injection.Named("blue") Circle blue,
                   @Injection.Named("green") Circle green) {
    }

    public static void main(String[] args) {
        var registry = InjectRegistryManager.create().registry();

        var circles = registry.get(Circles.class);

        System.out.printf("blue circle name: %s%n", circles.blue().name());
        System.out.printf("blue circle color hex-code: %s%n", circles.blue().color().hexCode());
        System.out.printf("green circle name: %s%n", circles.green().name());
        System.out.printf("green circle color hex-code: %s%n", circles.green().color().hexCode());
    }
}
