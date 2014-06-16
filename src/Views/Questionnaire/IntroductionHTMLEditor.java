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

package Views.Questionnaire;

import net.atlanticbb.tantlinger.shef.HTMLEditorPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by marcel on 4/22/14.
 * Frame that contains an html editor. With this editor it is possible to change the introduction text of the questionnaire
 */
public class IntroductionHTMLEditor extends JFrame {

    private HTMLEditorPane htmlEditorPane;

    public IntroductionHTMLEditor(final DisplayQuestionnairePanel qPanel) {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        htmlEditorPane = new HTMLEditorPane();
        htmlEditorPane.setBackground(Color.black);
        htmlEditorPane.setPreferredSize(new Dimension(800, 400));
        htmlEditorPane.setMaximumSize(new Dimension(800, 400));
        content.setPreferredSize(new Dimension(800, 400));
        content.setMaximumSize(new Dimension(800, 400));
        htmlEditorPane.setBackground(Color.decode("#eeece9"));

        JLabel intro = new JLabel("Introduction Text for the questionnaire.");
        intro.setAlignmentX(Component.CENTER_ALIGNMENT);
        Font f = intro.getFont();
        intro.setFont(new Font(f.getName(), Font.PLAIN, 18));
        content.add(Box.createVerticalStrut(20));
        content.add(intro, LEFT_ALIGNMENT);
        content.add(Box.createVerticalStrut(20));
        content.add(htmlEditorPane);
        content.add(Box.createVerticalStrut(20));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                qPanel.ChangeIntroduction(htmlEditorPane.getText());
                dispose();
            }
        });
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                dispose();
            }
        });
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        content.add(Box.createVerticalStrut(20));
        content.add(buttonPanel);
        this.getContentPane().add(content);
        pack();
    }

    public void Show(String text) {
        this.setEnabled(true);
        this.setVisible(true);
        this.setTitle("Change introduction text");
        htmlEditorPane.setText("<body>" + text + "</body>");
        this.setPreferredSize(new Dimension(800, 800));
    }


    public String getText() {
        return htmlEditorPane.getText();
    }
}
