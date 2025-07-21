package work.lclpnet.kibu.physics.api.event.collision;

import work.lclpnet.kibu.hook.Hook;
import work.lclpnet.kibu.hook.HookFactory;
import work.lclpnet.kibu.physics.api.PhysicsElement;
import work.lclpnet.kibu.physics.impl.bullet.collision.body.ElementRigidBody;
import work.lclpnet.kibu.physics.impl.bullet.collision.body.TerrainRigidBody;

/**
 * @since 1.0.0
 */
public class ElementCollisionEvents {

    /**
     * Called when an {@link ElementRigidBody} collides with a {@link TerrainRigidBody} (with a real block).
     * In order for this callback to be fired, collision events must be enabled for the physics space using
     * <code>MinecraftSpace.get(level).setCollisionEventsEnabled(true)</code>
     */
    public static final Hook<BlockCollision> BLOCK_COLLISION = HookFactory.createArrayBacked(BlockCollision.class, hooks -> (element, terrainObject, impulse) -> {
        for (BlockCollision hook : hooks) {
            hook.onCollide(element, terrainObject, impulse);
        }
    });

    /**
     * Called when two {@link ElementRigidBody} collide.
     * In order for this callback to be fired, collision events must be enabled for the physics space using
     * <code>MinecraftSpace.get(level).setCollisionEventsEnabled(true)</code>
     */
    public static final Hook<ElementCollision> ELEMENT_COLLISION = HookFactory.createArrayBacked(ElementCollision.class, hooks -> (element1, element2, impulse) -> {
        for (ElementCollision hook : hooks) {
            hook.onCollide(element1, element2, impulse);
        }
    });

    private ElementCollisionEvents() { }

    @FunctionalInterface
    public interface BlockCollision {
        /**
         * Invoked each time an {@link ElementRigidBody} collides with a {@link }.
         * @param element the element
         * @param terrainObject the terrain object
         * @param manifoldId the native manifold id that can be used to get more information about the collision points
         * @see com.jme3.bullet.collision.PersistentManifolds
         */
        void onCollide(PhysicsElement<?> element, TerrainRigidBody terrainObject, long manifoldId);
    }

    @FunctionalInterface
    public interface FluidCollision {
        /**
         * Invoked each time an {@link ElementRigidBody} collides with a {@link TerrainRigidBody}.
         * @param element the element
         * @param terrainObject the terrain object
         * @param manifoldId the native manifold id that can be used to get more information about the collision points
         * @see com.jme3.bullet.collision.PersistentManifolds
         */
        void onCollide(PhysicsElement<?> element, TerrainRigidBody terrainObject, long manifoldId);
    }

    @FunctionalInterface
    public interface ElementCollision {
        /**
         * Invoked each time an {@link ElementRigidBody} collides with another {@link ElementRigidBody}.
         * @param element1 the first element
         * @param element2 the second element
         * @param manifoldId the native manifold id that can be used to get more information about the collision points
         * @see com.jme3.bullet.collision.PersistentManifolds
         */
        void onCollide(PhysicsElement<?> element1, PhysicsElement<?> element2, long manifoldId);
    }
}
