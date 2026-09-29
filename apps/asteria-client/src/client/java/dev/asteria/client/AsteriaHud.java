package dev.asteria.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class AsteriaHud {
	private static final DateTimeFormatter CLOCK_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
	private static double previousX;
	private static double previousZ;
	private static double blocksPerSecond;

	private AsteriaHud() {
	}

	public static void tick(Minecraft client, AsteriaConfig config) {
		if (client.player == null) {
			return;
		}

		double dx = client.player.getX() - previousX;
		double dz = client.player.getZ() - previousZ;
		blocksPerSecond = Math.sqrt(dx * dx + dz * dz) * 20.0;
		previousX = client.player.getX();
		previousZ = client.player.getZ();

		if (config.enabled(HudModule.TOGGLE_SPRINT)
			&& client.options.keyUp.isDown()
			&& !client.player.isCrouching()) {
			client.player.setSprinting(true);
		}

	}

	public static void render(GuiGraphicsExtractor graphics, Minecraft client, AsteriaConfig config) {
		if (client.player == null) {
			return;
		}

		List<String> lines = new ArrayList<>();
		if (config.enabled(HudModule.FPS)) {
			lines.add("FPS: " + client.getFps());
		}
		if (config.enabled(HudModule.COORDINATES)) {
			lines.add(String.format(
				Locale.ROOT,
				"XYZ: %.1f / %.1f / %.1f",
				client.player.getX(),
				client.player.getY(),
				client.player.getZ()
			));
		}
		if (config.enabled(HudModule.PING) && client.player.connection.getPlayerInfo(client.player.getUUID()) != null) {
			lines.add("Ping: " + client.player.connection.getPlayerInfo(client.player.getUUID()).getLatency() + " ms");
		}
		if (config.enabled(HudModule.CLOCK)) {
			lines.add("Time: " + LocalTime.now().format(CLOCK_FORMAT));
		}
		if (config.enabled(HudModule.KEYSTROKES)) {
			lines.add(keyState(client));
		}
		if (config.enabled(HudModule.CPS)) {
			lines.add("CPS: " + AsteriaClient.clicksPerSecond());
		}
		if (config.enabled(HudModule.BIOME)) {
			lines.add("Biome: " + client.player.level().getBiome(client.player.blockPosition()).unwrapKey()
				.map(key -> key.identifier().getPath())
				.orElse("unknown"));
		}
		if (config.enabled(HudModule.DIRECTION)) {
			lines.add("Facing: " + client.player.getDirection().getName());
		}
		if (config.enabled(HudModule.SPEED)) {
			lines.add(String.format(Locale.ROOT, "Speed: %.2f b/s", blocksPerSecond));
		}
		if (config.enabled(HudModule.MEMORY)) {
			Runtime runtime = Runtime.getRuntime();
			long used = (runtime.totalMemory() - runtime.freeMemory()) / 1024L / 1024L;
			long maximum = runtime.maxMemory() / 1024L / 1024L;
			lines.add("Memory: " + used + " / " + maximum + " MB");
		}
		if (config.enabled(HudModule.ARMOR)) {
			lines.add(armorLine(client));
		}
		if (config.enabled(HudModule.EFFECTS)) {
			for (MobEffectInstance effect : client.player.getActiveEffects()) {
				lines.add(effect.getEffect().value().getDisplayName().getString() + " " + (effect.getAmplifier() + 1));
			}
		}

		int x = config.hudX();
		int y = config.hudY();
		for (String line : lines) {
			int lineWidth = client.font.width(line);
			if (config.background()) {
				graphics.fill(x - 2, y - 2, x + lineWidth + 2, y + client.font.lineHeight + 1, 0x88000000);
			}
			graphics.text(client.font, line, x, y, config.hudColor(), true);
			y += client.font.lineHeight + 4;
		}
	}

	private static String keyState(Minecraft client) {
		return String.format(
			Locale.ROOT,
			"Keys: %s %s %s %s | %s",
			client.options.keyUp.isDown() ? "W" : "-",
			client.options.keyLeft.isDown() ? "A" : "-",
			client.options.keyDown.isDown() ? "S" : "-",
			client.options.keyRight.isDown() ? "D" : "-",
			client.options.keyJump.isDown() ? "SPACE" : "-"
		);
	}

	private static String armorLine(Minecraft client) {
		StringBuilder result = new StringBuilder("Armor:");
		for (EquipmentSlot slot : new EquipmentSlot[] {
			EquipmentSlot.HEAD,
			EquipmentSlot.CHEST,
			EquipmentSlot.LEGS,
			EquipmentSlot.FEET,
		}) {
			ItemStack stack = client.player.getItemBySlot(slot);
			if (!stack.isEmpty()) {
				result.append(' ').append(stack.getHoverName().getString());
			}
		}
		return result.toString();
	}
}