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

package views;

import processing.core.PApplet;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Wilfried
 * Date: 30-10-11
 * Time: 19:23
 *
 */

/**
 * Deze class geeft de 5 getallen samenvatting terug voor een willekeurig array met float waarden
 */
public class DescriptiveStats extends PApplet {
    float newArray[];

    DescriptiveStats(float newArray[]) {
        this.newArray = newArray;
    }


    /**
     * Mediaan wordt berekent op basis van methode Moore & McCabe. Discissue blijft of je bij oneven aantal
     * gemiddelde neemt, of de eerst waarde na het 50e percentiel zoals dat wel bij q1 en q3 gebeurd. Moore McCabe
     * kiezen gemiddelde van de twee waarden vanneer het aantal waarneming bestaat uit een oneven aantal.
     *
     * @return mediaan (float)
     */
    public float median() {
        newArray = sort(newArray);
        float pos = (newArray.length + 1) / 2f;
        int posF = floor(pos) - 1;
        int posC = ceil(pos) - 1;
        return (newArray[posF] + newArray[posC]) / 2;
    }

    /**
     * Eerste kwartiel, is de eerste waarneming na het 25e percentiel.
     *
     * @return q1 (float)
     */
    public float q1() {
        newArray = sort(newArray);
        int pos = ceil((newArray.length) * .25f) - 1;
        return newArray[pos];
    }

    /**
     * Derder kwartiel, is de eerste waarneming na het 75e percentiel.
     *
     * @return q3 (float)
     */
    public float q3() {
        newArray = sort(newArray);
        int pos = ceil((newArray.length) * .75f) - 1;
        return newArray[pos];
    }

    /**
     * minValue is de eerste waarde welke binnen de q1 - 1.5 IKA regel valt.
     *
     * @return minValue (float)
     */
    public float minValue() {
        int n = 0;
        try {
            newArray = sort(newArray);
            int posQ1 = ceil((newArray.length) * .25f) - 1;
            int posQ3 = ceil((newArray.length) * .75f) - 1;

            float ika = newArray[posQ3] - newArray[posQ1];
            float minValue = newArray[posQ1] - (1.5f * ika);

            if (minValue <= newArray[0]) {
                return newArray[0];
            } else {
                while (newArray[n] < minValue) {
                    n++;
                }

            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Problem displaying boxplot: \n" +
                            "Not enough measurements in every category to calculate all the values the boxplot needs,\n" +
                            "Click on ok, then press the joystick button to finish the test.",
                    "Boxplot error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
        return newArray[n];
    }

    /**
     * maxValue is de laatste waarde welke binnen de q3 + 1.5 IKA regel valt.
     *
     * @return maxValue (float)
     */
    public float maxValue() {
        newArray = sort(newArray);
        int posQ1 = ceil((newArray.length) * .25f) - 1;
        int posQ3 = ceil((newArray.length) * .75f) - 1;
        float ika = newArray[posQ3] - newArray[posQ1];
        float maxValue = newArray[posQ3] + (1.5f * ika);

        if (maxValue >= newArray[newArray.length - 1]) {
            return newArray[newArray.length - 1];
        } else {
            int n = 0;
            while (newArray[n] < maxValue) {
                n++;
            }
            return newArray[n - 1];
        }
    }
}
