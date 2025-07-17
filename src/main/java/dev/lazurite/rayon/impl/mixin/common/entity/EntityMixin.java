package dev.lazurite.rayon.impl.mixin.common.entity;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.api.EntityPhysicsElement;
import dev.lazurite.rayon.impl.bullet.math.Convert;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Basic changes for {@link EntityPhysicsElement}s. ({@link CallbackInfo#cancel()} go brrr)
 */
@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    public void pushAwayFrom(Entity entity, CallbackInfo info) {
        if (EntityPhysicsElement.is((Entity) (Object) this) && EntityPhysicsElement.is(entity)) {
            info.cancel();
        }
    }

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    public void move(CallbackInfo info) {
        if (EntityPhysicsElement.is((Entity) (Object) this)) {
            info.cancel();
        }
    }

    @Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueOutput;)V"))
    public void saveWithoutId(ValueOutput valueOutput, CallbackInfo ci) {
        if (EntityPhysicsElement.is((Entity) (Object) this)) {
            var rigidBody = EntityPhysicsElement.get((Entity) (Object) this).getRigidBody();
            valueOutput.store("orientation", ExtraCodecs.QUATERNIONF, Convert.toMinecraft(rigidBody.getPhysicsRotation(new Quaternion())));
            valueOutput.store("linearVelocity", ExtraCodecs.VECTOR3F, Convert.toMinecraft(rigidBody.getLinearVelocity(new Vector3f())));
            valueOutput.store("angularVelocity", ExtraCodecs.VECTOR3F, Convert.toMinecraft(rigidBody.getAngularVelocity(new Vector3f())));
            valueOutput.putFloat("mass", rigidBody.getMass());
            valueOutput.putFloat("dragCoefficient", rigidBody.getDragCoefficient());
            valueOutput.putFloat("friction", rigidBody.getFriction());
            valueOutput.putFloat("restitution", rigidBody.getRestitution());
            valueOutput.putBoolean("terrainLoadingEnabled", rigidBody.terrainLoadingEnabled());
            valueOutput.putInt("buoyancyType", rigidBody.getBuoyancyType().ordinal());
            valueOutput.putInt("dragType", rigidBody.getDragType().ordinal());
        }
    }

    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueInput;)V"))
    public void load(ValueInput valueInput, CallbackInfo ci) {
        if (EntityPhysicsElement.is((Entity) (Object) this)) {
            EntityPhysicsElement.get((Entity) (Object) this).getRigidBody().readTagInfo(valueInput);
        }
    }
}
