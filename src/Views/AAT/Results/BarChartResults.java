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

package Views.AAT.Results;

import DataStructures.AATImage;
import Model.AATModel;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.statistics.Statistics;
import de.erichseifert.gral.plots.BarPlot;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.axes.Axis;
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
            int mean = mean(results.get(key));
            data.add(pos, mean, key);
        }
    }

    private int mean(float[] values) {
        float sum = 0;
        for (int x = 0; x < values.length; x++) {
            sum += values[x];
        }
        return (Math.round(sum / values.length));
    }

    private void createBarChart() {
        // Create new bar plot
        DataTable affective = new DataTable(Double.class, Integer.class, String.class);
        addToData(affective, model.getResultsPerCondition(AATImage.AFFECTIVE, AATImage.PULL), 1.0);
        addToData(affective, model.getResultsPerCondition(AATImage.AFFECTIVE, AATImage.PUSH), 3.0);
        DataTable neutral = new DataTable(Double.class, Integer.class, String.class);
        addToData(neutral, model.getResultsPerCondition(AATImage.NEUTRAL, AATImage.PULL), 2.0);
        addToData(neutral, model.getResultsPerCondition(AATImage.NEUTRAL, AATImage.PUSH), 4.0);


        BarPlot plot = new BarPlot(affective, neutral);

        // Format plot
        plot.setInsets(new Insets2D.Double(20.0, 60.0, 40.0, 20.0));
        plot.setBarWidth(0.75);

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
        plot.getTitle().setText("Mean reaction times(ms) for the four conditions");
        plot.getAxisRenderer(BarPlot.AXIS_Y).setLabel("Reaction time(ms)");
        //     plot.getAxisRenderer(BarPlot.AXIS_X).setTicksVisible(false);
        // Format axes
        plot.getAxisRenderer(XYPlot.AXIS_X).setCustomTicks(
                DataUtils.map(
                        new Double[]{0.0, 1.0, 2.0, 3.0, 4.0},
                        new String[]{"", model.getLabelPerCondition(AATImage.AFFECTIVE, AATImage.PULL)
                                , model.getLabelPerCondition(AATImage.NEUTRAL, AATImage.PULL)
                                , model.getLabelPerCondition(AATImage.AFFECTIVE, AATImage.PUSH)
                                , model.getLabelPerCondition(AATImage.NEUTRAL, AATImage.PUSH)
                        }
                )
        );
        // Add plot to Swing component
        autoScaleYAxis(plot);
        this.add(new InteractivePanel(plot), BorderLayout.CENTER);
    }


    public void autoScaleYAxis(XYPlot plot) {

        Axis axis = plot.getAxis(XYPlot.AXIS_Y);
        if (axis == null || !axis.isAutoscaled()) {
            return;
        }
        java.util.List<DataSource> sources = plot.getData();
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (DataSource data : sources) {
            min = 0;
            max = Math.max(max, data.getColumn(1)
                    .getStatistics(Statistics.MAX));
        }
        double spacing = 0.05 * (max - min);
        axis.setRange(0, max + spacing);
    }
}