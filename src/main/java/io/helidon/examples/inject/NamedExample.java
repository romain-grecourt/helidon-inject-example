package io.helidon.examples.inject;

import io.helidon.service.inject.InjectRegistryManager;
import io.helidon.service.inject.api.Injection;
import io.helidon.service.registry.Service;

class NamedExample {

    @Service.Contract
    interface Color {
        String hexCode();
    }

    @Injection.Named("blue")
    @Injection.Singleton
    static class Blue implements Color {

        @Override
        public String hexCode() {
            return "0000FF";
        }
    }

    @Injection.Named("green")
    @Injection.Singleton
    static class Green implements Color {

        @Override
        public String hexCode() {
            return "008000";
        }
    }

    @Injection.Singleton
    record BlueCircle(@Injection.Named("blue") Color color) {
    }

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
