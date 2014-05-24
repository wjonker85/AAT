package DataStructures;

/**
 * Created by marcel on 5/24/14.
 * Data structure containing descriptive statistics for a
 */
public class DesciptiveStatistics {

    private String label;

    public DesciptiveStatistics(String label)
    {
       this.label= label;
    }

    public String getlabel() {
        return label;
    }

    public float getMean() {
        return mean;
    }

    public void setMean(float mean) {
        this.mean = mean;
    }

    public float getMedian() {
        return median;
    }

    public void setMedian(float median) {
        this.median = median;
    }

    public float getQ1() {
        return q1;
    }

    public void setQ1(float q1) {
        this.q1 = q1;
    }

    public float getQ3() {
        return q3;
    }

    public void setQ3(float q3) {
        this.q3 = q3;
    }

    public float getMin() {
        return min;
    }

    public void setMin(float min) {
        this.min = min;
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
    }

    public float mean,median,q1,q3,min,max;

}
