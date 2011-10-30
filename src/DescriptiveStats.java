import processing.core.PApplet;

/**
 * Created by IntelliJ IDEA.
 * User: Wilfried
 * Date: 30-10-11
 * Time: 19:23
 * To change this template use File | Settings | File Templates.
 */
public class DescriptiveStats extends PApplet {
    float newArray[];

    DescriptiveStats(float newArray[]) {
        this.newArray = newArray;
    }

    public float average() {
        float tot = 0;
        for (int n = 0; n < newArray.length; n++) {
            tot += newArray[n];
        }
        return tot / newArray.length;
    }

    //Op basis van methode Moore & McCabe / Discissue blijft of je bij oneven aantal gemiddelde neemt, of de eerst waarde na het 50 percentiel zoals dat wel bij q1 en q3 gebeurd.
    public float median() {
        newArray = sort(newArray);
        float pos = (newArray.length + 1) / 2f;
        int posF = floor(pos) - 1; //Array begint bij 0, dus vandaar - 1
        int posC = ceil(pos) - 1; //Array begint bij 0, dus vandaar - 1
        return (newArray[posF] + newArray[posC]) / 2; // meest korte manier van schrijven, wanneer posF en posC gelijk zijn is dit irrelevant, wanneer array oneven aantal waarden kent moet er wel gemiddeld worden.
    }

    public float q1() {
        newArray = sort(newArray);
        int pos = (int) ceil((newArray.length) * .25f) - 1;
        return newArray[pos];
    }

    public float q3() {
        newArray = sort(newArray);
        int pos = (int) ceil((newArray.length) * .75f) - 1;
        return newArray[pos];
    }

    public float minValue() {
        newArray = sort(newArray);
        int posQ1 = (int) ceil((newArray.length) * .25f) - 1;
        int posQ3 = (int) ceil((newArray.length) * .75f) - 1;
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

    public float maxValue() {
        newArray = sort(newArray);
        int posQ1 = (int) ceil((newArray.length) * .25f) - 1;
        int posQ3 = (int) ceil((newArray.length) * .75f) - 1;
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
