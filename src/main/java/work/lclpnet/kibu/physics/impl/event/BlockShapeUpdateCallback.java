package work.lclpnet.kibu.physics.impl.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import work.lclpnet.kibu.hook.Hook;
import work.lclpnet.kibu.hook.HookFactory;

public interface BlockShapeUpdateCallback {

    Hook<BlockShapeUpdateCallback> HOOK = HookFactory.createArrayBacked(BlockShapeUpdateCallback.class, hooks -> (level, pos, state) -> {
        for (BlockShapeUpdateCallback hook : hooks) {
            hook.onUpdate(level, pos, state);
        }
    });

    void onUpdate(Level level, BlockPos pos, BlockState state);
}
