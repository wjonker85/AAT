package Views.AAT.Results;

import AAT.Util.Stats;
import DataStructures.AATImage;
import DataStructures.DesciptiveStatistics;
import Model.AATModel;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.BarPlot;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.Insets2D;
import de.erichseifert.gral.util.Location;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by marcel on 2/16/14.
 * Should be the panel that displays a plot a the end of the test.
 */
public class BarChart extends JPanel {

    private AATModel model;
    JPanel mainPanel;
    private final Color COLORA = Color.red;
    private final Color COLORN = Color.blue;


    public BarChart(AATModel model) {
        super(new BorderLayout());
        this.model = model;
        Dimension screen = getToolkit().getScreenSize();
        setPreferredSize(new Dimension(screen.width,screen.height));
        setBackground(Color.black);
        setForeground(Color.white);
        this.model = model;
           mainPanel = new JPanel(new BorderLayout());
           mainPanel.setBackground(Color.white);
         this.add(mainPanel, BorderLayout.CENTER);
    }

    private void addToData(DataTable data,HashMap<String,float[]> results,int pos)   {
        for(String key : results.keySet()) {
            DesciptiveStatistics stats = Stats.getDescriptiveStatistics(key,results.get(key));
            data.add(pos,stats.getMean(),stats.getlabel());
        }
    }


    private void createBarChart() {
        // Create new bar plot
        DataTable affective = new DataTable(Double.class,Float.class,String.class);
        addToData(affective,model.getResultsPerCondition(AATImage.AFFECTIVE,AATImage.PULL),1);
        addToData(affective,model.getResultsPerCondition(AATImage.AFFECTIVE,AATImage.PUSH),4);
        DataTable neutral = new DataTable(Double.class,Float.class,String.class);
        addToData(affective,model.getResultsPerCondition(AATImage.NEUTRAL,AATImage.PULL),2);
        addToData(affective,model.getResultsPerCondition(AATImage.NEUTRAL,AATImage.PUSH),5);

        BarPlot plot = new BarPlot(affective,neutral);

        // Format plot
        plot.setInsets(new Insets2D.Double(40.0, 40.0, 40.0, 40.0));
        plot.setBarWidth(0.075);

        // Format bars
        BarPlot.BarRenderer pointRendererA = (BarPlot.BarRenderer) plot.getPointRenderer(affective);
        pointRendererA.setColor(
                new LinearGradientPaint(0f,0f, 0f,1f,
                        new float[] { 0.0f, 1.0f },
                        new Color[] { COLORA, GraphicsUtils.deriveBrighter(COLORA) }
                )
        );
        pointRendererA.setBorderStroke(new BasicStroke(3f));
        pointRendererA.setBorderColor(
                new LinearGradientPaint(0f,0f, 0f,1f,
                        new float[] { 0.0f, 1.0f },
                        new Color[] { GraphicsUtils.deriveBrighter(COLORA), COLORA }
                )
        );
        pointRendererA.setValueVisible(true);
        pointRendererA.setValueColumn(2);
        pointRendererA.setValueLocation(Location.CENTER);
        pointRendererA.setValueColor(GraphicsUtils.deriveDarker(COLORA));
        pointRendererA.setValueFont(Font.decode(null).deriveFont(Font.BOLD));

        // Format bars
        BarPlot.BarRenderer pointRendererN = (BarPlot.BarRenderer) plot.getPointRenderer(affective);
        pointRendererN.setColor(
                new LinearGradientPaint(0f,0f, 0f,1f,
                        new float[] { 0.0f, 1.0f },
                        new Color[] { COLORN, GraphicsUtils.deriveBrighter(COLORN) }
                )
        );
        pointRendererN.setBorderStroke(new BasicStroke(3f));
        pointRendererN.setBorderColor(
                new LinearGradientPaint(0f,0f, 0f,1f,
                        new float[] { 0.0f, 1.0f },
                        new Color[] { GraphicsUtils.deriveBrighter(COLORN), COLORN }
                )
        );
        pointRendererN.setValueVisible(true);
        pointRendererN.setValueColumn(2);
        pointRendererN.setValueLocation(Location.CENTER);
        pointRendererN.setValueColor(GraphicsUtils.deriveDarker(COLORN));
        pointRendererN.setValueFont(Font.decode(null).deriveFont(Font.BOLD));

        // Add plot to Swing component
        mainPanel.add(new InteractivePanel(plot), BorderLayout.CENTER);


    }




    public void displayPlot(boolean show) {
        if (show) {
            createBarChart();
            repaint();
        } else {

            repaint();
        }
    }
}