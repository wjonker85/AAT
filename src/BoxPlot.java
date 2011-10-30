import processing.core.PApplet;

/**
 * Created by IntelliJ IDEA.
 * User: Wilfried
 * Date: 30-10-11
 * Time: 19:35
 * To change this template use File | Settings | File Templates.
 */
public class BoxPlot extends PApplet {
    float rH, minV, maxV;
    float[] minValue, q1, median, q3, maxValue;
    String[] labelsArray;
    int viewWidth, viewHeight;
    DescriptiveStats stat1;
    DescriptiveStats stat2;
    DescriptiveStats stat3;
    DescriptiveStats stat4;

    BoxPlot(float[] array1, float[] array2, float[] array3, float[] array4,String[] labelsArray,int viewWidth,int viewHeight) {
        this.labelsArray = labelsArray;
        this.viewHeight = viewHeight;
        this.viewWidth = viewWidth;
        stat1 = new DescriptiveStats(array1);
        stat2 = new DescriptiveStats(array2);
        stat3 = new DescriptiveStats(array3);
        stat4 = new DescriptiveStats(array4);

        float minValue[] = {
                stat1.minValue(), stat2.minValue(), stat3.minValue(), stat4.minValue()
        };
        float q1[] = {
                stat1.q1(), stat2.q1(), stat3.q1(), stat4.q1()
        };
        float median[] = {
                stat1.median(), stat2.median(), stat3.median(), stat4.median()
        };
        float q3[] = {
                stat1.q3(), stat2.q3(), stat3.q3(), stat4.q3()
        };
        float maxValue[] = {
                stat1.maxValue(), stat2.maxValue(), stat3.maxValue(), stat4.maxValue()
        };

        //Grootste en kleinste waarde vinden die zich in de 4 array´s bevinden.
        float[] minMaxArray = {
                stat1.minValue(), stat2.minValue(), stat3.minValue(), stat4.minValue(),
                stat1.maxValue(), stat2.maxValue(), stat3.maxValue(), stat4.maxValue()
        };

        rH = max(minMaxArray) - min(minMaxArray);
        minV = min(minMaxArray);
        maxV = max(minMaxArray);
        this.minValue = minValue;
        this.q1 = q1;
        this.median = median;
        this.q3 = q3;
        this.maxValue = maxValue;
    }

        public void setup() {
        size(viewHeight, viewWidth);
        smooth();
    }

    public void draw() {
        background(0);
        drawBoxPlot(0, 0, 1.3f, "Reaction Time (ms)", "Condition", labelsArray[0], labelsArray[1], labelsArray[2], labelsArray[3]);
    }

    public void drawBoxPlot(int x, int y, float s, String yText, String xText, String c1Text, String c2Text, String c3Text, String c4Text) {
        //1 is 500 x 500 en kan dus op basis van scherm worden aangepast.
        scale(s);
        translate(x, y);
        line(75, 40, 75, 455); //Y As
        line(75, 455, 475, 455); // X As

        float resizeFac = 400f / rH;
        float minFactor = (resizeFac * minV);

        // Boxplotten tekenen
        int vPos = 100;
        stroke(255);
        fill(0);
        rectMode(CORNERS);
        for (int n = 0; n < 4; n++) {
            line(vPos + 25, 450 - (resizeFac * minValue[n]) + minFactor, vPos + 25, 450 - (resizeFac * maxValue[n]) + minFactor); //Verticale lijn
            rect(vPos, 450 - (resizeFac * q3[n]) + minFactor, vPos + 50, 450 - (resizeFac * q1[n]) + minFactor); //q1 & q3
            line(vPos, 450 - (resizeFac * minValue[n]) + minFactor, vPos + 50, 450 - (resizeFac * minValue[n]) + minFactor); // horizontale streep minValue
            line(vPos, 450 - (resizeFac * median[n]) + minFactor, vPos + 50, 450 - (resizeFac * median[n]) + minFactor); // horizontale streep mediaan
            line(vPos, 450 - (resizeFac * maxValue[n]) + minFactor, vPos + 50, 450 - (resizeFac * maxValue[n]) + minFactor); // horizontale streep maxValue
            vPos += 100;
        }


        //Fille voor teksten
        fill(255);

        //Y as ms waarden voorbereiden;
        textAlign(RIGHT);
        textSize(9);
        int textY = 450;
        float msStepSize = rH / 8;
        float msStartStep = minV;

        //Y as ms waarden weergeven;
        for (int n = 0; n <= 8; n++) {
            text((int) msStartStep+" -", 76, textY);
            textY = textY - 50;
            msStartStep += round(msStepSize);
        }

        // Labels
        textAlign(CENTER);
        textSize(9);
        text(c1Text, 125, 470);
        text(c2Text, 225, 470);
        text(c3Text, 325, 470);
        text(c4Text, 425, 470);

        textSize(15);
        text(xText, 250, 495); // Text X-as
        rotate(HALF_PI);
        text(yText, 250, -10); //Text Y-as
        text(c1Text, 125, 475);
    }
}
