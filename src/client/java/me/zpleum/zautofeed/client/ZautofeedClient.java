package me.zpleum.zautofeed.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class ZautofeedClient implements ClientModInitializer {

    private static boolean enabled = false;

    private static final long INTERVAL_MS = 200_000; // 200 Second
    private static long lastFeedTime = 0;

    private static KeyBinding toggleKey;

    @Override
    public void onInitializeClient() {

        // Keybind
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.zautofeed.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_PERIOD,
                "category.zautofeed"
        ));

        enabled = true;
        lastFeedTime = System.currentTimeMillis();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            // Toggle
            while (toggleKey.wasPressed()) {
                enabled = !enabled;
                lastFeedTime = System.currentTimeMillis();

                client.player.sendMessage(
                        Text.literal("§6[zAutoFeed] " + (enabled ? "§aEnabled" : "§cDisabled")),
                        false
                );
            }

            if (!enabled) return;

            long now = System.currentTimeMillis();
            if (now - lastFeedTime >= INTERVAL_MS) {
                lastFeedTime = now;
                client.player.networkHandler.sendChatCommand("cmi feed");
            }
        });
    }
}
