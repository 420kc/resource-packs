/*
 * Copyright (c) 2025, Ron Young <https://github.com/raiyni>
 * All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package melky.resourcepacks.features.creators;

import java.awt.Color;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import melky.resourcepacks.ResourcePacksConfig;
import melky.resourcepacks.features.state.LocalConfig;
import melky.resourcepacks.model.SpriteOverride;
import melky.resourcepacks.module.PluginLifecycleComponent;
import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.eventbus.Subscribe;

@Singleton
@Slf4j
public class SpriteTracker implements PluginLifecycleComponent
{
	public enum TitleMode
	{
		INLINE,
		SPIDERWEB,
		CONDENSED,
		HIDDEN;
	}

	@Data
	@AllArgsConstructor
	public static class HighlightOptions
	{
		private SpriteOverride override;
		private Color color;
		private boolean visible;
	}

	@Inject
	private Client client;

	@Getter
	private final Set<Widget> widgets = new LinkedHashSet<>();

	@Getter
	private final Map<Integer, HighlightOptions> highlights = new ConcurrentHashMap<>();

	private final Map<Integer, SpriteOverride> spriteOverrides = new HashMap<>();

	private int ticksSince = 0;


	@Override
	public boolean isEnabled(ResourcePacksConfig config, LocalConfig localConfig)
	{
		return config.developerTools() && localConfig.getState(SpriteOverlay.class);
	}

	public boolean isEmpty()
	{
		return widgets.isEmpty();
	}

	public void computeTracking(Widget w)
	{
		Integer key = w.getSpriteId();
		if (spriteOverrides.containsKey(key))
		{
			widgets.add(w);

			if (!highlights.containsKey(key))
			{
				var override = spriteOverrides.get(key);
				highlights.put(key, new HighlightOptions(override, getColorFromHashCode(override), true));
			}
		}
	}

	public HighlightOptions getHighlight(int spriteId)
	{
		return highlights.get(spriteId);
	}

	private Color getColorFromHashCode(Object obj)
	{
		if (obj == null)
		{
			return Color.WHITE;
		}

		int hash = obj.hashCode();

		// Use different bits of the hash for RGB components
		int r = (hash & 0xFF0000) >> 16;
		int g = (hash & 0x00FF00) >> 8;
		int b = hash & 0x0000FF;

		return new Color(r, g, b);
	}

	@Override
	public void startUp()
	{
		for (var s : SpriteOverride.values())
		{
			if (s.getSpriteID() != -1)
			{
				spriteOverrides.put(s.getSpriteID(), s);
			}
		}
	}

	@Override
	public void shutDown()
	{
		widgets.clear();
		spriteOverrides.clear();
	}


	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (ticksSince++ < 5)
		{
			return;
		}

		ticksSince = 0;
		widgets.clear();
		var widgets = client.getWidgetRoots();
		for (var w : widgets)
		{
			walkWidget(w);
		}
	}

	private void walkWidget(Widget parent)
	{
		if (parent.getType() == WidgetType.GRAPHIC)
		{
			computeTracking(parent);
			return;
		}

		for (var w : parent.getStaticChildren())
		{
			walkWidget(w);
		}

		for (var w : parent.getNestedChildren())
		{
			walkWidget(w);
		}

		for (var w : parent.getDynamicChildren())
		{
			walkWidget(w);
		}
	}
}
