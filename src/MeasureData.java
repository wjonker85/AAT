import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/15/11
 * Time: 1:29 PM
 * MeasureData bevat all gemeten data. Dus per plaatje alles wat van belang is. Voor elke persoon die de test zal doen
 * zal er een nieuwe MeasureData aangemaakt worden.
 * Lijkt nu goed te werken. Alle data wordt goed opgeslagen.
 * TODO: de data uit deze class nog wegschrijven naar een bestand.
 */
public class MeasureData {

    private ArrayList<MeasureObject> allMeasures;
    private int id;

    //Constructor maakt een nieuwe lijst met MeasureObjecten. id = het id van de persoon die de test heeft gedaan.
    public MeasureData(int id) {
        this.id = id;
        allMeasures = new ArrayList<MeasureObject>();
    }

    //New measure for each picture
    public void newMeasure(int run, String imageName, int direction, int type) {
        System.out.println("Start nieuwe meting "+ imageName);
        MeasureObject measureObject = new MeasureObject(run, imageName, direction, type);
        allMeasures.add(measureObject);

    }

    //Elke beweging van de joystick wordt bijgehouden inclusief de bijbehorende reactietijd
    public void addResult(int size, long time) {
        System.out.println("Voeg resultaat toe: "+ size+" "+time);
        allMeasures.get(allMeasures.size()-1).addResult(size, time);
    }

    public int getId() {
      //  return id;
        return 12673;
    }


    //Geeft een tableModel terug met alle data.
    public AbstractTableModel getAllResults() {
        ResultsTableModel allResults = new ResultsTableModel();
        System.out.println("Aantal metingen "+allMeasures.size());
        for (MeasureObject mObject : allMeasures) {
            System.out.println(mObject.getImageName()+" "+mObject.size());
            for (int x = 0; x < mObject.size(); x++) {
            ArrayList<Object> imageResults = new ArrayList<Object>();
            imageResults.add(getId());
                imageResults.add(mObject.getRun());
                imageResults.add(mObject.getImageName());
                imageResults.add(mObject.getDirection());
                imageResults.add(mObject.getType());
                imageResults.add(mObject.getPosition(x));
                imageResults.add(mObject.getTime(x));
                allResults.add(imageResults);
            }
        }
        return allResults;
    }


    //Geeft een TableModel met alleen de simpele resultaten, dus per plaatje de reactietijd.
    public AbstractTableModel getSimpleResults() {
        SimpleResultsTableModel simpleResults = new SimpleResultsTableModel();
        for (MeasureObject mObject : allMeasures) {
            ArrayList<Object> imageResults = new ArrayList<Object>();
            imageResults.add(mObject.getRun());
            imageResults.add(mObject.getImageName());
            imageResults.add(mObject.getDirection());
            imageResults.add(mObject.getType());
            imageResults.add(mObject.getReactionTime());
            simpleResults.add(imageResults);
        }
        return simpleResults;
    }
}


//Simpele data structuur die alle gemeten informatie bevat. Aan de hand van deze structuur kunnen andere datastructuren zoals
//tableModels of misschien grafieken gemaakt worden. Natuurlijk ook de fileOutput;
class MeasureObject {

    private ArrayList<Integer> positionList;
    private ArrayList<Long> timeList;

    private int run;
    private String imageName;
    private int direction;
    private int type;

    public MeasureObject(int run, String imageName, int direction, int type) {
        positionList = new ArrayList<Integer>();
        timeList = new ArrayList<Long>();
        this.run = run;
        this.imageName = imageName;
        this.direction = direction;
        this.type = type;

    }

    public void addResult(int position, long time) {
        positionList.add(position);
        timeList.add(time);
    }


    //Returns the current direction (Push or Pull)
    public int getDirection() {
        return direction;
    }


    //Returns if it's an affective or neutral image
    public int getType() {
        return type;
    }

    //Returns the total reactionTime
    public long getReactionTime() {
        return timeList.get(size()-1);  //laatste object returnen
    }

    //Current run.
    public int getRun() {
        return run;
    }

    public String getImageName() {
        return imageName;
    }

    public int size() {
        return positionList.size();
    }

    public int getPosition(int i) {
        return positionList.get(i);
    }

    public long getTime(int i) {
        return timeList.get(i);
    }
}