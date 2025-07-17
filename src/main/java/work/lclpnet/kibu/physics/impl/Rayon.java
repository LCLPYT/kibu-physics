package work.lclpnet.kibu.physics.impl;

import org.slf4j.Logger;
import work.lclpnet.kibu.physics.KibuPhysics;
import work.lclpnet.kibu.physics.impl.bullet.natives.NativeLoader;
import work.lclpnet.kibu.physics.impl.event.ServerEventHandler;

public class Rayon {

	public static final Logger LOGGER = KibuPhysics.LOGGER;

	public static void intialize() {
		NativeLoader loader = new NativeLoader(LOGGER);

		if (!loader.load()) {
			LOGGER.error("Disabling...");
			return;
		}

		ServerEventHandler.register();
	}
}