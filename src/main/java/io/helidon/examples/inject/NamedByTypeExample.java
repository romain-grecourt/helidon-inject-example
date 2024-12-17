package io.helidon.examples.inject;

import io.helidon.service.registry.ServiceRegistryManager;
import io.helidon.service.registry.Service;

/**
 * An example that illustrates usages of {@link Service.NamedByType}.
 */
class NamedByTypeExample {

    /**
     * A service to be implemented by named services.
     */
    interface Color {
        String hexCode();
    }

    /**
     * A named service.
     */
    @Service.NamedByType(Blue.class)
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
    @Service.NamedByType(Green.class)
    @Service.Singleton
    static class Green implements Color {

        @Override
        public String hexCode() {
            return "008000";
        }
    }

    /**
     * A service that qualifies the injection point using {@link Service.NamedByType}.
     *
     * @param color color
     */
    @Service.Singleton
    record BlueSquare(@Service.NamedByType(Blue.class) Color color) {
    }

    /**
     * A service that qualifies the injection point using {@link Service.NamedByType}.
     *
     * @param color color
     */
    @Service.Singleton
    record GreenSquare(@Service.NamedByType(Green.class) Color color) {
    }

    public static void main(String[] args) {
        var registry = ServiceRegistryManager.create().registry();
        var blueCircle = registry.get(BlueSquare.class);
        var greenCircle = registry.get(GreenSquare.class);

        System.out.printf("blue square color hex-code: %s%n", blueCircle.color().hexCode());
        System.out.printf("green square color hex-code: %s%n", greenCircle.color().hexCode());
    }
}
