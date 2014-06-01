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
        this.model = model;
    }

    //Simple dispatch of panels containing different type of result graphs
    public void switchView(String type) {
        this.removeAll();
       if(type.equalsIgnoreCase("BoxPlot")) {
         //  BoxPlot bp = new BoxPlot(model);
         //  bp.init();
           BoxPlot2 bp = new BoxPlot2(model);
           this.add(bp);
           bp.displayPlot(true);
       }
        else if(type.equalsIgnoreCase("Barchart")) {
           BarChart b = new BarChart(model);
           this.add(b);
           b.displayPlot(true);
       }
        revalidate();
    }
}
