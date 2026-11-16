package com.trueopsec;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.network.chat.Component;

public class TrueOpsecClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		Spoof.load();

		ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
			if (!(screen instanceof JoinMultiplayerScreen)) return;

			String editText = Component.translatable("selectServer.edit").getString();
			AbstractWidget edit = null;
			for (AbstractWidget w : Screens.getButtons(screen)) {
				if (w.getMessage().getString().equals(editText)) {
					edit = w;
					break;
				}
			}

			int size = 20;
			int x = edit != null ? edit.getX() - size - 4 : width / 2 - 154 - size - 4;
			int y = edit != null ? edit.getY() : height - 52;

			Button btn = Button.builder(Component.empty(), b -> client.setScreen(new TrueOpsecScreen(screen)))
					.bounds(x, y, size, size)
					.build();
			Screens.getButtons(screen).add(btn);

			ScreenEvents.afterRender(screen).register((s, graphics, mouseX, mouseY, tickDelta) ->
					Logo.draw(graphics, btn.getX() + 2, btn.getY() + 2));
		});
	}
}
