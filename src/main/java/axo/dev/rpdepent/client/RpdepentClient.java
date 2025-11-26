package axo.dev.rpdepent.client;

import axo.dev.rpdepent.GUI.CrashScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
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
    public AtomicBoolean found = new AtomicBoolean(false);
    @Override
    public void onInitializeClient() {
        LOGGER.info("RPD client init");
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            LOGGER.warn("MinecraftClient not yet available; skipping resourcepack scan.");
            return;
        }
        Path resourcepacks = client.runDirectory.toPath().resolve("resourcepacks");

        try {
            processRpdFiles(resourcepacks, line -> {
                if (line == null || line.trim().isEmpty()) {
                    return; // Skip empty lines
                }

                String trimmedLine = line.trim();

                // Check if the line has the OR operator
                if (trimmedLine.contains("||")) {
                    boolean foundAny = false;
                    String[] keywords = trimmedLine.split("\\|\\|");
                    // Loop through all mod IDs in the OR condition
                    for (String keyword : keywords) {
                        String modId = keyword.strip();
                        if (!modId.isEmpty() && FabricLoader.getInstance().isModLoaded(modId)) {
                            LOGGER.info("Mod found: {}", modId);
                            foundAny = true;
                            break; // Found at least one required mod
                        }
                    }

                    if (!foundAny) {
                        // None of the mods in the OR condition were found
                        String ORnotfound = keywords[0] + " or " + keywords[1];
                        NOT_FOUND_MODS.add(ORnotfound);
                        found.set(true);
                    }
                } else {
                    if (FabricLoader.getInstance().isModLoaded(trimmedLine)) {
                        LOGGER.info("Mod found: {}", trimmedLine);
                    } else {
                        NOT_FOUND_MODS.add(trimmedLine);
                        found.set(true);
                    }
                }
            });
        } catch (IOException e) {
            LOGGER.error("Failed scanning RPD files", e);
        }

        if (found.get()) {
            ClientTickEvents.START_CLIENT_TICK.register(mc -> {
                client.setScreen(new CrashScreen());
            MissingMods = "Mods with these IDs were not found:\n" + String.join("; " + NOT_FOUND_MODS) +"\nyour resource packs may not work properly without";
            LOGGER.error(MissingMods);
        });
    }
}
}
