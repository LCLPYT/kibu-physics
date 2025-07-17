package work.lclpnet.kibu.physics.impl.bullet.natives;

import electrostatic4j.snaploader.LibraryInfo;
import electrostatic4j.snaploader.LoadingCriterion;
import electrostatic4j.snaploader.NativeBinaryLoader;
import electrostatic4j.snaploader.filesystem.DirectoryPath;
import electrostatic4j.snaploader.platform.NativeDynamicLibrary;
import electrostatic4j.snaploader.platform.util.PlatformPredicate;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;

import java.nio.file.Path;

public class NativeLoader {

    private final Logger logger;

    public NativeLoader(Logger logger) {
        this.logger = logger;
    }

    public boolean load() {
        Path path = getGameDir().resolve("natives");
        var dirPath = new DirectoryPath(path.toString());

        LibraryInfo info = new LibraryInfo(null, "bulletjme", dirPath);
        NativeBinaryLoader loader = new NativeBinaryLoader(info);

        NativeDynamicLibrary[] libraries = {
                new NativeDynamicLibrary("native/linux/arm64", PlatformPredicate.LINUX_ARM_64),
                new NativeDynamicLibrary("native/linux/x86_64", PlatformPredicate.LINUX_X86_64),
                new NativeDynamicLibrary("native/osx/arm64", PlatformPredicate.MACOS_ARM_64),
                new NativeDynamicLibrary("native/osx/x86_64", PlatformPredicate.MACOS_X86_64),
                new NativeDynamicLibrary("native/windows/x86_64", PlatformPredicate.WIN_X86_64)
        };

        loader.registerNativeLibraries(libraries).initPlatformLibrary();

        try {
            loader.loadLibrary(LoadingCriterion.CLEAN_EXTRACTION);
        } catch (Exception e) {
            logger.error("Failed to load libbulletjme native library", e);
            return false;
        }

        logger.info("libbulletjme native library successfully loaded");
        return true;
    }

    static Path getGameDir() {
        return FabricLoader.getInstance().getGameDir();
    }
}