package dev.asteria.client;

public enum HudModule {
	FPS("FPS"),
	COORDINATES("Coordinates"),
	PING("Ping"),
	CLOCK("Clock"),
	KEYSTROKES("Keystrokes"),
	CPS("CPS"),
	ARMOR("Armor"),
	EFFECTS("Potion effects"),
	BIOME("Biome"),
	DIRECTION("Direction"),
	SPEED("Speed"),
	MEMORY("Memory"),
	TOGGLE_SPRINT("Toggle sprint");

	private final String title;

	HudModule(String title) {
		this.title = title;
	}

	public String title() {
		return title;
	}
}