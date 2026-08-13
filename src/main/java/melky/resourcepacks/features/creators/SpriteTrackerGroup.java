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
import java.awt.Graphics;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import lombok.Getter;
import melky.resourcepacks.model.SpriteOverride.Folder;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;

public class SpriteTrackerGroup extends JPanel
{
	@Getter
	private final Folder folder;

	private final TriStateCheckBox groupCheckbox;
	private final JPanel itemsPanel;
	private final List<SpriteTrackerPanelItem> items = new ArrayList<>();

	public SpriteTrackerGroup(Folder folder)
	{
		this.folder = folder;

		setLayout(new BorderLayout());
		setBackground(ColorScheme.DARKER_GRAY_COLOR);
		setAlignmentX(LEFT_ALIGNMENT);

		JPanel header = new JPanel(new BorderLayout());
		header.setBackground(ColorScheme.DARK_GRAY_COLOR);
		header.setBorder(BorderFactory.createEmptyBorder(3, 4, 3, 4));

		groupCheckbox = new TriStateCheckBox();
		groupCheckbox.setBackground(ColorScheme.DARK_GRAY_COLOR);
		groupCheckbox.setForeground(Color.WHITE);
		groupCheckbox.setFocusPainted(false);
		groupCheckbox.addActionListener(e -> toggleGroup());

		JLabel folderLabel = new JLabel(folder.name());
		folderLabel.setFont(FontManager.getRunescapeBoldFont());
		folderLabel.setForeground(Color.WHITE);

		header.add(groupCheckbox, BorderLayout.WEST);
		header.add(folderLabel, BorderLayout.CENTER);

		itemsPanel = new JPanel();
		itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
		itemsPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		itemsPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

		add(header, BorderLayout.NORTH);
		add(itemsPanel, BorderLayout.CENTER);
	}

	public void addItem(SpriteTrackerPanelItem item)
	{
		items.add(item);
		itemsPanel.add(item);
		item.setOnVisibilityChanged(this::refreshGroupState);
	}

	private void toggleGroup()
	{
		boolean allSelected = items.stream().allMatch(SpriteTrackerPanelItem::isSelected);
		boolean newState = !allSelected;

		for (var item : items)
		{
			item.setSelected(newState);
		}

		refreshGroupState();
	}

	public void refreshGroupState()
	{
		boolean anySelected = items.stream().anyMatch(SpriteTrackerPanelItem::isSelected);
		boolean allSelected = items.stream().allMatch(SpriteTrackerPanelItem::isSelected);

		groupCheckbox.setSelected(allSelected);
		groupCheckbox.setIndeterminate(!allSelected && anySelected);
	}

	@Override
	public Dimension getMaximumSize()
	{
		return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
	}

	private static class TriStateCheckBox extends JCheckBox
	{
		private boolean indeterminate;

		@Override
		public void setSelected(boolean selected)
		{
			super.setSelected(selected);
			indeterminate = false;
			repaint();
		}

		public void setIndeterminate(boolean indeterminate)
		{
			if (this.indeterminate != indeterminate)
			{
				this.indeterminate = indeterminate;
				repaint();
			}
		}

		@Override
		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);

			if (indeterminate)
			{
				int size = getCheckBoxIconSize();
				Insets insets = getInsets();
				int x = insets.left + size / 2;
				int y = getHeight() / 2;
				int half = Math.max(2, size / 5);

				g.setColor(getForeground());
				g.fillRect(x - half, y - half, half * 2, half * 2);
			}
		}

		private static int getCheckBoxIconSize()
		{
			Icon icon = UIManager.getIcon("CheckBox.icon");
			return icon == null ? 13 : icon.getIconWidth();
		}
	}
}
