package com.trueopsec;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TrueOpsecScreen extends Screen {
	private static final long ANIM_MS = 550;
	private static final int OPT_TOP = 58;

	private final Screen parent;
	private long clickTime = -1;
	private boolean boardOpen = false;
	private boolean syncing = false;

	private Button mainBtn, invisBtn, donutBtn, doneBtn;
	private EditBox nameBox, boardName, moneyBox, killsBox, shardsBox;

	public TrueOpsecScreen(Screen parent) {
		super(Component.literal("True opsec"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		int cx = width / 2;
		int w = 200;
		int x = cx - w / 2;

		mainBtn = addRenderableWidget(Button.builder(Component.literal("True opsec"), b -> {
			if (clickTime < 0) clickTime = System.currentTimeMillis();
		}).bounds(cx - 60, height / 2 - 10, 120, 20).build());

		nameBox = box(x, OPT_TOP, w, "Fake username");
		nameBox.setValue(Spoof.name);
		nameBox.setResponder(s -> {
			Spoof.name = s;
			if (!syncing) { syncing = true; boardName.setValue(s); syncing = false; }
		});

		invisBtn = addRenderableWidget(Button.builder(invisLabel(), b -> {
			Spoof.invisible = !Spoof.invisible;
			b.setMessage(invisLabel());
		}).bounds(x, OPT_TOP + 34, w, 20).build());

		donutBtn = addRenderableWidget(Button.builder(donutLabel(), b -> {
			boardOpen = !boardOpen;
			b.setMessage(donutLabel());
		}).bounds(x, OPT_TOP + 60, w, 20).build());

		boardName = box(x, OPT_TOP + 86, w, "Name");
		boardName.setValue(Spoof.name);
		boardName.setResponder(s -> {
			Spoof.name = s;
			if (!syncing) { syncing = true; nameBox.setValue(s); syncing = false; }
		});

		moneyBox = box(x, OPT_TOP + 110, w, "Money (e.g. 5.2M)");
		moneyBox.setValue(Spoof.money);
		moneyBox.setResponder(s -> Spoof.money = s);

		killsBox = box(x, OPT_TOP + 134, w, "Kills");
		killsBox.setValue(Spoof.kills);
		killsBox.setResponder(s -> Spoof.kills = s);

		shardsBox = box(x, OPT_TOP + 158, w, "Shards");
		shardsBox.setValue(Spoof.shards);
		shardsBox.setResponder(s -> Spoof.shards = s);

		doneBtn = addRenderableWidget(Button.builder(Component.literal("Done"), b -> onClose())
				.bounds(cx - 50, height - 28, 100, 20).build());

		layout(progress());
	}

	private EditBox box(int x, int y, int w, String hint) {
		EditBox b = new EditBox(font, x, y, w, 20, Component.literal(hint));
		b.setMaxLength(32);
		b.setHint(Component.literal(hint));
		return addRenderableWidget(b);
	}

	private Component invisLabel() {
		return Component.literal("Character invisibility: " + (Spoof.invisible ? "ON" : "OFF"));
	}

	private Component donutLabel() {
		return Component.literal("Donut board  " + (boardOpen ? "\u25B2" : "\u25BC"));
	}

	private float progress() {
		if (clickTime < 0) return 0f;
		return Math.min(1f, (System.currentTimeMillis() - clickTime) / (float) ANIM_MS);
	}

	private static float ease(float t) {
		float u = 1f - t;
		return 1f - u * u * u;
	}

	private void layout(float p) {
		int startY = height / 2 - 10;
		int endY = 20;
		mainBtn.setY(Math.round(startY + (endY - startY) * ease(p)));

		float a = Math.max(0f, Math.min(1f, (p - 0.55f) / 0.45f));
		boolean show = a > 0f;
		for (AbstractWidget w : new AbstractWidget[] { nameBox, invisBtn, donutBtn, doneBtn }) {
			w.visible = show;
			w.active = a >= 1f;
			w.setAlpha(a);
		}
		for (AbstractWidget w : new AbstractWidget[] { boardName, moneyBox, killsBox, shardsBox }) {
			w.visible = show && boardOpen;
			w.active = a >= 1f && boardOpen;
			w.setAlpha(a);
		}
	}

	@Override
	public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
		float p = progress();
		layout(p);
		super.render(g, mouseX, mouseY, delta);

		float a = Math.max(0f, Math.min(1f, (p - 0.55f) / 0.45f));
		if (a > 0.05f) {
			int col = ((int) (a * 255) << 24) | 0xFFFFFF;
			g.drawString(font, "Username changer (only you see it)", width / 2 - 100, OPT_TOP - 11, col, true);
		}
	}

	@Override
	public void onClose() {
		Spoof.save();
		minecraft.setScreen(parent);
	}
}
