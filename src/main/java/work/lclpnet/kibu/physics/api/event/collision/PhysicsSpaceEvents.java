package work.lclpnet.kibu.physics.api.event.collision;

import net.minecraft.server.level.ServerLevel;
import work.lclpnet.kibu.hook.Hook;
import work.lclpnet.kibu.hook.HookFactory;
import work.lclpnet.kibu.physics.impl.bullet.collision.body.ElementRigidBody;
import work.lclpnet.kibu.physics.impl.bullet.collision.space.MinecraftSpace;
import work.lclpnet.kibu.physics.impl.bullet.thread.PhysicsThread;

/**
 * @since 1.0.0
 */
public final class PhysicsSpaceEvents {

    public static final Hook<Create> CREATE = HookFactory.createArrayBacked(Create.class, hooks -> (thread, level, prev) -> {
        for (Create hook : hooks) {
            prev = hook.createPhysicsSpace(thread, level, prev);
        }

        return prev;
    });

    public static final Hook<Init> INIT = HookFactory.createArrayBacked(Init.class, hooks -> space -> {
        for (Init hook : hooks) {
            hook.onInit(space);
        }
    });

    public static final Hook<Step> STEP = HookFactory.createArrayBacked(Step.class, hooks -> space -> {
        for (Step hook : hooks) {
            hook.onStep(space);
        }
    });

    public static final Hook<ElementAdded> ELEMENT_ADDED = HookFactory.createArrayBacked(ElementAdded.class, hooks -> (space, rigidBody) -> {
        for (ElementAdded hook : hooks) {
            hook.onElementAdded(space, rigidBody);
        }
    });

    public static final Hook<ElementRemoved> ELEMENT_REMOVED = HookFactory.createArrayBacked(ElementRemoved.class, hooks -> (space, rigidBody) -> {
        for (ElementRemoved hook : hooks) {
            hook.onElementRemoved(space, rigidBody);
        }
    });

    private PhysicsSpaceEvents() { }

    @FunctionalInterface
    public interface Create {
        /**
         * Invoked when a {@link MinecraftSpace} is created for a {@link ServerLevel}.
         * @param thread The central {@link PhysicsThread}.
         * @param level The level for which to create the level.
         * @param prev The {@link MinecraftSpace} instance created by the previous callback.
         * @return The resulting callback, which is also passed to the next callback.
         */
        MinecraftSpace createPhysicsSpace(PhysicsThread thread, ServerLevel level, MinecraftSpace prev);
    }

    @FunctionalInterface
    public interface Init {
        /**
         * Invoked each time a new {@link MinecraftSpace} is created.
         * @param space the minecraft space
         */
        void onInit(MinecraftSpace space);
    }

    @FunctionalInterface
    public interface Step {
        /**
         * Invoked each time the {@link MinecraftSpace} is stepped.
         * @param space the minecraft space
         */
        void onStep(MinecraftSpace space);
    }

    @FunctionalInterface
    public interface ElementAdded {
        /**
         * Invoked each time a new {@link ElementRigidBody} is added to the environment.
         * @param space the minecraft space
         * @param rigidBody the element rigid body being added
         */
        void onElementAdded(MinecraftSpace space, ElementRigidBody rigidBody);
    }

    @FunctionalInterface
    public interface ElementRemoved {
        /**
         * Invoked each time an {@link ElementRigidBody} is removed from the environment.
         * @param space the minecraft space
         * @param rigidBody the element rigid body being removed
         */
        void onElementRemoved(MinecraftSpace space, ElementRigidBody rigidBody);
    }
}