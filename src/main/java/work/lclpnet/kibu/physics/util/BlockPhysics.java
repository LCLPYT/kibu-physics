package work.lclpnet.kibu.physics.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import work.lclpnet.kibu.physics.KibuPhysics;
import work.lclpnet.kibu.physics.impl.bullet.collision.body.ElementRigidBody;
import work.lclpnet.kibu.physics.impl.bullet.collision.body.shape.MinecraftShape;

import static java.lang.Float.isNaN;
import static java.lang.Math.*;
import static work.lclpnet.kibu.physics.impl.bullet.collision.body.shape.MinecraftShape.convex;

public class BlockPhysics {

    public static final TagKey<Block> FLOATING_TAG = TagKey.create(Registries.BLOCK, KibuPhysics.id("water_floating"));
    public static final MinecraftShape.Convex CENTERED_BOX = convex(new AABB(-.5, -.5, -.5, .5, .5, .5));

    public static float getMass(BlockState state) {
        Block block = state.getBlock();

        float mass = (float) max(5, min(50, 12f * log1p(max(block.defaultDestroyTime(), block.getExplosionResistance()))));

        return isNaN(mass) ? 60 : mass;
    }

    public static ElementRigidBody.BuoyancyType getBuoyancyType(BlockState state) {
        return state.is(FLOATING_TAG) ? ElementRigidBody.BuoyancyType.WATER : ElementRigidBody.BuoyancyType.NONE;
    }

    public static MinecraftShape.@NotNull Convex getShape(BlockState state, Level level) {
        if (state.isAir()) {
            return CENTERED_BOX;
        }

        VoxelShape box = state.getCollisionShape(level, BlockPos.ZERO);

        if (box.isEmpty()) {
            box = state.getShape(level, BlockPos.ZERO);
        }

        return convex(box);
    }
}
