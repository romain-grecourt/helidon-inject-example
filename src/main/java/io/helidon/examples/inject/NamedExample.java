package io.helidon.examples.inject;

import io.helidon.service.inject.InjectRegistryManager;
import io.helidon.service.inject.api.Injection;

/**
 * An example that illustrates usages of {@link Injection.Named}.
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
     * A service that qualifies the injection point using {@link Injection.Named}.
     *
     * @param color color
     */
    @Injection.Singleton
    record BlueCircle(@Injection.Named("blue") Color color) {
    }

    /**
     * A service that qualifies the injection point using {@link Injection.Named}.
     *
     * @param color color
     */
    @Injection.Singleton
    record GreenCircle(@Injection.Named("green") Color color) {
    }

    public static void main(String[] args) {
        var registry = InjectRegistryManager.create().registry();
        var blueCircle = registry.get(BlueCircle.class);
        var greenCircle = registry.get(GreenCircle.class);

        System.out.printf("blue circle color hex-code: %s%n", blueCircle.color().hexCode());
        System.out.printf("green circle color hex-code: %s%n", greenCircle.color().hexCode());
    }
}
