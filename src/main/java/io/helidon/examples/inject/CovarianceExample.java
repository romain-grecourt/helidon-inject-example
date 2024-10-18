package io.helidon.examples.inject;

import java.util.List;

import io.helidon.service.inject.InjectRegistryManager;
import io.helidon.service.inject.api.Injection;

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

    @Injection.Singleton
    record Siamese() implements Cat {
    }

    @Injection.Singleton
    record Bengal() implements Cat {
    }

    @Injection.Singleton
    record Boxer() implements Dog {
    }

    @Injection.Singleton
    record Husky() implements Dog {
    }

    @Injection.Singleton
    record Shelter(List<Pet> all, List<Cat> cats, List<Dog> dogs) {
    }

    public static void main(String[] args) {
        var registry = InjectRegistryManager.create().registry();
        var shelter = registry.get(Shelter.class);

        System.out.println("All pets:");
        shelter.all.stream().map(Pet::name).forEach(System.out::println);

        System.out.println("\nAll cats:");
        shelter.cats.stream().map(Cat::name).forEach(System.out::println);

        System.out.println("\nAll dogs:");
        shelter.dogs.stream().map(Dog::name).forEach(System.out::println);
    }
}
