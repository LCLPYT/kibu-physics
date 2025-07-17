package work.lclpnet.kibu.physics.impl.bullet.collision.space.storage;

import work.lclpnet.kibu.physics.impl.bullet.collision.space.MinecraftSpace;
import net.minecraft.world.level.Level;

/**
 * Used for storing a {@link MinecraftSpace} within any
 * {@link Level} object.
 */
public interface SpaceStorage {
    void kibu$setSpace(MinecraftSpace space);
    MinecraftSpace kibu$getSpace();
}