package axo.dev.rpdepent.client;

import axo.dev.rpdepent.GUI.CrashScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

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
                //check if the line as the OR operator
                if (line.contains("||")) {
                    //declare an array for the mods ID's
                    String[] keywords = line.split("\\|\\|");
                    //Loop trough all of them
                    for(int i = 0; i < keywords.length; i++) {
                        if(FabricLoader.getInstance().isModLoaded(keywords[i].strip())) {
                            LOGGER.info("mod found: {}", keywords[i]);
                            break; //found the motherfucker now terminate
                        }
                        else {
                            NOT_FOUND_MODS.add(keywords[i]);
                            found.set(true);
                        }
                    }
                }
                else if (FabricLoader.getInstance().isModLoaded(line)) {
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
