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
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;
import javax.inject.Inject;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import lombok.extern.slf4j.Slf4j;
import static melky.resourcepacks.features.creators.SpriteTracker.TRACKER_DEFAULT_VISIBLE;
import melky.resourcepacks.features.state.LocalConfig;
import melky.resourcepacks.model.SpriteOverride.Folder;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

@Slf4j
public class SpriteTrackerPanel extends PluginPanel
{
	private final SpriteTracker spriteTracker;
	private final LocalConfig localConfig;
	private final Map<Folder, SpriteTrackerGroup> groups = new LinkedHashMap<>();
	private final Map<Integer, SpriteTrackerPanelItem> itemsBySpriteId = new HashMap<>();

	private boolean showAllSprites = TRACKER_DEFAULT_VISIBLE;
	private JPanel spriteListPanel;
	private JButton selectorButton;

	@Inject
	public SpriteTrackerPanel(SpriteTracker spriteTracker, LocalConfig localConfig)
	{
		super(false);

		this.spriteTracker = spriteTracker;
		this.localConfig = localConfig;

		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setBackground(ColorScheme.DARK_GRAY_COLOR);

		add(createToolbar(), BorderLayout.NORTH);
		add(createSpriteListPanel(), BorderLayout.CENTER);

		updateAllSpriteVisibility(showAllSprites);
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
			updateAllSpriteVisibility(showAllSprites);
			updateSpriteList();
		});

		selectorButton = new JButton(localConfig.getState(SpritePicker.class) ? "Remove Picker" : "Add Picker");
		selectorButton.setFocusPainted(false);
		selectorButton.setAlignmentX(0.5f);
		selectorButton.addActionListener(e -> {
			localConfig.toggleState(SpritePicker.class);
			selectorButton.setText(localConfig.getState(SpritePicker.class) ? "Remove Picker" : "Add Picker");
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

	public void updateSpriteList()
	{
		spriteListPanel.removeAll();
		groups.clear();
		itemsBySpriteId.clear();

		for (var highlight : spriteTracker.getHighlightList())
		{
			var group = groups.computeIfAbsent(highlight.getOverride().getFolder(), SpriteTrackerGroup::new);
			var item = new SpriteTrackerPanelItem(highlight);
			group.addItem(item);
			itemsBySpriteId.put(highlight.getSpriteId(), item);
		}

		var sortedFolders = new TreeSet<>(Comparator.comparing(Folder::name));
		sortedFolders.addAll(groups.keySet());

		for (var folder : sortedFolders)
		{
			var group = groups.get(folder);
			group.refreshGroupState();
			spriteListPanel.add(group);
		}

		spriteListPanel.revalidate();
		spriteListPanel.repaint();
	}

	private void updateAllSpriteVisibility(boolean visible)
	{
		for (var highlight : spriteTracker.getHighlightList())
		{
			highlight.setVisible(visible);
		}
	}

	public boolean isSpriteVisible(int spriteId)
	{
		return false;
	}

	@Override
	public void onActivate()
	{
	}

	@Override
	public void onDeactivate()
	{
		if (localConfig.getState(SpritePicker.class))
		{
			localConfig.setState(SpritePicker.class, false);
			selectorButton.setText("Add Picker");
		}
	}

	public void refreshVisibility()
	{
		for (var item : itemsBySpriteId.values())
		{
			item.setSelected(item.getOptions().isVisible());
		}
	}

	void rebuild()
	{
		boolean hasNew = false;
		for (var highlight : spriteTracker.getHighlightList())
		{
			if (!itemsBySpriteId.containsKey(highlight.getSpriteId()))
			{
				hasNew = true;
				break;
			}
		}

		if (hasNew)
		{
			updateSpriteList();
		}
	}

	void clear()
	{
		spriteListPanel.removeAll();
		groups.clear();
		itemsBySpriteId.clear();
		spriteListPanel.revalidate();
		spriteListPanel.repaint();
	}
}
