package io.helidon.examples.inject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.helidon.service.inject.InjectRegistryManager;
import io.helidon.service.inject.api.InjectRegistry;
import io.helidon.service.inject.api.InjectServiceInfo;
import io.helidon.service.inject.api.Injection;
import io.helidon.service.inject.api.Lookup;
import io.helidon.service.registry.Service;

/**
 * A service that illustrates {@link Injection.RunLevel}
 */
class RunLevelExample {

    static final List<String> STARTUP_EVENTS = new ArrayList<>();

    /**
     * A service that starts at level {@code 1}.
     */
    @Injection.RunLevel(1)
    @Injection.Singleton
    static class Level1 {

        @Service.PostConstruct
        void onCreate() {
            STARTUP_EVENTS.add("level1");
        }
    }

    /**
     * A service that starts at level {@code 2}.
     */
    @Injection.RunLevel(2)
    @Injection.Singleton
    static class Level2 {

        @Service.PostConstruct
        void onCreate() {
            STARTUP_EVENTS.add("level2");
        }
    }

    /**
     * Start all the services with run levels.
     *
     * @param registry registry
     */
    static void startRunLevels(InjectRegistry registry) {
        for (var runLevel : runLevels(registry)) {
            if (runLevel <= 2) {
                registry.all(Lookup.builder()
                        .runLevel(runLevel)
                        .build());
            }
        }
    }

    /**
     * Extract all the run levels from the registry.
     *
     * @param registry registry
     * @return run levels
     */
    static List<Double> runLevels(InjectRegistry registry) {
        return registry.lookupServices(Lookup.EMPTY)
                .stream()
                .map(InjectServiceInfo::runLevel)
                .flatMap(Optional::stream)
                .distinct()
                .sorted()
                .toList();
    }

    public static void main(String[] args) {
        var registry = InjectRegistryManager.create().registry();
        startRunLevels(registry);
        STARTUP_EVENTS.forEach(System.out::println);
    }
}
