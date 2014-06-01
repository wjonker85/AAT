package Views.AAT.Results;

import DataStructures.AATImage;
import Model.AATModel;
import de.erichseifert.gral.data.Column;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.statistics.Statistics;
import de.erichseifert.gral.plots.*;
import de.erichseifert.gral.plots.BoxPlot;
import de.erichseifert.gral.plots.XYPlot.XYNavigationDirection;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.Insets2D;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

/**
 * Created by marcel on 6/1/14.
 */
public class BoxPlot2 extends JPanel {


    private AATModel model;
    JPanel mainPanel;
    private final Color COLORA = Color.red;
    private final Color COLORB = Color.blue;


    public BoxPlot2(AATModel model) {
        this.model = model;
        Dimension screen = getToolkit().getScreenSize();
        setPreferredSize(new Dimension(screen.width, screen.height));
        setBackground(Color.black);
        setForeground(Color.white);
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.white);
        mainPanel.setPreferredSize(new Dimension((int) (0.7 * screen.width), (int) (0.6 * screen.height)));
        this.add(mainPanel);
    }

    private void addToData(DataTable data, HashMap<String, float[]> h1, HashMap<String, float[]> h2, HashMap<String, float[]> h3, HashMap<String, float[]> h4) {
        int size = model.getLargestSampleSize();
        for (int x = 0; x < size; x++) {
            float a = h1.get(h1.keySet().toArray()[0])[x];
            float b = h2.get(h2.keySet().toArray()[0])[x];
            float c = h3.get(h3.keySet().toArray()[0])[x];
            float d = h4.get(h4.keySet().toArray()[0])[x];
            data.add(a, b, c, d);
        }
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


    private void changeRenderer(Color color, DataSource source, XYPlot plot) {
        Stroke stroke = new BasicStroke(2f);
        BoxPlot.BoxWhiskerRenderer boxRenderer = new BoxPlot.BoxWhiskerRenderer();
        boxRenderer.setColor(color);
        boxRenderer.setWhiskerStroke(stroke);
        boxRenderer.setBoxBorderStroke(stroke);
        plot.setPointRenderer(source, boxRenderer);
         BoxPlot.BoxWhiskerRenderer pointRenderer =
             (BoxPlot.BoxWhiskerRenderer)
                   plot.getPointRenderer(source);
    //    pointRenderer.setBoxBackground(color);
        pointRenderer.setBoxBorderColor(color);
        pointRenderer.setWhiskerColor(color);
        pointRenderer.setCenterBarColor(color);

    }

    private void createBoxPlot() {
        DataSource affectivePull = toDataSource(model.getResultsPerCondition(AATImage.AFFECTIVE, AATImage.PULL), 1);
        DataSource affectivePush = toDataSource(model.getResultsPerCondition(AATImage.AFFECTIVE, AATImage.PUSH), 2);
        DataSource neutralPull = toDataSource(model.getResultsPerCondition(AATImage.NEUTRAL, AATImage.PULL), 3);
        DataSource neutralPush = toDataSource(model.getResultsPerCondition(AATImage.NEUTRAL, AATImage.PUSH), 4);

        XYPlot plot = new XYPlot(affectivePull, neutralPull, affectivePush, neutralPush);
        // Format plot
        plot.setInsets(new Insets2D.Double(20.0, 50.0, 40.0, 20.0));
        changeRenderer(Color.red, affectivePull, plot);
        changeRenderer(Color.blue, affectivePush, plot);
        changeRenderer(Color.red, neutralPull, plot);
        changeRenderer(Color.blue, neutralPush, plot);
        //Disable original renderers

//        plot.setPointRenderer(affectivePull, null);
        //       plot.setPointRenderer(affectivePush, null);
        //      plot.setPointRenderer(neutralPull, null);
        //    plot.setPointRenderer(neutralPush, null);
        // Format axes
        //    plot.getAxisRenderer(BoxPlot.AXIS_X).setCustomTicks(
        //            DataUtils.map(
        //                    new Double[]{1.0, 2.0,
        //                           3.0},
        //                  new String[]{affectivePull.keySet().toArray()[0].toString(),affectivePush.keySet().toArray()[0].toString(),neutralPull.keySet().toArray()[0].toString(),neutralPush.keySet().toArray()[0].toString()}
        //          )
        //  );

        //   Stroke stroke = new BasicStroke(2f);
        //   ScaledContinuousColorMapper colors =
        //         new
        //                 LinearGradient(GraphicsUtils.deriveBrighter(COLORA), Color.WHITE);
        // colors.setRange(1.0, 1.0);
        // B//oxWhiskerRenderer pointRenderer =
        //     (BoxWhiskerRenderer)
        //           plot.getPointRenderer(boxData);
        // pointRenderer.setWhiskerStroke(stroke);
        // pointRenderer.setBoxBorderStroke(stroke);


        plot.getNavigator().setDirection(XYNavigationDirection.VERTICAL);
        AxisRenderer rendererX = plot.getAxisRenderer(XYPlot.AXIS_X);
        rendererX.setIntersection(0);

        AxisRenderer rendererY = plot.getAxisRenderer(XYPlot.AXIS_Y);
      //  rendererX.se
        rendererY.setIntersection(0);

        // Add plot to Swing component
        InteractivePanel panel = new InteractivePanel(plot);

        mainPanel.add(panel);
    }


    public void displayPlot(boolean show) {
        if (show) {
            createBoxPlot();
            repaint();
        } else {

            repaint();
        }
    }
}



