package work.lclpnet.kibu.physics.impl.event;

import com.jme3.math.Vector3f;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import work.lclpnet.kibu.physics.api.EntityPhysicsElement;
import work.lclpnet.kibu.physics.api.event.collision.PhysicsSpaceEvents;
import work.lclpnet.kibu.physics.impl.bullet.collision.body.ElementRigidBody;
import work.lclpnet.kibu.physics.impl.bullet.collision.body.EntityRigidBody;
import work.lclpnet.kibu.physics.impl.bullet.collision.space.MinecraftSpace;
import work.lclpnet.kibu.physics.impl.bullet.collision.space.generator.EntityCollisionGenerator;
import work.lclpnet.kibu.physics.impl.bullet.collision.space.generator.PressureGenerator;
import work.lclpnet.kibu.physics.impl.bullet.collision.space.generator.TerrainGenerator;
import work.lclpnet.kibu.physics.impl.bullet.collision.space.storage.SpaceStorage;
import work.lclpnet.kibu.physics.impl.bullet.collision.space.supplier.entity.ServerEntitySupplier;
import work.lclpnet.kibu.physics.impl.bullet.collision.space.supplier.level.ServerLevelSupplier;
import work.lclpnet.kibu.physics.impl.bullet.math.Convert;
import work.lclpnet.kibu.physics.impl.bullet.thread.PhysicsThread;
import work.lclpnet.kibu.physics.impl.bullet.thread.util.ClientUtil;

public final class ServerEventHandler {
    private static PhysicsThread thread;

    public static PhysicsThread getThread() {
        return thread;
    }

    public static void register() {
        // Rayon Events
        PhysicsSpaceEvents.STEP.register(PressureGenerator::step);
        PhysicsSpaceEvents.STEP.register(ServerEventHandler::stepTerrain);
        PhysicsSpaceEvents.ELEMENT_ADDED.register(ServerEventHandler::onElementAddedToSpace);

        // Server Events
        ServerLifecycleEvents.SERVER_STARTING.register(ServerEventHandler::onServerStart);
        ServerLifecycleEvents.SERVER_STOPPING.register(ServerEventHandler::onServerStop);
        ServerTickEvents.END_SERVER_TICK.register(ServerEventHandler::onServerTick);

        // Level Events
        ServerWorldEvents.LOAD.register(ServerEventHandler::onLevelLoad);
        ServerTickEvents.START_WORLD_TICK.register(ServerEventHandler::onStartLevelTick);
        ServerTickEvents.START_WORLD_TICK.register(ServerEventHandler::onEntityStartLevelTick);
        BlockShapeUpdateCallback.HOOK.register(ServerEventHandler::onBlockUpdate);

        // Entity Events
        ServerEntityEvents.ENTITY_LOAD.register(ServerEventHandler::onEntityLoad);
        EntityTrackingEvents.START_TRACKING.register(ServerEventHandler::onStartTrackingEntity);
        EntityTrackingEvents.STOP_TRACKING.register(ServerEventHandler::onStopTrackingEntity);
    }

    public static void onBlockUpdate(Level level, BlockPos blockPos, BlockState blockState) {
        MinecraftSpace.getOptional(level).ifPresent(space -> space.doBlockUpdate(blockPos));
    }

    public static void onServerStart(MinecraftServer server) {
        thread = new PhysicsThread(server, Thread.currentThread(), new ServerLevelSupplier(server), new ServerEntitySupplier(), "Server Physics Thread");
        thread.start();
    }

    public static void onServerStop(MinecraftServer server) {
        thread.destroy();
    }

    public static void onServerTick(MinecraftServer server) {
        if (thread.throwable != null) {
            throw new RuntimeException(thread.throwable);
        }
    }

    public static void onStartLevelTick(Level level) {
        if (!ClientUtil.isPaused()) {
            MinecraftSpace.get(level).step();
        }
    }

    public static void onLevelLoad(MinecraftServer server, ServerLevel level) {
        var space = new MinecraftSpace(thread, level);

        space = PhysicsSpaceEvents.CREATE.invoker().createPhysicsSpace(thread, level, space);

        ((SpaceStorage) level).kibu$setSpace(space);
        PhysicsSpaceEvents.INIT.invoker().onInit(space);
    }

    public static void onElementAddedToSpace(MinecraftSpace space, ElementRigidBody rigidBody) {
        if (rigidBody instanceof EntityRigidBody entityBody) {
            final var pos = entityBody.getElement().cast().position();
            entityBody.setPhysicsLocation(Convert.toBullet(pos));
        }
    }

    public static void onEntityLoad(Entity entity, ServerLevel level) {
        if (EntityPhysicsElement.is(entity) && !PlayerLookup.tracking(entity).isEmpty()) {
            var space = MinecraftSpace.get(entity.level());
            space.getWorkerThread().execute(() -> space.addCollisionObject(EntityPhysicsElement.get(entity).getRigidBody()));
        }
    }

    public static void onStartTrackingEntity(Entity entity, ServerPlayer player) {
        if (EntityPhysicsElement.is(entity)) {
            var space = MinecraftSpace.get(entity.level());
            space.getWorkerThread().execute(() -> space.addCollisionObject(EntityPhysicsElement.get(entity).getRigidBody()));
        }
    }

    public static void onStopTrackingEntity(Entity entity, ServerPlayer player) {
        if (EntityPhysicsElement.is(entity) && PlayerLookup.tracking(entity).isEmpty()) {
            var space = MinecraftSpace.get(entity.level());
            space.getWorkerThread().execute(() -> space.removeCollisionObject(EntityPhysicsElement.get(entity).getRigidBody()));
        }
    }

    public static void onEntityStartLevelTick(Level level) {
        var space = MinecraftSpace.get(level);
        EntityCollisionGenerator.step(space);

        for (var rigidBody : space.getRigidBodiesByClass(EntityRigidBody.class)) {
            /* Set entity position */
            var location = rigidBody.getFrame().getLocation(new Vector3f(), 1.0f);
            rigidBody.getElement().cast().absSnapTo(location.x, location.y, location.z);
        }
    }

    private static void stepTerrain(MinecraftSpace space) {
        if (space.isAutoLoadTerrain()) {
            TerrainGenerator.step(space);
        }
    }
}