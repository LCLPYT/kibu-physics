package work.lclpnet.kibu.physics.impl.bullet.collision.space.generator;

import net.minecraft.core.BlockPos;
import work.lclpnet.kibu.physics.impl.bullet.collision.body.ElementRigidBody;
import work.lclpnet.kibu.physics.impl.bullet.collision.body.TerrainRigidBody;
import work.lclpnet.kibu.physics.impl.bullet.collision.space.MinecraftSpace;

import java.util.HashSet;
import java.util.Optional;

/**
 * Used for loading blocks into the simulation so that rigid bodies can interact with them.
 * @see MinecraftSpace
 */
public class TerrainGenerator {
    public static void step(MinecraftSpace space) {
        final var keep = new HashSet<TerrainRigidBody>();

        for (var rigidBody : space.getRigidBodiesByClass(ElementRigidBody.class)) {
            if (!rigidBody.terrainLoadingEnabled() || !rigidBody.isActive()) {
                continue;
            }

            final var aabb = rigidBody.getCurrentMinecraftBoundingBox().inflate(0.5f);

            BlockPos.betweenClosedStream(aabb).forEach(blockPos -> load(space, blockPos).ifPresent(keep::add));
        }

        space.getTerrainMap().forEach((blockPos, terrain) -> {
            if (!keep.contains(terrain)) {
                space.removeTerrainObjectAt(blockPos);
            }
        });
    }

    public static Optional<TerrainRigidBody> load(MinecraftSpace space, BlockPos pos) {
        return space.getChunkCache().getBlockData(pos).map(blockData -> space.getTerrainObjectAt(pos)
                .map(terrain -> {
                    if (blockData.blockState() == terrain.getBlockState()) {
                        return terrain;
                    }

                    space.removeCollisionObject(terrain);

                    final var terrain2 = TerrainRigidBody.from(blockData);
                    space.addCollisionObject(terrain2);

                    return terrain2;
                })
                .orElseGet(() -> {
                    final var terrain = TerrainRigidBody.from(blockData);
                    space.addCollisionObject(terrain);
                    return terrain;
                }));
    }
}