package Views.AAT.Results;

import AAT.Util.Stats;
import DataStructures.DesciptiveStatistics;
import Model.AATModel;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.BarPlot;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.ui.InteractivePanel;

import javax.swing.*;
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
   // JPanel mainPanel;
  //  XYPlot plot;
    DataTable data;
    InteractivePanel iPanel;

    public BarChart(AATModel model) {
        super(new BorderLayout());
        Dimension screen = getToolkit().getScreenSize();
        setPreferredSize(new Dimension(screen.width,screen.height));
        setBackground(Color.WHITE);
        this.model = model;
     //   mainPanel = new JPanel();
     //   mainPanel.setBackground(Color.black);
     //   mainPanel.setForeground(Color.white);
//        mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));    //TODO even naar kijken
    //    setSize(800,800);
//        mainPanel.setPreferredSize(new Dimension(1000, 800));
  //      mainPanel.setSize(800,800);
    //    Dimension screen = getToolkit().getScreenSize();
    //    this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
   //     this.setLayout(new GridBagLayout());
   //     this.setBackground(Color.black);
  //      this.setForeground(Color.white);
  //      this.setPreferredSize(new Dimension(screen.width / 2, screen.height));
  //      this.setMaximumSize(new Dimension(screen.width / 2, screen.height));
       // this.add(mainPanel);
    }

    private void createBarChart() {
        this.removeAll();
        data = new DataTable(String.class, Float.class);
        HashMap<String,float[]> results = model.getResultsPerCondition();
        for(String key : results.keySet()) {
            DesciptiveStatistics stats = Stats.getDescriptiveStatistics(key,results.get(key));
            data.add(stats.getlabel(),stats.getMean());
        }
        BarPlot plot = new BarPlot(data);
        iPanel = new InteractivePanel(plot);
        PointRenderer barRenderer = plot.getPointRenderer(data);
        barRenderer.setColor(Color.RED);
        this.add(iPanel,BorderLayout.CENTER);


  //      iPanel.setPreferredSize(new Dimension(500,500));
   //     iPanel.setMinimumSize(new Dimension(500,500));



  //      revalidate();
    }

    protected JFrame showInFrame() {
                     JFrame frame = new JFrame("test");
                frame.getContentPane().add(this, BorderLayout.CENTER);
                     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                   frame.setSize(getPreferredSize());
                      frame.setVisible(true);
                    return frame;
        }


    public void displayPlot(boolean show) {
        if (show) {
            createBarChart();
            repaint();
        } else {
        //    mainPanel.removeAll();
            repaint();
        }
    }
}
