package dev.asteria.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class AsteriaMenuScreen extends Screen {
	private final AsteriaConfig config;

	public AsteriaMenuScreen(AsteriaConfig config) {
		super(Component.literal("Asteria Client"));
		this.config = config;
	}

	@Override
	protected void init() {
		int width = 150;
		int gap = 6;
		int columns = Math.max(1, this.width / (width + gap));
		int totalWidth = columns * width + (columns - 1) * gap;
		int startX = (this.width - totalWidth) / 2;
		int startY = 42;

		HudModule[] modules = HudModule.values();
		for (int index = 0; index < modules.length; index++) {
			HudModule module = modules[index];
			int column = index % columns;
			int row = index / columns;
			int x = startX + column * (width + gap);
			int y = startY + row * 26;
			addRenderableWidget(
				Button.builder(label(module), button -> {
					config.toggle(module);
					button.setMessage(label(module));
				}).bounds(x, y, width, 20).build()
			);
		}
	}

	private Component label(HudModule module) {
		String state = config.enabled(module) ? "ON" : "OFF";
		return Component.literal(module.title() + ": " + state);
	}

	@Override
	public void extractRenderState(
		GuiGraphicsExtractor graphics,
		int mouseX,
		int mouseY,
		float partialTick
	) {
		extractTransparentBackground(graphics);
		graphics.centeredText(font, title, width / 2, 16, 0xFFFFFF);
		super.extractRenderState(graphics, mouseX, mouseY, partialTick);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}