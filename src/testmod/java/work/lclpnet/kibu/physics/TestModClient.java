package work.lclpnet.kibu.physics;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.DisplayRenderer;

public class TestModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(TestMod.RIGID_BODY_BLOCK, DisplayRenderer.BlockDisplayRenderer::new);
    }
}
