package io.helidon.examples.inject;

import io.helidon.service.registry.ServiceRegistryManager;
import io.helidon.service.registry.Service;

/**
 * An example that illustrates usages of {@link Service.Qualifier}.
 */
class CustomQualifierExample {

    /**
     * A service to be implemented by qualified services.
     */
    interface Color {
        String name();
    }

    /**
     * A custom qualifier annotation.
     */
    @Service.Qualifier
    public @interface HexCode {
        String value();
    }

    /**
     * A qualified service.
     */
    @HexCode("0000FF")
    @Service.Singleton
    static class Blue implements Color {

        @Override
        public String name() {
            return "blue";
        }
    }

    /**
     * A qualified service.
     */
    @HexCode("008000")
    @Service.Singleton
    static class Green implements Color {

        @Override
        public String name() {
            return "green";
        }
    }

    /**
     * A service that injects using the custom qualifier.
     *
     * @param color color
     */
    @Service.Singleton
    record BlueCircle(@HexCode("0000FF") Color color) {
    }

    /**
     * A service that injects using the custom qualifier.
     *
     * @param color color
     */
    @Service.Singleton
    record GreenCircle(@HexCode("008000") Color color) {
    }

    public static void main(String[] args) {
        var registry = ServiceRegistryManager.create().registry();
        var blueCircle = registry.get(BlueCircle.class);
        var greenCircle = registry.get(GreenCircle.class);

        System.out.printf("blue circle color name: %s%n", blueCircle.color().name());
        System.out.printf("green circle color name: %s%n", greenCircle.color().name());
    }
}
