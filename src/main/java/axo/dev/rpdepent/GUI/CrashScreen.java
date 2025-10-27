package axo.dev.rpdepent.GUI;
import axo.dev.rpdepent.client.RpdepentClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static axo.dev.rpdepent.client.RpdepentClient.MissingMods;

public class CrashScreen extends Screen {
    public static final String MOD_ID = "axo.dev.rpd";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    // We use the static nested class to make sure it's compiled correctly
    // and can be instantiated easily.

    public CrashScreen() {
        // The parameter is the title of the screen,
        // which will be narrated when you enter the screen.
        super(Text.literal("Crash screen - missing mods"));

    }

    public ButtonWidget button1;

    @Override
    protected void init() {
        // A common Minecraft variable for the current client instance
        MinecraftClient client = MinecraftClient.getInstance();

        button1 = ButtonWidget.builder(Text.literal("Close the game"), button -> {
                    LOGGER.warn("You clicked the \"close\" button and so i made the game close itself! unexpected right?");
                    this.client.stop();
                })
                .dimensions(width / 4, 200, 200, 20).build();

        addDrawableChild(button1);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawText(this.textRenderer, MissingMods, 40, 40 - this.textRenderer.fontHeight - 10, 0xFFFFFFFF, true);
    }
}