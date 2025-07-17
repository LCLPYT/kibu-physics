package work.lclpnet.kibu.physics.impl.mixin.common.entity;

import com.llamalad7.mixinextras.sugar.Local;
import work.lclpnet.kibu.physics.api.EntityPhysicsElement;
import work.lclpnet.kibu.physics.api.PhysicsElement;
import work.lclpnet.kibu.physics.impl.bullet.math.Convert;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Allows {@link PhysicsElement} objects to be affected by explosions.
 */
@Mixin(ServerExplosion.class)
public class ExplosionMixin {

    @ModifyArg(
            method = "hurtEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;push(Lnet/minecraft/world/phys/Vec3;)V"
            )
    )
    public Vec3 setVelocity(Vec3 velocity, @Local Entity entity) {
        if (EntityPhysicsElement.is(entity)) {
            var element = EntityPhysicsElement.get(entity);
            element.getRigidBody().applyCentralImpulse(Convert.toBullet(velocity).multLocal(element.getRigidBody().getMass() * 100f));
        }

        return velocity;
    }
}