package io.helidon.examples.inject;

import io.helidon.service.registry.ServiceRegistryManager;
import io.helidon.service.registry.Service;

/**
 * An example that illustrates usages of {@link Service.PerInstance}.
 */
class PerInstanceExample {

    /**
     * A service to be implemented by named services.
     */
    interface Color {
        String hexCode();
    }

    /**
     * A named service.
     */
    @Service.Named("blue")
    @Service.Singleton
    static class Blue implements Color {

        @Override
        public String hexCode() {
            return "0000FF";
        }
    }

    /**
     * A named service.
     */
    @Service.Named("green")
    @Service.Singleton
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
    @Service.PerInstance(Color.class)
    record Circle(@Service.InstanceName String name, Color color) {
    }

    /**
     * A service that illustrates the inherited names.
     *
     * @param blue  blue circle
     * @param green green circle
     */
    @Service.Singleton
    record Circles(@Service.Named("blue") Circle blue,
                   @Service.Named("green") Circle green) {
    }

    public static void main(String[] args) {
        var registry = ServiceRegistryManager.create().registry();

        var circles = registry.get(Circles.class);

        System.out.printf("blue circle name: %s%n", circles.blue().name());
        System.out.printf("blue circle color hex-code: %s%n", circles.blue().color().hexCode());
        System.out.printf("green circle name: %s%n", circles.green().name());
        System.out.printf("green circle color hex-code: %s%n", circles.green().color().hexCode());
    }
}
