package dev.lazurite.rayon.impl.mixin.common;

import dev.lazurite.rayon.impl.event.BlockShapeUpdateCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public class BlockStateBaseMixin {

    @Inject(
            method = "updateShape",
            at = @At("HEAD")
    )
    public void updateShape(LevelReader levelReader, ScheduledTickAccess scheduledTickAccess, BlockPos blockPos, Direction direction, BlockPos blockPos2, BlockState blockState, RandomSource randomSource, CallbackInfoReturnable<BlockState> cir) {
        if (levelReader instanceof Level level) {
            BlockShapeUpdateCallback.HOOK.invoker().onUpdate(level, blockPos2, blockState);
        }
    }
}
