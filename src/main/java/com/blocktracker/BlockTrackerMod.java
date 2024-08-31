package com.blocktracker;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class BlockTrackerMod implements ClientModInitializer {
    private int blockBreakCount = 0;
    private long lastResetTime = System.currentTimeMillis();

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(this::onHudRender);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world != null && client.player != null) {
                if (client.options.attackKey.isPressed()) {
                    blockBreakCount++;
                }
            }
        });
    }

    private void onHudRender(MatrixStack matrixStack, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            long currentTime = System.currentTimeMillis();
            long elapsed = currentTime - lastResetTime;

            if (elapsed > 60000) {
                blockBreakCount = 0;
                lastResetTime = currentTime;
            }

            int blocksPerMinute = (int) ((blockBreakCount * 60000) / elapsed);
            client.textRenderer.draw(matrixStack, Text.of(blocksPerMinute + " Blocks per minute"), 10, client.getWindow().getScaledHeight() - 20, 0xFFFFFF);
        }
    }
}
