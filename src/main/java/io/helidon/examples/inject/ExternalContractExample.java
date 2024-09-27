package io.helidon.examples.inject;

import java.util.List;
import java.util.Random;

import io.helidon.service.inject.InjectRegistryManager;
import io.helidon.service.inject.api.Injection;
import io.helidon.service.registry.Service;

/**
 * An example that illustrates usages of {@link Service.ExternalContracts}.
 */
class ExternalContractExample {

    /**
     * A service that implements {@link CharSequence} as a greeting.
     */
    @Injection.Instance
    @Service.ExternalContracts(CharSequence.class)
    static class RandomName implements CharSequence {

        static final List<String> NAMES = List.of(
                "Joe", "Jack", "Julia", "Jeanne", "Jessica",
                "Juliet", "Jennifer", "Josephine");

        private static final Random RANDOM = new Random();

        private final String name = NAMES.get(RANDOM.nextInt(0, NAMES.size() - 1));

        @Override
        public int length() {
            return name.length();
        }

        @Override
        public char charAt(int index) {
            return name.charAt(index);
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return name.subSequence(start, end);
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static void main(String[] args) {
        var registry = InjectRegistryManager.create().registry();
        var name = registry.get(CharSequence.class);
        System.out.println(name);
    }
}
