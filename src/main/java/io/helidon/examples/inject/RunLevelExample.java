package io.helidon.examples.inject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.helidon.service.registry.Lookup;
import io.helidon.service.registry.Service;
import io.helidon.service.registry.ServiceInfo;
import io.helidon.service.registry.ServiceRegistry;
import io.helidon.service.registry.ServiceRegistryManager;

/**
 * A example that illustrates {@link Service.RunLevel}
 */
class RunLevelExample {

    static final List<String> STARTUP_EVENTS = new ArrayList<>();

    /**
     * A service that starts at level {@code 1}.
     */
    @Service.RunLevel(1)
    @Service.Singleton
    static class Level1 {

        @Service.PostConstruct
        void onCreate() {
            STARTUP_EVENTS.add("level1");
        }
    }

    /**
     * A service that starts at level {@code 2}.
     */
    @Service.RunLevel(2)
    @Service.Singleton
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
    static void startRunLevels(ServiceRegistry registry) {
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
    static List<Double> runLevels(ServiceRegistry registry) {
        return registry.lookupServices(Lookup.EMPTY)
                .stream()
                .map(ServiceInfo::runLevel)
                .flatMap(Optional::stream)
                .distinct()
                .sorted()
                .toList();
    }

    public static void main(String[] args) {
        var registry = ServiceRegistryManager.create().registry();
        startRunLevels(registry);
        STARTUP_EVENTS.forEach(System.out::println);
    }
}
