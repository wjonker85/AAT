package Views.AAT.Results;

import Model.AATModel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by marcel on 5/24/14.
 */
public class ResultsView extends JPanel {

    private AATModel model;

    public ResultsView(AATModel model) {
        super(new GridBagLayout());
        this.setBackground(Color.black);
        this.setForeground(Color.white);
        Dimension screen = getToolkit().getScreenSize();
        setPreferredSize(new Dimension(screen.width, screen.height));
        this.model = model;
    }

    //Simple dispatch of panels containing different type of result graphs
    public void switchView(String type) {
        this.removeAll();
        Dimension screen = getToolkit().getScreenSize();
       if(type.equalsIgnoreCase("BoxPlot")) {
           BoxPlotResults bp = new BoxPlotResults(model);
           bp.setPreferredSize(new Dimension((int) (0.7 * screen.width), (int) (0.6 * screen.height)));
           this.add(bp);
       }
        else if(type.equalsIgnoreCase("Barchart")) {
           BarChartResults b = new BarChartResults(model);
           b.setPreferredSize(new Dimension((int) (0.7 * screen.width), (int) (0.6 * screen.height)));
           this.add(b);
       }

        else if(type.equalsIgnoreCase("Both")) {
           BarChartResults b = new BarChartResults(model);
           BoxPlotResults bp = new BoxPlotResults(model);
           b.setPreferredSize(new Dimension((int) (0.45 * screen.width), (int) (0.6 * screen.height)));
           bp.setPreferredSize(new Dimension((int) (0.45 * screen.width), (int) (0.6 * screen.height)));
           this.add(b);
           this.add(bp);

       }
        revalidate();
    }
}
