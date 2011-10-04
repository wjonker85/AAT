import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/4/11
 * Time: 3:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class AAT_Main  {



    private AATModel model;
    private JFrame frame;
    private JPanel mainPanel;

   /*
   Definieer view classes, deze classes worden door Wilfried in Processing geschreven.
         */
    private AATView aatView;
    private AATResults aatResults;


    public static void main(String[] args) {
         AAT_Main main = new AAT_Main();
    }

    private void show() {

        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800,600);
        frame.pack();
        frame.setVisible(true);
    }

    public AAT_Main() {
       frame = new JFrame("Approach Avoidance Task");
        model = new AATModel();
        aatView = new AATView();
        aatResults = new AATResults();

        aatView.init();
        aatResults.init();
        mainPanel = new JPanel();
        mainPanel.add(aatView);
        mainPanel.add(aatResults);

        this.show();
    //    model.add
    }
}
