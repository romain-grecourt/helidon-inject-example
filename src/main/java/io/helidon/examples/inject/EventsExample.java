package io.helidon.examples.inject;

import java.util.ArrayList;
import java.util.List;

import io.helidon.service.inject.InjectRegistryManager;
import io.helidon.service.inject.api.Event;
import io.helidon.service.inject.api.Injection;

/**
 * An example that illustrates usages of {@link Event}.
 */
class EventsExample {

    /**
     * A custom event payload.
     * @param msg message
     */
    record MyEvent(String msg) {
    }

    /**
     * A service that emits {@link MyEvent}.
     * @param emitter emitter
     */
    @Injection.Singleton
    record MyEmitter(Event.Emitter<MyEvent> emitter) {

        void emit(String msg) {
            emitter.emit(new MyEvent(msg));
        }
    }

    /**
     * A service that observes {@link MyEvent}.
     */
    @Injection.Singleton
    static class MyObserver {

        final List<String> messages = new ArrayList<>();

        @Event.Observer
        void event(MyEvent event) {
            messages.add(event.msg);
        }
    }

    /**
     * A service that emits string events named {@code id}.
     *
     * @param emitter emitter
     */
    @Injection.Singleton
    record MyIdEmitter(@Injection.Named("id") Event.Emitter<String> emitter) {

        void emit(String msg) {
            emitter.emit(msg);
        }
    }

    /**
     * A service that observes string events named {@code id}.
     */
    @Injection.Singleton
    static class MyIdObserver {

        final List<String> ids = new ArrayList<>();

        @Event.Observer
        @Injection.Named("id")
        void event(String id) {
            ids.add(id);
        }
    }

    /**
     * A service that emits string events named {@code name}.
     *
     * @param emitter emitter
     */
    @Injection.Singleton
    record MyNameEmitter(@Injection.Named("name") Event.Emitter<String> emitter) {

        void emit(String msg) {
            emitter.emit(msg);
        }
    }

    /**
     * A service that observes string events named {@code name}.
     */
    @Injection.Singleton
    static class MyNameObserver {

        final List<String> names = new ArrayList<>();

        @Event.Observer
        @Injection.Named("name")
        void event(String name) {
            names.add(name);
        }
    }

    public static void main(String[] args) {
        var registry = InjectRegistryManager.create().registry();
        var myEmitter = registry.get(MyEmitter.class);
        var myObserver = registry.get(MyObserver.class);
        var myIdEmitter = registry.get(MyIdEmitter.class);
        var myIdObserver = registry.get(MyIdObserver.class);
        var myNameEmitter = registry.get(MyNameEmitter.class);
        var myNameObserver = registry.get(MyNameObserver.class);

        myEmitter.emit("foo");
        myEmitter.emit("bar");
        System.out.println(myObserver.messages);

        myIdEmitter.emit("123");
        myIdEmitter.emit("456");
        System.out.println(myIdObserver.ids);

        myNameEmitter.emit("Jack");
        myNameEmitter.emit("Jill");
        System.out.println(myNameObserver.names);
    }
}
