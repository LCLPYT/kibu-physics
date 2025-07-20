package work.lclpnet.kibu.physics.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import work.lclpnet.kibu.physics.KibuPhysics;
import work.lclpnet.kibu.physics.impl.bullet.collision.body.ElementRigidBody;

import static java.lang.Float.isNaN;
import static java.lang.Math.*;

public class BlockPhysics {

    public static final TagKey<Block> FLOATING_TAG = TagKey.create(Registries.BLOCK, KibuPhysics.rl("water_floating"));

    public static float getMass(BlockState state) {
        Block block = state.getBlock();

        float mass = (float) max(5, min(50, 12f * log1p(max(block.defaultDestroyTime(), block.getExplosionResistance()))));

        return isNaN(mass) ? 60 : mass;
    }

    public static ElementRigidBody.BuoyancyType getBuoyancyType(BlockState state) {
        return state.is(FLOATING_TAG) ? ElementRigidBody.BuoyancyType.WATER : ElementRigidBody.BuoyancyType.NONE;
    }
}
