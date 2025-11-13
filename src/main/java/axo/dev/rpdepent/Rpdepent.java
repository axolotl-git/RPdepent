package axo.dev.rpdepent;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

import static axo.dev.rpdepent.client.RpdepentClient.MOD_ID;
import static axo.dev.rpdepent.utilis.configScrapper.processRpdFiles;

public class Rpdepent implements ModInitializer {
    @Override
    public void onInitialize() {
    }
}