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
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import lombok.Getter;
import lombok.Setter;
import melky.resourcepacks.model.SpriteOverride;
import net.runelite.client.ui.ColorScheme;

@Getter
public class SpriteTrackerPanelItem extends JPanel
{
	private final SpriteOverride sprite;
	@Setter
	private boolean visible;

	private final JLabel nameLabel;
	private final JCheckBox visibleCheckbox;

	public SpriteTrackerPanelItem(SpriteOverride sprite, boolean visible)
	{
		this.sprite = sprite;
		this.visible = visible;

		setLayout(new BorderLayout());
		setBackground(visible ? ColorScheme.DARKER_GRAY_COLOR : new Color(60, 60, 60));
		setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 30));
		setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 5, 2, 5));

		nameLabel = new JLabel(sprite.name() + " (" + sprite.getSpriteID() + ")");
		nameLabel.setForeground(visible ? Color.WHITE : Color.GRAY);

		visibleCheckbox = new JCheckBox("", visible);
		visibleCheckbox.setBackground(getBackground());
		visibleCheckbox.setFocusPainted(false);

		add(nameLabel, BorderLayout.CENTER);
		add(visibleCheckbox, BorderLayout.EAST);
	}

	public void setVisible(boolean visible, boolean updateCheckbox)
	{
		this.visible = visible;
		setBackground(visible ? ColorScheme.DARKER_GRAY_COLOR : new Color(60, 60, 60));
		nameLabel.setForeground(visible ? Color.WHITE : Color.GRAY);

		if (updateCheckbox)
		{
			visibleCheckbox.setSelected(visible);
		}
	}
}
