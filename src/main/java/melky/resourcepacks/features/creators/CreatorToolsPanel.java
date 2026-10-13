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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchService;

import javax.inject.Inject;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import lombok.extern.slf4j.Slf4j;
import melky.resourcepacks.features.creators.events.SpritePicked;
import melky.resourcepacks.features.creators.events.TrackerBuilt;
import melky.resourcepacks.features.creators.sprites.SpriteOverlay;
import melky.resourcepacks.features.creators.sprites.SpritePicker;
import melky.resourcepacks.features.creators.sprites.SpriteTrackerPanel;
import melky.resourcepacks.features.state.LocalConfig;
import net.runelite.api.Client;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

@Slf4j
public class CreatorToolsPanel extends PluginPanel
{
	private static final String OVERLAY_ICON = "/overlay.png";
	private static final String VISIBILITY_ICON = "/visible.png";
	private static final String PICKER_ICON = "/picker.png";

	private final Client client;
	private final LocalConfig localConfig;
	private final SpriteTrackerPanel spriteTrackerPanel;

	@Inject
	public CreatorToolsPanel(
		Client client,
		LocalConfig localConfig,
		SpriteTrackerPanel spriteTrackerPanel
	)
	{
		super(false);
		this.client = client;
		this.localConfig = localConfig;
		this.spriteTrackerPanel = spriteTrackerPanel;

		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setBackground(ColorScheme.DARK_GRAY_COLOR);

		add(createControlsPanel(), BorderLayout.NORTH);
		add(spriteTrackerPanel, BorderLayout.CENTER);
	}

	private JPanel createControlsPanel()
	{
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		panel.setBackground(ColorScheme.DARK_GRAY_COLOR);
		panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

		JButton spriteOverlayButton = new ToolbarButton(OVERLAY_ICON);
		spriteOverlayButton.setToolTipText("Toggle sprite overlay");
		spriteOverlayButton.addActionListener(e -> toggleSpriteOverlay());

		JButton visibilityButton = new ToolbarButton(VISIBILITY_ICON);
		visibilityButton.setToolTipText("Toggle all sprites");
		visibilityButton.addActionListener(e ->
		{
			spriteTrackerPanel.toggleAllSprites();
		});

		JButton pickerButton = new ToolbarButton(PICKER_ICON);
		pickerButton.setToolTipText("Toggle sprite picker");
		pickerButton.addActionListener(e ->
		{
			localConfig.toggleState(SpritePicker.class);
		});

		panel.add(spriteOverlayButton);
		panel.add(visibilityButton);
		panel.add(pickerButton);

		return panel;
	}

	private void toggleSpriteOverlay()
	{
		localConfig.toggleState(SpriteOverlay.class);
	}


	@Subscribe
	public void onTrackerBuilt(TrackerBuilt event)
	{
		spriteTrackerPanel.rebuild();
	}

	@Subscribe
	public void onSpritePicked(SpritePicked event)
	{
		spriteTrackerPanel.refreshVisibility();
	}

}
