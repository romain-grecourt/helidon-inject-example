package io.helidon.examples.inject;

import io.helidon.service.inject.InjectRegistryManager;
import io.helidon.service.inject.api.Injection;
import io.helidon.service.registry.Service;

class NamedByClassExample {

    @Service.Contract
    interface Color {
        String hexCode();
    }

    @Injection.NamedByClass(Blue.class)
    @Injection.Singleton
    static class Blue implements Color {

        @Override
        public String hexCode() {
            return "0000FF";
        }
    }

    @Injection.NamedByClass(Green.class)
    @Injection.Singleton
    static class Green implements Color {

        @Override
        public String hexCode() {
            return "008000";
        }
    }

    @Injection.Singleton
    record BlueSquare(@Injection.NamedByClass(Blue.class) Color color) {
    }

    @Injection.Singleton
    record GreenSquare(@Injection.NamedByClass(Green.class) Color color) {
    }

    public static void main(String[] args) {
        var registry = InjectRegistryManager.create().registry();
        var blueCircle = registry.get(BlueSquare.class);
        var greenCircle = registry.get(GreenSquare.class);

        System.out.printf("blue square color hex-code: %s%n", blueCircle.color().hexCode());
        System.out.printf("green square color hex-code: %s%n", greenCircle.color().hexCode());
    }
}
