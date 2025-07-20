package work.lclpnet.kibu.physics.impl;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.objects.PhysicsRigidBody;
import org.slf4j.Logger;
import work.lclpnet.kibu.physics.KibuPhysics;
import work.lclpnet.kibu.physics.impl.bullet.natives.NativeLoader;
import work.lclpnet.kibu.physics.impl.event.ServerEventHandler;

import java.util.logging.Level;

public class Rayon {

	public static final Logger LOGGER = KibuPhysics.LOGGER;

	public static void intialize() {
		configureLoggers();

		NativeLoader loader = new NativeLoader(LOGGER);

		if (!loader.load()) {
			LOGGER.error("Disabling...");
			return;
		}


		ServerEventHandler.register();
	}

	private static void configureLoggers() {
		java.util.logging.Logger.getLogger("jSnapLoader").setLevel(Level.WARNING);

		PhysicsSpace.logger.setLevel(Level.SEVERE);
		PhysicsCollisionObject.logger.setLevel(Level.SEVERE);
		PhysicsRigidBody.logger2.setLevel(Level.SEVERE);
	}
}