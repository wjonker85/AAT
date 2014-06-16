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

/**
 * Created by marcel on 3/25/14.
 */
public class TextAreaPanel extends AbstractQuestionPanel {

    JTextArea textArea;

    public TextAreaPanel(boolean isRequired) {
        super();
        this.isRequired = isRequired;
        textArea = new JTextArea(10, 40);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane2 = new JScrollPane(textArea);
        scrollPane2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        add(scrollPane2);
        setBackground(Color.black);
        setForeground(Color.white);
        textArea.setBackground(Color.WHITE);
        textArea.setForeground(Color.BLACK);
        if (isRequired) {
            add(asterisks);
        }
    }


    @Override
    public String getValue() {
        return textArea.getText();
    }

    @Override
    public String getType() {
        return "textArea";
    }
}
