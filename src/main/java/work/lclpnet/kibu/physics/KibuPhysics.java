package work.lclpnet.kibu.physics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.lclpnet.kibu.physics.impl.Rayon;
import net.fabricmc.api.ModInitializer;

public class KibuPhysics implements ModInitializer {

    public static final String MODID = "kibu-physics";
    public static final Logger LOGGER = LoggerFactory.getLogger(KibuPhysics.MODID);

    @Override
    public void onInitialize() {
        Rayon.intialize();
    }
}
