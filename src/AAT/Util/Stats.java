package AAT.Util;

import DataStructures.DesciptiveStatistics;
import java.util.Arrays;


/**
 * Created by marcel on 5/24/14.
 */
public class Stats {



    public static DesciptiveStatistics getDescriptiveStatistics(String label,float[] values) {
        DesciptiveStatistics stats = new DesciptiveStatistics(label);
        Arrays.sort(values);        //First sort the results
        stats.setMean(mean(values));
        stats.setMax(maxValue(values));
        stats.setMin(minValue(values));
        stats.setQ1(q1(values));
        stats.setMedian(median(values));
        stats.setQ3(q3(values));
        return stats;
    }


    /**
     * Mediaan wordt berekent op basis van methode Moore & McCabe. Discissue blijft of je bij oneven aantal
     * gemiddelde neemt, of de eerst waarde na het 50e percentiel zoals dat wel bij q1 en q3 gebeurd. Moore McCabe
     * kiezen gemiddelde van de twee waarden vanneer het aantal waarneming bestaat uit een oneven aantal.
     *
     * @return mediaan (float)
     */
    private static float median(float[] values) {

        float pos = (values.length + 1) / 2f;
        int posF = (int) Math.floor(pos) - 1;
        int posC = (int) Math.ceil(pos) - 1;
        return (values[posF] + values[posC]) / 2;
    }

    private static float mean(float[] values) {
        float sum = 0;
        for(int x = 0;x<values.length;x++) {
            sum+=values[x];
        }
        return sum/values.length;
    }

    /**
     * Eerste kwartiel, is de eerste waarneming na het 25e percentiel.
     *
     * @return q1 (float)
     */
    private static float q1(float[] values) {
        int pos = (int) Math.ceil((values.length) * .25f) - 1;
        return values[pos];
    }

    /**
     * Derder kwartiel, is de eerste waarneming na het 75e percentiel.
     *
     * @return q3 (float)
     */
    private static float q3(float[] values) {
        int pos = (int) Math.ceil((values.length) * .75f) - 1;
        return values[pos];
    }

    /**
     * minValue is de eerste waarde welke binnen de q1 - 1.5 IKA regel valt.
     *
     * @return minValue (float)
     */
    private static float minValue(float[] values) {
        int n = 0;
        try {
            int posQ1 = (int) Math.ceil((values.length) * .25f) - 1;
            int posQ3 = (int) Math.ceil((values.length) * .75f) - 1;

            float ika = values[posQ3] - values[posQ1];
            float minValue = values[posQ1] - (1.5f * ika);

            if (minValue <= values[0]) {
                return values[0];
            } else {
                while (values[n] < minValue) {
                    n++;
                }

            }
        } catch (Exception e) {
            return values[0];
        }
        return values[n];
    }

    /**
     * maxValue is de laatste waarde welke binnen de q3 + 1.5 IKA regel valt.
     *
     * @return maxValue (float)
     */
    private static float maxValue(float[] values) {
        int posQ1 = (int) Math.ceil((values.length) * .25f) - 1;
        int posQ3 = (int) Math.ceil((values.length) * .75f) - 1;
        float ika = values[posQ3] - values[posQ1];
        float maxValue = values[posQ3] + (1.5f * ika);

        if (maxValue >= values[values.length - 1]) {
            return values[values.length - 1];
        } else {
            int n = 0;
            while (values[n] < maxValue) {
                n++;
            }
            return values[n - 1];
        }
    }
}
