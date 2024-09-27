package io.helidon.examples.inject;

import io.helidon.service.inject.InjectRegistryManager;
import io.helidon.service.inject.api.Injection;
import io.helidon.service.registry.Service;

/**
 * An example that illustrates usages of {@link Injection.Qualifier}.
 */
class CustomQualifierExample {

    /**
     * A contract to be implemented by qualified services.
     */
    @Service.Contract
    interface Color {
        String name();
    }

    /**
     * A custom qualifier annotation.
     */
    @Injection.Qualifier
    public @interface HexCode {
        String value();
    }

    /**
     * A qualified service.
     */
    @HexCode("0000FF")
    @Injection.Singleton
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
    @Injection.Singleton
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
    @Injection.Singleton
    record BlueCircle(@HexCode("0000FF") Color color) {
    }

    /**
     * A service that injects using the custom qualifier.
     *
     * @param color color
     */
    @Injection.Singleton
    record GreenCircle(@HexCode("008000") Color color) {
    }

    public static void main(String[] args) {
        var registry = InjectRegistryManager.create().registry();
        var blueCircle = registry.get(BlueCircle.class);
        var greenCircle = registry.get(GreenCircle.class);

        System.out.printf("blue circle color name: %s%n", blueCircle.color().name());
        System.out.printf("green circle color name: %s%n", greenCircle.color().name());
    }
}
