package work.lclpnet.kibu.physics.impl.mixin.common.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import work.lclpnet.kibu.physics.api.EntityPhysicsElement;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Consumer;

/**
 * Prevents certain packets from being sent for {@link EntityPhysicsElement}s.
 */
@Mixin(ServerEntity.class)
public class EntityTrackerEntryMixin {
    @Shadow @Final private Entity entity;

    @WrapOperation(
            method = "sendChanges",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V",
                    ordinal = 1
            )
    )
    public void rotate(Consumer<?> instance, Object o, Operation<Void> original) {  // TODO check 1.19 injectors!!
        if (!EntityPhysicsElement.is(entity)) {
            original.call(instance, o);
        }
    }

    @WrapOperation(
            method = "sendChanges",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V",
                    ordinal = 2
            )
    )
    public void velocity(Consumer<?> instance, Object o, Operation<Void> original) {
        if (!EntityPhysicsElement.is(entity)) {
            original.call(instance, o);
        }
    }

    @WrapOperation(
            method = "sendChanges",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V",
                    ordinal = 3
            )
    )
    public void multiple(Consumer<?> instance, Object o, Operation<Void> original) {
        if (!EntityPhysicsElement.is(entity)) {
            original.call(instance, o);
        }
    }
}
