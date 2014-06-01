package Views.AAT.Results;

import AAT.Util.Stats;
import DataStructures.AATImage;
import DataStructures.DesciptiveStatistics;
import Model.AATModel;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.BarPlot;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.Insets2D;
import de.erichseifert.gral.util.Location;
import de.erichseifert.vectorgraphics2d.util.DataUtils;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

/**
 * Created by marcel on 2/16/14.
 * Should be the panel that displays a plot a the end of the test.
 */
public class BarChartResults extends JPanel {

    private AATModel model;
    private final Color COLORA = Color.red;
    private final Color COLORN = Color.blue;


    public BarChartResults(AATModel model) {
        super(new BorderLayout());
        setForeground(Color.black);
        this.model = model;
        setBackground(Color.white);
        createBarChart();
    }

    private void addToData(DataTable data, HashMap<String, float[]> results, double pos) {
        for (String key : results.keySet()) {
            DesciptiveStatistics stats = Stats.getDescriptiveStatistics(key, results.get(key));
            data.add(pos, stats.getMean(), key);
        }
    }


    private void createBarChart() {
        // Create new bar plot
        DataTable affective = new DataTable(Double.class, Float.class, String.class);
        addToData(affective, model.getResultsPerCondition(AATImage.AFFECTIVE, AATImage.PULL), 0.1);
        addToData(affective, model.getResultsPerCondition(AATImage.AFFECTIVE, AATImage.PUSH), 0.4);
        DataTable neutral = new DataTable(Double.class, Float.class, String.class);
        addToData(neutral, model.getResultsPerCondition(AATImage.NEUTRAL, AATImage.PULL), 0.2);
        addToData(neutral, model.getResultsPerCondition(AATImage.NEUTRAL, AATImage.PUSH), 0.5);

        BarPlot plot = new BarPlot(affective, neutral);

        // Format plot
        plot.setInsets(new Insets2D.Double(40.0, 40.0, 40.0, 40.0));
        plot.setBarWidth(0.075);

        // Format bars
        BarPlot.BarRenderer pointRendererA = (BarPlot.BarRenderer) plot.getPointRenderer(affective);
        pointRendererA.setColor(
                new LinearGradientPaint(0f, 0f, 0f, 1f,
                        new float[]{0.0f, 1.0f},
                        new Color[]{COLORA, GraphicsUtils.deriveBrighter(COLORA)}
                )
        );
        pointRendererA.setBorderStroke(new BasicStroke(3f));
        pointRendererA.setBorderColor(
                new LinearGradientPaint(0f, 0f, 0f, 1f,
                        new float[]{0.0f, 1.0f},
                        new Color[]{GraphicsUtils.deriveBrighter(COLORA), COLORA}
                )
        );
        pointRendererA.setValueVisible(true);
        pointRendererA.setValueColumn(1);
        pointRendererA.setValueLocation(Location.CENTER);
        pointRendererA.setValueColor(Color.white);
        pointRendererA.setValueFont(Font.decode(null).deriveFont(Font.BOLD));

        // Format bars
        BarPlot.BarRenderer pointRendererN = (BarPlot.BarRenderer) plot.getPointRenderer(neutral);
        pointRendererN.setColor(
                new LinearGradientPaint(0f, 0f, 0f, 1f,
                        new float[]{0.0f, 1.0f},
                        new Color[]{COLORN, GraphicsUtils.deriveBrighter(COLORN)}
                )
        );
        pointRendererN.setBorderStroke(new BasicStroke(3f));
        pointRendererN.setBorderColor(
                new LinearGradientPaint(0f, 0f, 0f, 1f,
                        new float[]{0.0f, 1.0f},
                        new Color[]{GraphicsUtils.deriveBrighter(COLORN), COLORN}
                )
        );
        pointRendererN.setValueVisible(true);
        pointRendererN.setValueColumn(1);
        pointRendererN.setValueLocation(Location.CENTER);
        pointRendererN.setValueColor(Color.white);
        pointRendererN.setValueFont(Font.decode(null).deriveFont(Font.BOLD));
        plot.getTitle().setText("Average reaction times(ms) for the four conditions");
        plot.getAxisRenderer(BarPlot.AXIS_X).setLabel("Condition");
        plot.getAxisRenderer(BarPlot.AXIS_Y).setLabel("Mean RTime(ms)");
   //     plot.getAxisRenderer(BarPlot.AXIS_X).setTicksVisible(false);
        // Format axes
        plot.getAxisRenderer(de.erichseifert.gral.plots.BoxPlot.AXIS_X).setCustomTicks(
                DataUtils.map(
                        new Double[]{0.0,0.1,0.2,0.3,0.4,0.5},
                        new String[]{"",model.getLabelPerCondition(AATImage.AFFECTIVE, AATImage.PULL)
                                , model.getLabelPerCondition(AATImage.NEUTRAL, AATImage.PULL)
                                ," "
                                , model.getLabelPerCondition(AATImage.AFFECTIVE, AATImage.PUSH)
                                , model.getLabelPerCondition(AATImage.NEUTRAL, AATImage.PUSH)
                        }
                )
        );
        // Add plot to Swing component
        this.add(new InteractivePanel(plot), BorderLayout.CENTER);
    }
}