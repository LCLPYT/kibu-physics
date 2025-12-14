package work.lclpnet.kibu.physics;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.lclpnet.kibu.physics.impl.Rayon;

public class KibuPhysics implements ModInitializer {

    public static final String MODID = "kibu-physics";
    public static final Logger LOGGER = LoggerFactory.getLogger(KibuPhysics.MODID);

    @Override
    public void onInitialize() {
        Rayon.intialize();
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
}
