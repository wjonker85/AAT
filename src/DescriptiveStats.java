import processing.core.PApplet;

/**
 * Created by IntelliJ IDEA.
 * User: Wilfried
 * Date: 30-10-11
 * Time: 19:23
 * To change this template use File | Settings | File Templates.
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
     * Weinig onduidelijk aan naam van deze functie, geeft gemiddelde van een array terug
     * @return gemiddelde (float)
     */
    public float average() {
        float tot = 0;
        for (int n = 0; n < newArray.length; n++) {
            tot += newArray[n];
        }
        return tot / newArray.length;
    }

    /**
     * Mediaan wordt berekent op basis van methode Moore & McCabe. Discissue blijft of je bij oneven aantal
     * gemiddelde neemt, of de eerst waarde na het 50e percentiel zoals dat wel bij q1 en q3 gebeurd. Moore McCabe
     * kiezen gemiddelde van de twee waarden vanneer het aantal waarneming bestaat uit een oneven aantal.
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
     * @return q1 (float)
     */
    public float q1() {
        newArray = sort(newArray);
        int pos = ceil((newArray.length) * .25f) - 1;
        return newArray[pos];
    }

    /**
     * Derder kwartiel, is de eerste waarneming na het 75e percentiel.
     * @return q3 (float)
     */
    public float q3() {
        newArray = sort(newArray);
        int pos = ceil((newArray.length) * .75f) - 1;
        return newArray[pos];
    }

    /**
     * minValue is de eerste waarde welke binnen de q1 - 1.5 IKA regel valt.
     * @return minValue (float)
     */
    public float minValue() {
        newArray = sort(newArray);
        int posQ1 = ceil((newArray.length) * .25f) - 1;
        int posQ3 = ceil((newArray.length) * .75f) - 1;
        float ika = newArray[posQ3] - newArray[posQ1];
        float minValue = newArray[posQ1] - (1.5f * ika);

        if (minValue <= newArray[0]) {
            return newArray[0];
        } else {
            int n = 0;
            while (newArray[n] < minValue) {
                n++;
            }
            return newArray[n];
        }
    }

    /**
     * maxValue is de laatste waarde welke binnen de q3 + 1.5 IKA regel valt.
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
