package views;

import Model.AATModel;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.ui.InteractivePanel;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Set;

/**
 * Created by marcel on 2/16/14.
 * Should be the panel that displays a plot a the end of the test.
 */
public class PlotPanel extends JPanel {

    private AATModel model;
    float[] array1, array2, array3, array4;
    String[] labelsArray;
    JPanel mainPanel;
    XYPlot plot;
    DataTable data;
    InteractivePanel iPanel;

    public PlotPanel(AATModel model) {
        this.model = model;
        mainPanel = new JPanel();
        //   mainPanel.setBackground(Color.black);
        //   mainPanel.setForeground(Color.white);
        mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER));    //TODO even naar kijken
        mainPanel.setPreferredSize(new Dimension(1000, 800));
        Dimension screen = getToolkit().getScreenSize();
        //  this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setLayout(new GridBagLayout());
        this.setBackground(Color.black);
        this.setForeground(Color.white);
        this.setPreferredSize(new Dimension(screen.width / 2, screen.height));
        this.setMaximumSize(new Dimension(screen.width / 2, screen.height));

        data = new DataTable(Integer.class, Double.class);
        plot = new XYPlot(data);
        LineRenderer lines = new DefaultLineRenderer2D();

        plot.setLineRenderer(data, lines);
        Color color = new Color(0.0f, 0.3f, 1.0f);
        plot.getPointRenderer(data).setColor(color);
        plot.getLineRenderer(data).setColor(color);
        iPanel = new InteractivePanel(plot);
        mainPanel.add(iPanel);
        this.add(mainPanel);
    }

    private void createLinePlot() {
        Set labels = model.getResultsPerCondition().keySet();        //Get results from the model
        labelsArray = Arrays.copyOf(labels.toArray(), labels.toArray().length, String[].class);    //Convert labels to array

        array1 = model.getResultsPerCondition().get(labelsArray[0]);    //Fill the 4 float arrays;

        for (int x = 0; x < array1.length; x++) {
            data.add(x, (double) array1[x]);
            System.out.println("x " + x + " y " + array1[x] + " " + data.getRowCount());
        }
        plot.add(data);
        mainPanel.revalidate();

        iPanel.setMinimumSize(new Dimension(800, 600));
        iPanel.setPreferredSize(new Dimension(800, 600));
        System.out.println(iPanel.getWidth() + " test");
        iPanel.revalidate();
        iPanel.repaint();
        mainPanel.repaint();
        repaint();
        //   return plotPanel;
    }

    public void displayPlot(boolean show) {
        if (show) {
            createLinePlot();
            //     mainPanel.removeAll();
            //    mainPanel.repaint();
            //   mainPanel.add(createLinePlot());
            //   mainPanel.revalidate();
            //   mainPanel.getRootPane().revalidate();
            //   this.getRootPane().revalidate();
            repaint();
        } else {
            mainPanel.removeAll();
            repaint();
        }
    }
}
