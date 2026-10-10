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
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import lombok.Getter;
import net.runelite.client.ui.ColorScheme;

@Getter
public class SpriteTrackerPanelItem extends JPanel
{
	@Getter
	private final SpriteTracker.HighlightOptions options;

	private final JLabel nameLabel;
	private final JCheckBox visibleCheckbox;
	private Runnable onVisibilityChanged;

	public SpriteTrackerPanelItem(SpriteTracker.HighlightOptions options)
	{
		this.options = options;
		var visible = options.isVisible();

		setLayout(new BorderLayout());
		setBackground(visible ? ColorScheme.DARKER_GRAY_COLOR : ColorScheme.MEDIUM_GRAY_COLOR);
		setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

		nameLabel = new JLabel(options.getOverride().getFileName());
		nameLabel.setForeground(options.getColor());

		visibleCheckbox = new JCheckBox("", visible);
		visibleCheckbox.setBackground(getBackground());
		visibleCheckbox.setFocusPainted(false);
		visibleCheckbox.addChangeListener(c -> applyVisibility(visibleCheckbox.isSelected()));

		add(visibleCheckbox, BorderLayout.WEST);
		add(nameLabel, BorderLayout.CENTER);
	}

	public boolean isSelected()
	{
		return visibleCheckbox.isSelected();
	}

	public void setSelected(boolean selected)
	{
		visibleCheckbox.setSelected(selected);
	}

	void setOnVisibilityChanged(Runnable onVisibilityChanged)
	{
		this.onVisibilityChanged = onVisibilityChanged;
	}

	private void applyVisibility(boolean visible)
	{
		options.setVisible(visible);
		setBackground(visible ? ColorScheme.DARKER_GRAY_COLOR : ColorScheme.MEDIUM_GRAY_COLOR);
		visibleCheckbox.setBackground(getBackground());

		if (onVisibilityChanged != null)
		{
			onVisibilityChanged.run();
		}
	}
}
