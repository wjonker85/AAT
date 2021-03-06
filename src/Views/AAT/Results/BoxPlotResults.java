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
import de.erichseifert.gral.data.Column;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.statistics.Statistics;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.XYPlot.XYNavigationDirection;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.Insets2D;
import de.erichseifert.vectorgraphics2d.util.DataUtils;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;

/**
 * Created by marcel on 6/1/14.
 */
public class BoxPlotResults extends JPanel {


    private AATModel model;

    public BoxPlotResults(AATModel model) {
        super(new BorderLayout());
        this.model = model;
        setForeground(Color.white);
        setBackground(Color.white);
        createBoxPlot();

    }

    private DataSource toDataSource(HashMap<String, float[]> h1, int pos) {
        DataTable data = new DataTable(Float.class);
        int size = h1.get(h1.keySet().toArray()[0]).length;
        for (int x = 0; x < size; x++) {
            float a = h1.get(h1.keySet().toArray()[0])[x];
            data.add(a);
        }
        DataTable stats = new DataTable(Integer.class, Double.class, Double.class, Double.class, Double.class, Double.class);
        Column col = data.getColumn(0);
        stats.add(
                pos,
                col.getStatistics(Statistics.MEDIAN),
                col.getStatistics(Statistics.MIN),
                col.getStatistics(Statistics.QUARTILE_1),
                col.getStatistics(Statistics.QUARTILE_3),
                col.getStatistics(Statistics.MAX)
        );
        return stats;
    }

    //Change the default point renderer to a boxwhiskerRenderer. Draw everything in the given color.
    private void changeRenderer(Color color, DataSource source, XYPlot plot) {
        Stroke stroke = new BasicStroke(2f);
        de.erichseifert.gral.plots.BoxPlot.BoxWhiskerRenderer boxRenderer = new de.erichseifert.gral.plots.BoxPlot.BoxWhiskerRenderer();
        boxRenderer.setColor(color);
        boxRenderer.setWhiskerStroke(stroke);
        boxRenderer.setBoxBorderStroke(stroke);
        plot.setPointRenderer(source, boxRenderer);
        de.erichseifert.gral.plots.BoxPlot.BoxWhiskerRenderer pointRenderer =
                (de.erichseifert.gral.plots.BoxPlot.BoxWhiskerRenderer)
                        plot.getPointRenderer(source);
        pointRenderer.setBoxBorderColor(color);
        pointRenderer.setWhiskerColor(color);
        pointRenderer.setCenterBarColor(color);
        plot.getAxisRenderer(XYPlot.AXIS_X).setTickSpacing(1.0);
        plot.getAxisRenderer(XYPlot.AXIS_X).setMinorTicksVisible(false);
        plot.getAxisRenderer(XYPlot.AXIS_X).setIntersection(-Double.MAX_VALUE);
        plot.getAxisRenderer(XYPlot.AXIS_Y).setIntersection(-Double.MAX_VALUE);
    }


    public void autoScaleAxis(String axisName, XYPlot plot) {

        Axis axis = plot.getAxis(axisName);
        if (axis == null || !axis.isAutoscaled()) {
            return;
        }

        List<DataSource> sources = plot.getData();
        boolean isXAxis = plot.AXIS_X.equals(axisName);

        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (DataSource data : sources) {
            de.erichseifert.gral.plots.BoxPlot.BoxWhiskerRenderer pointRenderer =
                    (de.erichseifert.gral.plots.BoxPlot.BoxWhiskerRenderer) plot.getPointRenderer(data);

            int minColumnIndex, maxColumnIndex;
            if (isXAxis) {
                minColumnIndex = pointRenderer.getPositionColumn();
                maxColumnIndex = pointRenderer.getPositionColumn();
            } else {
                minColumnIndex = pointRenderer.getBottomBarColumn();
                maxColumnIndex = pointRenderer.getTopBarColumn();
            }

            min = Math.min(min, data.getColumn(minColumnIndex)
                    .getStatistics(Statistics.MIN));
            max = Math.max(max, data.getColumn(maxColumnIndex)
                    .getStatistics(Statistics.MAX));
        }
        double spacing = (isXAxis) ? 0.5 : 0.05 * (max - min);
        axis.setRange(min - spacing, max + spacing);
    }


    private void createBoxPlot() {
        DataSource affectivePull = toDataSource(model.getResultsPerCondition(AATImage.AFFECTIVE, AATImage.PULL), 1);
        DataSource neutralPull = toDataSource(model.getResultsPerCondition(AATImage.NEUTRAL, AATImage.PULL), 3);
        DataSource affectivePush = toDataSource(model.getResultsPerCondition(AATImage.AFFECTIVE, AATImage.PUSH), 2);
        DataSource neutralPush = toDataSource(model.getResultsPerCondition(AATImage.NEUTRAL, AATImage.PUSH), 4);

        XYPlot plot = new XYPlot(affectivePull, neutralPull, affectivePush, neutralPush);
        // Format plot
        plot.setInsets(new Insets2D.Double(20.0, 60.0, 40.0, 20.0));
        changeRenderer(Color.red, affectivePull, plot);
        changeRenderer(Color.blue, affectivePush, plot);
        changeRenderer(Color.red, neutralPull, plot);
        changeRenderer(Color.blue, neutralPush, plot);
        ((XYPlot.XYPlotArea2D) plot.getPlotArea()).setMajorGridX(false);
        autoScaleAxis(XYPlot.AXIS_X, plot);
        autoScaleAxis(XYPlot.AXIS_Y, plot);
        plot.getNavigator().setDirection(XYNavigationDirection.VERTICAL);
        plot.getTitle().setText("Boxplot results(ms) for the four conditions");
        // Format axes
        plot.getAxisRenderer(de.erichseifert.gral.plots.BoxPlot.AXIS_X).setCustomTicks(
                DataUtils.map(
                        new Double[]{1.0, 2.0, 3.0, 4.0},
                        new String[]{model.getLabelPerCondition(AATImage.AFFECTIVE, AATImage.PULL)
                                , model.getLabelPerCondition(AATImage.NEUTRAL, AATImage.PULL)
                                , model.getLabelPerCondition(AATImage.AFFECTIVE, AATImage.PUSH)
                                , model.getLabelPerCondition(AATImage.NEUTRAL, AATImage.PUSH)
                        }
                )
        );
        plot.getAxisRenderer(XYPlot.AXIS_Y).setLabel("Reaction Time(ms)");
        // Add plot to Swing component
        InteractivePanel panel = new InteractivePanel(plot);

        this.add(panel, BorderLayout.CENTER);
    }


}



