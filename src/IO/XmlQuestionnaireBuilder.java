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


package IO;

import DataStructures.Questionnaire.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * Created by marcel on 3/25/14.
 * This class is an implementation of the questionVisitor. This builds an xml document from the questionnaire that was assembled using
 * the configuration builder
 */
public class XmlQuestionnaireBuilder implements IVoidQuestionVisitor {

    private Document doc;
    private Element rootElement;

    public XmlQuestionnaireBuilder(Document doc, Element rootElement) {
        this.doc = doc;
        this.rootElement = rootElement;
    }

    @Override
    public void Visit(ClosedButtonQuestion question) {
        Element questionNode = this.createBaseElement(question);
        questionNode.setAttribute("type", "closed_button");
        System.out.println("closed_button");
        for (String option : question.getOptions()) {
            Element optionNode = doc.createElement("option");
            Text optionNodeTxt = doc.createTextNode(option);
            optionNode.appendChild(optionNodeTxt);
            questionNode.appendChild(optionNode);
        }
        addkeyToElement(question, questionNode);
    }

    @Override
    public void Visit(ClosedComboQuestion question) {
        Element questionNode = this.createBaseElement(question);
        questionNode.setAttribute("type", "closed_combo");
        for (String option : question.getOptions()) {
            Element optionNode = doc.createElement("option");
            Text optionNodeTxt = doc.createTextNode(option);
            optionNode.appendChild(optionNodeTxt);
            questionNode.appendChild(optionNode);
        }
        addkeyToElement(question, questionNode);
    }

    @Override
    public void Visit(LikertQuestion question) {
        Element questionNode = this.createBaseElement(question);
        questionNode.setAttribute("type", "likert");
        System.out.println("likert");
        Element sizeNode = doc.createElement("size");
        Text sizeNodeTxt = doc.createTextNode(String.valueOf(question.getSize()));
        sizeNode.appendChild(sizeNodeTxt);
        questionNode.appendChild(sizeNode);

        Element leftNode = doc.createElement("left");
        Text leftNodeTxt = doc.createTextNode(question.getLeft());
        leftNode.appendChild(leftNodeTxt);
        questionNode.appendChild(leftNode);

        Element rightNode = doc.createElement("right");
        Text rightNodeTxt = doc.createTextNode(question.getRight());
        rightNode.appendChild(rightNodeTxt);
        questionNode.appendChild(rightNode);
        addkeyToElement(question, questionNode);
    }

    @Override
    public void Visit(SemDiffQuestion question) {
        Element questionNode = this.createBaseElement(question);
        questionNode.setAttribute("type", "sem_diff");
        System.out.println("sem_diff");
        Element sizeNode = doc.createElement("size");
        Text sizeNodeTxt = doc.createTextNode(String.valueOf(question.getSize()));
        sizeNode.appendChild(sizeNodeTxt);
        questionNode.appendChild(sizeNode);

        Element leftNode = doc.createElement("left");
        Text leftNodeTxt = doc.createTextNode(question.getLeft());
        leftNode.appendChild(leftNodeTxt);
        questionNode.appendChild(leftNode);

        Element rightNode = doc.createElement("right");
        Text rightNodeTxt = doc.createTextNode(question.getRight());
        rightNode.appendChild(rightNodeTxt);
        questionNode.appendChild(rightNode);
        addkeyToElement(question, questionNode);
    }

    @Override
    public void Visit(OpenQuestion question) {
        Element questionNode = this.createBaseElement(question);
        questionNode.setAttribute("type", "open");
        addkeyToElement(question, questionNode);
    }

    @Override
    public void Visit(OpenTextAreaQuestion question) {
        Element questionNode = this.createBaseElement(question);
        questionNode.setAttribute("type", "textArea");
        addkeyToElement(question, questionNode);

    }

    private Element createBaseElement(AbstractQuestion question) {
        Element questionNode = doc.createElement("question");
        questionNode.setAttribute(("required"), String.valueOf(question.isRequired()));
        rootElement.appendChild(questionNode);
        Element textNode = doc.createElement("text");
        Text textNodeStr = doc.createTextNode(question.getQuestion());
        textNode.appendChild(textNodeStr);
        questionNode.appendChild(textNode);
        return questionNode;
    }

    private void addkeyToElement(AbstractQuestion question, Element questionNode) {
        Element keyNode = doc.createElement("key");
        Text keyNodeStr = doc.createTextNode(question.getKey());
        keyNode.appendChild(keyNodeStr);
        questionNode.appendChild(keyNode);
    }
}
