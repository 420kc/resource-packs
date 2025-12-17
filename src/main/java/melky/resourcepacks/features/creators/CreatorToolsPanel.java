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
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchService;
import javax.inject.Inject;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import lombok.extern.slf4j.Slf4j;
import melky.resourcepacks.features.creators.events.TrackerBuilt;
import melky.resourcepacks.features.state.LocalConfig;
import net.runelite.api.Client;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
public class CreatorToolsPanel extends PluginPanel
{
	private final Client client;
	private final LocalConfig localConfig;
	private final SpriteTrackerPanel spriteTrackerPanel;

	private boolean fileWatcherEnabled = false;
	private WatchService watchService;

	private JPanel spriteComparisonPanel;
	private BufferedImage selectedOriginalSprite;
	private BufferedImage selectedPackSprite;

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
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBackground(ColorScheme.DARK_GRAY_COLOR);
		panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

		JButton fileWatcherButton = new JButton("Enable File Watcher");
		fileWatcherButton.setFocusPainted(false);
		fileWatcherButton.addActionListener(e -> toggleFileWatcher());

		JButton spriteOverlayButton = new JButton("Toggle Sprite Overlay");
		spriteOverlayButton.setFocusPainted(false);
		spriteOverlayButton.addActionListener(e -> toggleSpriteOverlay());

		panel.add(fileWatcherButton);
		panel.add(Box.createRigidArea(new Dimension(0, 5)));
		panel.add(spriteOverlayButton);

		return panel;
	}

	private JPanel createSpriteComparisonPanel()
	{
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(ColorScheme.DARK_GRAY_COLOR);
		panel.setVisible(false);

		JLabel titleLabel = new JLabel("Sprite Comparison");
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

		spriteComparisonPanel = new JPanel();
		spriteComparisonPanel.setLayout(new BoxLayout(spriteComparisonPanel, BoxLayout.X_AXIS));
		spriteComparisonPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		spriteComparisonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JScrollPane scrollPane = new JScrollPane(spriteComparisonPanel);
		scrollPane.setPreferredSize(new Dimension(0, 200));

		panel.add(titleLabel, BorderLayout.NORTH);
		panel.add(scrollPane, BorderLayout.CENTER);

		return panel;
	}

	private void toggleFileWatcher()
	{
		fileWatcherEnabled = !fileWatcherEnabled;
		if (fileWatcherEnabled)
		{
			startFileWatcher();
		}
		else
		{
			stopFileWatcher();
		}
	}

	private void toggleSpriteOverlay()
	{
		localConfig.toggleState(SpriteOverlay.class);
	}

	private void startFileWatcher()
	{
		try
		{
			Path watchPath = Paths.get(System.getProperty("user.home"), ".runelite", "resource-packs");
			// Setup WatchService to monitor directory changes
			log.info("Starting file watcher for: {}", watchPath);
		}
		catch (Exception e)
		{
			log.error("Failed to start file watcher", e);
		}
	}

	private void stopFileWatcher()
	{
		if (watchService != null)
		{
			try
			{
				watchService.close();
				watchService = null;
				log.info("File watcher stopped");
			}
			catch (Exception e)
			{
				log.error("Failed to stop file watcher", e);
			}
		}
	}


	@Subscribe
	public void onTrackerBuilt(TrackerBuilt event)
	{
		spriteTrackerPanel.rebuild();
	}
}