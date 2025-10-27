package axo.dev.rpdepent.client;

import axo.dev.rpdepent.GUI.CrashScreen;
import com.fasterxml.jackson.core.io.JsonStringEncoder;
import com.mojang.brigadier.CommandDispatcher;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;

import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.*;

import static axo.dev.rpdepent.utilis.configScrapper.processRpdFiles;

public class RpdepentClient implements ClientModInitializer {
    public static String MissingMods;
    public static final String MOD_ID = "axo.dev.rpd";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    // replace instance field with a static one
    public static final ArrayList<String> NOT_FOUND_MODS = new ArrayList<>();
    private Screen parent;
    @Override
    public void onInitializeClient() {
        LOGGER.info("RPD client init");
        MinecraftClient client = MinecraftClient.getInstance();
        AtomicBoolean found = new AtomicBoolean(false);
        if (client == null) {
            LOGGER.warn("MinecraftClient not yet available; skipping resourcepack scan.");
            return;
        }
        Path resourcepacks = client.runDirectory.toPath().resolve("resourcepacks");

        try {
            processRpdFiles(resourcepacks, line -> {
                if (FabricLoader.getInstance().isModLoaded(line)) {
                    LOGGER.info("mod found: {}", line);
                } else {
                    NOT_FOUND_MODS.add(line);
                    found.set(true);
                }
            });
        } catch (IOException e) {
            LOGGER.error("Failed scanning RPD files", e);
        }

        if (found.get()) {
            ClientTickEvents.END_CLIENT_TICK.register(mc -> {

                if (true){
                    client.setScreen(new CrashScreen());
                }

            });
            String newline = System.getProperty("line.separator");
            MissingMods = "Mods with these IDs were not found: " + String.join(", ", NOT_FOUND_MODS);
            LOGGER.error(MissingMods);
        }
    }
}
