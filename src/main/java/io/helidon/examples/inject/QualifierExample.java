package io.helidon.examples.inject;

import io.helidon.service.inject.InjectRegistryManager;
import io.helidon.service.inject.api.Injection;
import io.helidon.service.registry.Service;

class QualifierExample {

    @Service.Contract
    interface Color {
        String name();
    }

    @Injection.Qualifier
    public @interface HexCode {
        String value();
    }

    @HexCode("0000FF")
    @Injection.Singleton
    static class Blue implements Color {

        @Override
        public String name() {
            return "blue";
        }
    }

    @HexCode("008000")
    @Injection.Singleton
    static class Green implements Color {

        @Override
        public String name() {
            return "green";
        }
    }

    @Injection.Singleton
    record BlueCircle(@HexCode("0000FF") Color color) {
    }

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
