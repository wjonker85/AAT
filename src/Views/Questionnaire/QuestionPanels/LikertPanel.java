/** This file is part of Approach Avoidance Task.
 *
 * Approach Avoidance Task is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Approach Avoidance Task is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Approach Avoidance Task.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package Views.Questionnaire.QuestionPanels;

import javax.swing.*;
import java.awt.*;
import java.util.Enumeration;

/**
 * Created by marcel on 3/25/14.
 */
public class LikertPanel extends AbstractQuestionPanel {

    private ButtonGroup likertScale;

    public LikertPanel(int size, String left, int leftLabelWidth, String right, boolean isRequired) {
        super();
        this.isRequired = isRequired;
        setLayout(new FlowLayout(FlowLayout.LEFT));
        this.setBackground(Color.black);
        this.setForeground(Color.white);

        JLabel leftLabel = new JLabel(left, JLabel.RIGHT);
        leftLabel.setForeground(Color.WHITE);
        leftLabel.setBackground(Color.black);
        leftLabel.setPreferredSize(new Dimension(leftLabelWidth, 20));
        this.add(leftLabel);
        this.add(Box.createHorizontalStrut(20));//20px margin
        likertScale = new ButtonGroup();
        JPanel buttonPanel = new JPanel(new GridLayout(2, size, 20, 0));
        buttonPanel.setForeground(Color.white);
        buttonPanel.setBackground(Color.black);
        for (int x = 0; x < size; x++) {
            JLabel label = new JLabel(String.valueOf(x + 1), JLabel.CENTER);
            label.setForeground(Color.white);
            buttonPanel.add(label);
        }

        for (int x = 0; x < size; x++) {
            JRadioButton likertButton = new JRadioButton();
            likertButton.setBackground(Color.black);
            likertButton.setForeground(Color.white);

            buttonPanel.add(likertButton);
            likertScale.add(likertButton);
        }
        this.add(buttonPanel);
        this.add(Box.createHorizontalStrut(20));//20px margin
        JLabel rightLabel = new JLabel(right, JLabel.LEFT);
        //    rightLabel.setPreferredSize(new Dimension(200,30));
        rightLabel.setForeground(Color.white);
        rightLabel.setBackground(Color.black);
        this.add(rightLabel);

        if (isRequired) {
            add(asterisks);
        }
    }

    public String getValue() {
        int x = 1;
        for (Enumeration e = likertScale.getElements(); e.hasMoreElements(); ) {
            JRadioButton b = (JRadioButton) e.nextElement();
            if (b.isSelected()) {

                return String.valueOf(x);
            }
            x++;
        }
        return "";
    }

    @Override
    public String getType() {
        return "likert";
    }
}
