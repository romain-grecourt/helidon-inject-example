package io.helidon.examples.inject;

import java.util.List;

import io.helidon.service.registry.ServiceRegistryManager;
import io.helidon.service.registry.Service;

/**
 * An example that demonstrates covariant lookups.
 */
class CovarianceExample {
    sealed interface Pet permits Cat, Dog {

        default String name() {
            return getClass().getSimpleName();
        }
    }

    sealed interface Cat extends Pet permits Siamese, Bengal {
    }

    sealed interface Dog extends Pet permits Boxer, Husky {
    }

    @Service.Singleton
    record Siamese() implements Cat {
    }

    @Service.Singleton
    record Bengal() implements Cat {
    }

    @Service.Singleton
    record Boxer() implements Dog {
    }

    @Service.Singleton
    record Husky() implements Dog {
    }

    @Service.Singleton
    record Shelter(List<Pet> all, List<Cat> cats, List<Dog> dogs) {
    }

    public static void main(String[] args) {
        var registry = ServiceRegistryManager.create().registry();
        var shelter = registry.get(Shelter.class);

        System.out.println("All pets:");
        shelter.all.stream().map(Pet::name).forEach(System.out::println);

        System.out.println("\nAll cats:");
        shelter.cats.stream().map(Cat::name).forEach(System.out::println);

        System.out.println("\nAll dogs:");
        shelter.dogs.stream().map(Dog::name).forEach(System.out::println);
    }
}
