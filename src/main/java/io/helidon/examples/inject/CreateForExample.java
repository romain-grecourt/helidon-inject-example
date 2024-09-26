package io.helidon.examples.inject;

import io.helidon.service.inject.InjectRegistryManager;
import io.helidon.service.inject.api.Injection;
import io.helidon.service.registry.Service;

class CreateForExample {

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

    @Injection.CreateFor(Color.class)
    record Circle(@Injection.CreateForName String name, Color color) {
    }

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
