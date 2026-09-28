package dev.asteria.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

import java.util.ArrayDeque;
import java.util.Deque;

public final class AsteriaClient implements ClientModInitializer {
	private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
		Identifier.fromNamespaceAndPath("asteria-client", "controls")
	);
	private static final Deque<Long> CLICKS = new ArrayDeque<>();
	private static AsteriaConfig config;
	private static boolean attackPressed;

	@Override
	public void onInitializeClient() {
		config = AsteriaConfig.load();
		KeyMapping menuKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
			"key.asteria-client.menu",
			InputConstants.Type.KEYBOARD,
			344,
			CATEGORY
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (menuKey.consumeClick()) {
				client.setScreenAndShow(new AsteriaMenuScreen(config));
			}

			boolean currentAttack = client.options.keyAttack.isDown();
			if (currentAttack && !attackPressed) {
				CLICKS.addLast(System.currentTimeMillis());
			}
			attackPressed = currentAttack;
			pruneClicks();
			AsteriaHud.tick(client, config);
		});

		HudElementRegistry.attachElementAfter(
			VanillaHudElements.MISC_OVERLAYS,
			Identifier.fromNamespaceAndPath("asteria-client", "main_hud"),
			(graphics, tickCounter) -> AsteriaHud.render(graphics, Minecraft.getInstance(), config)
		);
	}

	public static int clicksPerSecond() {
		pruneClicks();
		return CLICKS.size();
	}

	private static void pruneClicks() {
		long cutoff = System.currentTimeMillis() - 1000L;
		while (!CLICKS.isEmpty() && CLICKS.peekFirst() < cutoff) {
			CLICKS.removeFirst();
		}
	}
}