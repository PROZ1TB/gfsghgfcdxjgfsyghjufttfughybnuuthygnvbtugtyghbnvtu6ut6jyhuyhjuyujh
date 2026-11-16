package com.trueopsec;

import net.minecraft.client.gui.GuiGraphics;

/** The 16x16 Anonymous-style logo, drawn pixel by pixel (no texture needed). '.' = transparent. */
public final class Logo {
	private static final String[] ROWS = {
		"....cabbbbac....",
		"...acffffffca...",
		"..befffeefffeb..",
		".bc9feeeeeef9cb.",
		"da5bfeecaeefb5ad",
		"a66efffc9fffe66a",
		"959feefddfeef959",
		"86bfefeaaefefb68",
		"85bfe52aa25efb58",
		"859fd008800df958",
		"a65dc000000cd56a",
		"d85790000009758d",
		".a754000000457a.",
		"..a8630000368a..",
		"...a97000079a...",
		"....d920029d...."
	};

	public static void draw(GuiGraphics g, int x, int y) {
		for (int j = 0; j < 16; j++) {
			for (int i = 0; i < 16; i++) {
				char c = ROWS[j].charAt(i);
				if (c == '.') continue;
				int v = Character.digit(c, 16) * 17;
				g.fill(x + i, y + j, x + i + 1, y + j + 1, 0xFF000000 | (v << 16) | (v << 8) | v);
			}
		}
	}

	private Logo() {}
}
