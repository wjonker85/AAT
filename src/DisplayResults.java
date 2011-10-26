import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/15/11
 * Time: 11:03 AM
 * Frame die de uitvoer in tabelvorm laat zien.
 */


public class DisplayResults extends JFrame implements Observer {

    private JPanel mainPanel;
    private JTable resultsTable;

    public DisplayResults() {
        mainPanel = new JPanel();
        resultsTable = new JTable(null);
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        mainPanel.add(scrollPane);
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setVisible(false);
    }



    public void update(Observable observable, Object o) {
        AATModel model = (AATModel) observable;
        if (o.toString().equals("Test ended")) {
            System.out.println("Moet nu resultaten laten zien");
               resultsTable.setModel(model.getResults());
            resultsTable.repaint();
               setVisible(true);
        }
   //     else {
     //       setVisible(false);
     //   }
    }
}
