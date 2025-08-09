package work.lclpnet.kibu.physics.impl.bullet.thread;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import work.lclpnet.kibu.physics.api.PhysicsElement;
import work.lclpnet.kibu.physics.api.event.collision.PhysicsSpaceEvents;
import work.lclpnet.kibu.physics.impl.Rayon;
import work.lclpnet.kibu.physics.impl.bullet.collision.space.MinecraftSpace;
import work.lclpnet.kibu.physics.impl.bullet.collision.space.supplier.entity.EntitySupplier;
import work.lclpnet.kibu.physics.impl.bullet.collision.space.supplier.level.LevelSupplier;
import work.lclpnet.kibu.physics.impl.bullet.thread.util.ClientUtil;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executor;

/**
 * In order to access an instance of this, all you need is a {@link Level} or {@link ReentrantBlockableEventLoop} object.
 * Calling {@link PhysicsThread#execute} adds a runnable to the queue of tasks and is the main way to execute code on
 * this thread. You can also execute code here by using {@link PhysicsSpaceEvents}.
 * @see PhysicsSpaceEvents
 * @see PhysicsElement
 * @see MinecraftSpace
 */
public class PhysicsThread extends Thread implements Executor {

    private final Queue<Runnable> tasks = new LinkedList<>();
    private final Executor parentExecutor;
    private final Thread parentThread;
    private final LevelSupplier levelSupplier;
    private final EntitySupplier entitySupplier;

    public volatile Throwable throwable;
    public volatile boolean running = true;

    public static PhysicsThread get(Level level) {
        return MinecraftSpace.get(level).getWorkerThread();
    }

    public PhysicsThread(Executor parentExecutor, Thread parentThread, LevelSupplier levelSupplier, EntitySupplier entitySupplier, String name) {
        this.parentExecutor = parentExecutor;
        this.parentThread = parentThread;
        this.levelSupplier = levelSupplier;
        this.entitySupplier = entitySupplier;

        this.setName(name);
        this.setUncaughtExceptionHandler((thread, throwable) -> {
            this.running = false;
            this.throwable = throwable;
        });

        Rayon.LOGGER.info("Starting {}", getName());
    }

    /**
     * The worker loop. Waits for tasks and executes right away.
     */
    @Override
    public void run() {
        try {
            while (running) {
                Runnable task;

                synchronized (tasks) {
                    while (tasks.isEmpty() || ClientUtil.isPaused()) {
                        tasks.wait();

                        if (!running) return;
                    }

                    task = tasks.poll();
                }

                if (task != null) {
                    task.run();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * For queueing up tasks to be executed on this thread. A {@link MinecraftSpace}
     * object is provided within the consumer.
     * @param task the task to run
     */
    @Override
    public void execute(@NotNull Runnable task) {
        synchronized (tasks) {
            tasks.add(task);
            tasks.notifyAll();
        }
    }

    /**
     * Gets the {@link LevelSupplier}. For servers, it is able to provide multiple worlds.
     * For clients, it will only provide one unless immersive portals is installed.
     * @return the {@link LevelSupplier}
     */
    public LevelSupplier getLevelSupplier() {
        return this.levelSupplier;
    }

    /**
     * A utility class for getting entity information.
     */
    public EntitySupplier getEntitySupplier() {
        return this.entitySupplier;
    }

    /**
     * Gets the parent executor. Useful for returning to the main thread,
     * especially server-side where {@link MinecraftServer} isn't always readily
     * available.
     * @return the original {@link Executor} object
     */
    public Executor getParentExecutor() {
        return this.parentExecutor;
    }

    /**
     * Gets the parent thread. This is useful for checking
     * whether a method is executing on this thread.
     * @see EntitySupplier
     * @return the parent {@link Thread} object
     */
    public Thread getParentThread() {
        return this.parentThread;
    }

    /**
     * Join the thread when the game closes.
     */
    public void destroy() {
        this.running = false;
        Rayon.LOGGER.info("Stopping {}", getName());

        synchronized (tasks) {
            tasks.notifyAll();
        }

        try {
            this.join(5000); // 5 second timeout
        } catch (InterruptedException e) {
            Rayon.LOGGER.error("Error joining {}", getName());
            Thread.currentThread().interrupt();
        }
    }
}
