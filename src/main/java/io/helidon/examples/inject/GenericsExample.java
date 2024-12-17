package io.helidon.examples.inject;

import java.util.List;

import io.helidon.service.registry.ServiceRegistryManager;
import io.helidon.service.registry.Service;

/**
 * An example that demonstrates using generics.
 */
public class GenericsExample {

    /**
     * A service to be implemented by qualified services.
     */
    interface Color {
        String name();
    }

    @Service.Singleton
    static class Blue implements Color {
        @Override
        public String name() {
            return "blue";
        }
    }

    @Service.Singleton
    static class Green implements Color {
        @Override
        public String name() {
            return "green";
        }
    }

    interface Circle<T extends Color> {
        T color();

        default String name() {
            return color().name() + " circle";
        }
    }

    @Service.Singleton
    record BlueCircle(Blue color) implements Circle<Blue> {
    }

    @Service.Singleton
    record GreenCircle(Green color) implements Circle<Green> {
    }

    @Service.Singleton
    record MyService(Circle<Blue> blueCircle,
                     Circle<Green> greenCircle,
                     List<Circle<Color>> circles) {

        List<String> circleNames() {
            return circles.stream()
                    .map(GenericsExample.Circle::name)
                    .toList();
        }
    }

    public static void main(String[] args) {
        var registry = ServiceRegistryManager.create().registry();
        var myService = registry.get(MyService.class);

        System.out.println(myService.blueCircle().name());
        System.out.println(myService.greenCircle().name());
        myService.circleNames().forEach(System.out::println);
    }
}
