package com.github.kurdishlabs.farmingmode;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

@Mod(modid = FarmingMod.MODID, version = FarmingMod.VERSION, clientSideOnly = true)
public class FarmingMod {
    public static final String MODID = "farmingmod";
    public static final String VERSION = "1.0";

    private KeyBinding toggleKey;
    private boolean farmingModeEnabled = false;

    // Store previous values
    private int previousRenderDistance = -1;
    private int previousFpsLimit = -1;
    private boolean previousVsync = false;

    private final Minecraft mc = Minecraft.getMinecraft();

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // Register keybinding - default to F key, but user can change it
        toggleKey = new KeyBinding("key.farmingmod.toggle", Keyboard.KEY_F, "key.categories.farmingmod");
        ClientRegistry.registerKeyBinding(toggleKey);

        // Register event handler
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (toggleKey.isPressed()) {
            toggleFarmingMode();
        }
    }

    private void toggleFarmingMode() {
        GameSettings settings = mc.gameSettings;

        if (!farmingModeEnabled) {
            // Store current values before enabling
            previousRenderDistance = settings.renderDistanceChunks;
            previousFpsLimit = settings.limitFramerate;
            previousVsync = settings.enableVsync;

            // Enable farming mode
            settings.renderDistanceChunks = 1;
            settings.limitFramerate = 30;
            settings.enableVsync = false;

            farmingModeEnabled = true;

            // Send client message
            if (mc.thePlayer != null) {
                mc.thePlayer.addChatMessage(new ChatComponentText("§aFarming mod enabled"));
            }
        } else {
            // Restore previous values
            if (previousRenderDistance != -1) {
                settings.renderDistanceChunks = previousRenderDistance;
            }
            if (previousFpsLimit != -1) {
                settings.limitFramerate = previousFpsLimit;
            }
            settings.enableVsync = previousVsync;

            farmingModeEnabled = false;

            // Send client message
            if (mc.thePlayer != null) {
                mc.thePlayer.addChatMessage(new ChatComponentText("§cFarming mod disabled"));
            }
        }

        // Save the settings
        settings.saveOptions();
    }

    // Optional: Add a visual indicator on the screen
    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Text event) {
        if (farmingModeEnabled && mc.gameSettings.showDebugInfo) {
            event.left.add("§aFarming Mode: ON");
        }
    }
}