package io.helidon.examples.inject;

import io.helidon.service.registry.ServiceRegistryManager;
import io.helidon.service.registry.Service;

/**
 * An example that illustrates usages of {@link Service.Named}.
 */
class NamedExample {

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
     * A service that qualifies the injection point using {@link Service.Named}.
     *
     * @param color color
     */
    @Service.Singleton
    record BlueCircle(@Service.Named("blue") Color color) {
    }

    /**
     * A service that qualifies the injection point using {@link Service.Named}.
     *
     * @param color color
     */
    @Service.Singleton
    record GreenCircle(@Service.Named("green") Color color) {
    }

    public static void main(String[] args) {
        var registry = ServiceRegistryManager.create().registry();
        var blueCircle = registry.get(BlueCircle.class);
        var greenCircle = registry.get(GreenCircle.class);

        System.out.printf("blue circle color hex-code: %s%n", blueCircle.color().hexCode());
        System.out.printf("green circle color hex-code: %s%n", greenCircle.color().hexCode());
    }
}
