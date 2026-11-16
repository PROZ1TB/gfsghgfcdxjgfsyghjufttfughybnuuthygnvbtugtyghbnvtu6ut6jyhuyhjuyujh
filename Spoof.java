package com.trueopsec;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

/** All spoof state (client-side only) plus the text rewriting logic. */
public final class Spoof {
	public static String name = "";
	public static String money = "";
	public static String kills = "";
	public static String shards = "";
	public static boolean invisible = false;

	// Words that identify a scoreboard line. Edit these if Donut changes its sidebar text.
	private static final String KEY_MONEY = "money";
	private static final String KEY_KILLS = "kills";
	private static final String KEY_SHARDS = "shards";

	private static final Pattern NUMBER = Pattern.compile("\\d[\\d,.]*[KkMmBbTt]?");

	private static Path file() {
		return FabricLoader.getInstance().getConfigDir().resolve("trueopsec.properties");
	}

	public static void load() {
		Path p = file();
		if (!Files.exists(p)) return;
		try (InputStream in = Files.newInputStream(p)) {
			Properties props = new Properties();
			props.load(in);
			name = props.getProperty("name", "");
			money = props.getProperty("money", "");
			kills = props.getProperty("kills", "");
			shards = props.getProperty("shards", "");
			invisible = Boolean.parseBoolean(props.getProperty("invisible", "false"));
		} catch (IOException ignored) {
		}
	}

	public static void save() {
		Properties props = new Properties();
		props.setProperty("name", name);
		props.setProperty("money", money);
		props.setProperty("kills", kills);
		props.setProperty("shards", shards);
		props.setProperty("invisible", Boolean.toString(invisible));
		try (OutputStream out = Files.newOutputStream(file())) {
			props.store(out, "True Opsec (client-side only)");
		} catch (IOException ignored) {
		}
	}

	public static boolean active() {
		return !name.isEmpty() || !money.isEmpty() || !kills.isEmpty() || !shards.isEmpty();
	}

	private record Seg(Style style, String text) {}

	/** Rewrites names and Donut board values inside a component, keeping colours and styles. */
	public static MutableComponent rewrite(MutableComponent in) {
		if (!active()) return in;
		try {
			List<Seg> segs = new ArrayList<>();
			in.<Boolean>visit((style, text) -> {
				segs.add(new Seg(style, text));
				return Optional.empty();
			}, Style.EMPTY);

			StringBuilder full = new StringBuilder();
			for (Seg s : segs) full.append(s.text());
			String lower = full.toString().toLowerCase();
			boolean changed = false;

			String real = Minecraft.getInstance().getUser().getName();
			if (!name.isEmpty() && real != null && !real.isEmpty() && full.indexOf(real) >= 0) {
				for (int i = 0; i < segs.size(); i++) {
					Seg s = segs.get(i);
					if (s.text().contains(real)) {
						segs.set(i, new Seg(s.style(), s.text().replace(real, name)));
						changed = true;
					}
				}
			}

			changed |= replaceValue(segs, lower, KEY_MONEY, money);
			changed |= replaceValue(segs, lower, KEY_KILLS, kills);
			changed |= replaceValue(segs, lower, KEY_SHARDS, shards);

			if (!changed) return in;
			MutableComponent out = Component.empty();
			for (Seg s : segs) out.append(Component.literal(s.text()).setStyle(s.style()));
			return out;
		} catch (Throwable t) {
			return in;
		}
	}

	private static boolean replaceValue(List<Seg> segs, String lowerFull, String key, String value) {
		if (value.isEmpty() || !lowerFull.contains(key)) return false;
		for (int i = segs.size() - 1; i >= 0; i--) {
			Seg s = segs.get(i);
			Matcher m = NUMBER.matcher(s.text());
			if (m.find()) {
				String replaced = s.text().substring(0, m.start()) + value + s.text().substring(m.end());
				segs.set(i, new Seg(s.style(), replaced));
				return true;
			}
		}
		return false;
	}

	private Spoof() {}
}
