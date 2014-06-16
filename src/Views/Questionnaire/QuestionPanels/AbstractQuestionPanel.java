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
 * Created by marcel on 3/16/14.
 * This class contains the functionality to display the different types of questions in the Questionnaire.
 */
public abstract class AbstractQuestionPanel extends JPanel {

    public JLabel asterisks;
    public boolean isRequired = true;

    public AbstractQuestionPanel() {
        asterisks = new JLabel("*", JLabel.TRAILING);
        asterisks.setFont(new Font("Roman", Font.BOLD, 20));
        asterisks.setForeground(Color.WHITE);
        setOpaque(false);
    }

    public abstract String getValue();

    public abstract String getType();

    public void setRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    public void changeAsteriskColor(Color c) {
        asterisks.setForeground(c);
    }
}








