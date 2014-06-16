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

package DataStructures.Questionnaire;

import java.util.ArrayList;

/**
 * Created by marcel on 3/25/14.
 * Closed question with a combobox
 */

public class ClosedComboQuestion extends AbstractClosedQuestion {

    private ArrayList<String> options;

    public ClosedComboQuestion() {
        super();
        options = new ArrayList<String>();
    }

    @Override
    public <T> T Accept(IQuestionVisitor<T> visitor) {
        return visitor.Visit(this);
    }

    public void addOptions(String option) {
        options.add(option);
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    @Override
    public void Accept(IVoidQuestionVisitor visitor) {
        visitor.Visit(this);
    }

    @Override
    public AbstractQuestion newInstance() {
        return new ClosedComboQuestion();
    }

    @Override
    public <T extends AbstractQuestion> T convertQuestion(T newQuestion) {
        newQuestion.setKey(this.getKey());
        newQuestion.setQuestion(this.getQuestion());
        newQuestion.setRequired(this.isRequired());
        if (newQuestion instanceof AbstractClosedQuestion) {
            for (String s : this.getOptions()) {
                ((AbstractClosedQuestion) newQuestion).addOptions(s);
            }
        }
        return newQuestion;
    }
}
