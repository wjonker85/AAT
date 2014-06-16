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

/**
 * Created by marcel on 3/25/14.
 * Visitor pattern for questionnaire.
 */
public interface IQuestionVisitor<TObject> {

    public TObject Visit(ClosedButtonQuestion question);

    public TObject Visit(ClosedComboQuestion question);

    public TObject Visit(LikertQuestion question);

    public TObject Visit(SemDiffQuestion question);

    public TObject Visit(OpenQuestion question);

    public TObject Visit(OpenTextAreaQuestion question);

}
