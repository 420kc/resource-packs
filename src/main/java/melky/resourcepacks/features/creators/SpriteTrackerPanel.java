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
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import melky.resourcepacks.model.SpriteOverride;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

@Slf4j
public class SpriteTrackerPanel extends PluginPanel
{
	private final SpriteTracker spriteTracker;

	private boolean showAllSprites = true;
	private JPanel spriteListPanel;

	@Inject
	public SpriteTrackerPanel(SpriteTracker spriteTracker)
	{
		super(false);

		this.spriteTracker = spriteTracker;

		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setBackground(ColorScheme.DARK_GRAY_COLOR);

		add(createToolbar(), BorderLayout.NORTH);
		add(createSpriteListPanel(), BorderLayout.CENTER);

		// Initialize visibility for all sprites
		updateAllSpriteVisibility();
		updateSpriteList();
	}

	private JPanel createToolbar()
	{
		JPanel toolbar = new JPanel();
		toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.Y_AXIS));
		toolbar.setBackground(ColorScheme.DARK_GRAY_COLOR);
		toolbar.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

		JButton toggleAllButton = new JButton(showAllSprites ? "Hide All" : "Show All");
		toggleAllButton.setFocusPainted(false);
		toggleAllButton.setAlignmentX(0.5f);
		toggleAllButton.addActionListener(e -> {
			showAllSprites = !showAllSprites;
			toggleAllButton.setText(showAllSprites ? "Hide All" : "Show All");
			updateAllSpriteVisibility();
			updateSpriteList();
		});

		JButton selectorButton = new JButton("Select Sprite");
		selectorButton.setFocusPainted(false);
		selectorButton.setAlignmentX(0.5f);
		selectorButton.addActionListener(e -> {
			// Enable sprite selector mode
			log.info("Sprite selector enabled");
		});

		toolbar.add(toggleAllButton);
		toolbar.add(Box.createRigidArea(new Dimension(0, 5)));
		toolbar.add(selectorButton);

		return toolbar;
	}

	private JPanel createSpriteListPanel()
	{
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(ColorScheme.DARK_GRAY_COLOR);

		JLabel titleLabel = new JLabel("Tracked Sprites:");
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

		spriteListPanel = new JPanel();
		spriteListPanel.setLayout(new BoxLayout(spriteListPanel, BoxLayout.Y_AXIS));
		spriteListPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		JScrollPane scrollPane = new JScrollPane(spriteListPanel);
		scrollPane.setPreferredSize(new Dimension(0, 400));

		panel.add(titleLabel, BorderLayout.NORTH);
		panel.add(scrollPane, BorderLayout.CENTER);

		return panel;
	}

	private void updateSpriteList()
	{
		spriteListPanel.removeAll();

		for (SpriteOverride sprite : SpriteOverride.values())
		{
			SpriteTrackerPanelItem item = new SpriteTrackerPanelItem(sprite, true);
			item.getVisibleCheckbox().addActionListener(e -> {
				boolean selected = item.getVisibleCheckbox().isSelected();
				item.setVisible(selected, false);
			});

			spriteListPanel.add(item);
		}

		spriteListPanel.revalidate();
		spriteListPanel.repaint();
	}

	private void updateAllSpriteVisibility()
	{
	}

	public boolean isSpriteVisible(int spriteId)
	{
		return false;
	}

	@Override
	public void onActivate()
	{
		// Panel activated
	}

	@Override
	public void onDeactivate()
	{
		// Panel deactivated
	}
}
