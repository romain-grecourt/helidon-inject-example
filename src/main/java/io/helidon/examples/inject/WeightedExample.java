package io.helidon.examples.inject;

import java.util.Optional;

import io.helidon.common.Weight;
import io.helidon.service.inject.InjectRegistryManager;
import io.helidon.service.inject.api.Injection;
import io.helidon.service.registry.Service;

class WeightedExample {

    @Service.Contract
    interface Color {
        String name();
    }

    @Service.Contract
    interface Shape {
        String name();
    }

    @Weight(1)
    @Injection.Singleton
    static class Blue implements Color {

        @Override
        public String name() {
            return "blue";
        }
    }

    @Weight(2)
    @Injection.Singleton
    static class Green implements Color {
        @Override
        public String name() {
            return "green";
        }
    }

    @Injection.Singleton
    record Symbol(Color color, Optional<Shape> shape) {
    }

    public static void main(String[] args) {
        var registry = InjectRegistryManager.create().registry();
        var symbol = registry.get(Symbol.class);

        System.out.printf("symbol color name: %s", symbol.color().name());
        System.out.printf("symbol shape: %s", symbol.shape().map(Shape::name).orElse("circle"));
    }
}
