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

package IO;

import AAT.AATImage;
import DataStructures.DynamicTableModel;
import DataStructures.ResultsDataTableModel;
import Model.AATModel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/29/11
 * Time: 4:24 PM
 * This class is used to export all the registered data in a form that can be used to do the data analysis.
 */
public class DataExporter {

    private DynamicTableModel exportData;
    private ResultsDataTableModel resultsData;
    private HashMap<String, Integer> NoMistakes;
    private AATModel model;
    private int centerPos;
    private int minRtime, maxRtime, errorPerc;
    private boolean transposed;

    //Constructor, based on information from the views.ExportDataDialog
    public DataExporter(AATModel model, int minRTime, int maxRtime, int errorPerc, boolean transposed) {
        this.model = model;
        this.minRtime = minRTime;
        this.maxRtime = maxRtime;
        this.errorPerc = errorPerc;
        this.transposed = transposed;
        centerPos = (model.getTest().getDataSteps() + 1) / 2;
        CSVReader partReader = new CSVReader(model.getTest().getParticipantsFile());       //Read all stored participants data
        CSVReader dataReader = new CSVReader(model.getTest().getDataFile());              //read all stored measurement data.
        DynamicTableModel partipantsTable = new DynamicTableModel();
        partipantsTable.setColumnNames(partReader.getColumnNames());
        partipantsTable.add(partReader.getData());
        resultsData = new ResultsDataTableModel();
        resultsData.add(dataReader.getData());
        exportData = new DynamicTableModel();
        ArrayList<Object> columnNames = new ArrayList<Object>();
        columnNames.add("ID");
        columnNames.add("Triall");
        columnNames.add("Image");
        columnNames.add("Direction");
        columnNames.add("Type");
        columnNames.add("ReactionTime");
        exportData.setColumnNames(columnNames);
        NoMistakes = new HashMap<String, Integer>();
    }


    /*
    This method creates a new table with reactionTimes. It filters out images that have reactionTimes that are not
    withing the user defined boundaries. It also removes the instances where the joystick started in the wrong position, not in
    the center.
     */
    private void createReactionTimeTable() {
        String reactionTime = "";
        String firstMovement = "";
        String nextImage;
        String nextDirection;
        boolean firstLine = true;

        for (int x = 0; x < resultsData.getRowCount(); x++) {    //Calculate reactionTimes  Start at next line.
            ArrayList<Object> result = new ArrayList<Object>();
            String id = resultsData.getValueAt(x, 0).toString();
            String run = resultsData.getValueAt(x, 1).toString();
            String image = resultsData.getValueAt(x, 2).toString();      //Next image in the list
            String direction = resultsData.getValueAt(x, 3).toString();
            String type = resultsData.getValueAt(x, 4).toString();

            if (x < resultsData.getRowCount() - 1) {
                nextImage = resultsData.getValueAt(x + 1, 2).toString();
                nextDirection = resultsData.getValueAt(x + 1, 3).toString();  //Next direction in the list
            } else {      //Make sure the last row get read to
                nextImage = "";
                nextDirection = "";
            }


            if (image.equals(nextImage) && direction.equals(nextDirection)) {  //Same image and condition.
                reactionTime = resultsData.getValueAt(x + 1, 6).toString();
                if (firstLine) {
                    firstMovement = resultsData.getValueAt(x, 5).toString();
                    firstLine = false;
                }

            } else {
                firstLine = true;
                if (correctStartPosition(firstMovement) && checkTime(minRtime, maxRtime, Integer.parseInt(reactionTime))) {
                    if (wrongFirstDirection(firstMovement, direction)) {
                        updateMistakeCount(id);
                    }

                    result.add(id);
                    result.add(run);
                    result.add(image);
                    result.add(direction);
                    result.add(type);
                    result.add(reactionTime);
                    exportData.add(result);
                }
            }
        }
    }

    /*
    Checks if the joystick was in the center at the moment the image was shown. This is based on the asked direction.
        The correct position is always the center position + or - 1
     */
    private boolean correctStartPosition(String firstPos) {
        int pos = Integer.parseInt(firstPos);   //First position measured
        return !(pos != centerPos - 1 && pos != centerPos + 1);
    }


    /*
      Checks if the joystick was pushed or pulled in the right direction, or that the participant pulled the wrong direction first
      This is only a problem if it happens too often.
     */
    private boolean wrongFirstDirection(String firstPos, String direction) {
        int dir = Integer.parseInt(direction);
        int pos = Integer.parseInt(firstPos);
        if (dir == AATImage.PULL) {
            if (pos != centerPos + 1) {
                return true;           //Add a mistake
            }
        } else if (dir == AATImage.PUSH) {
            if (pos != centerPos - 1) {     //Wrong start movement
                return true;
            }
        }
        return false;
    }

    /*
    User can define a minimum and maximum for the reactionTimes. This method check whether these times fall within the set
    boundaries.
     */
    private boolean checkTime(int low, int high, int reactionTime) {
        return reactionTime > low && reactionTime < high;
    }

    //Update the mistakes counter for a given id.
    private void updateMistakeCount(String id) {
        int count = 0;
        if (NoMistakes.containsKey(id)) {
            count = NoMistakes.get(id);    //Gets the current count value;
            NoMistakes.remove(id);        //Remove value
        }
        count++;
        NoMistakes.put(id, count);  //Re-add value
    }


    /*
    Writes the data to file, but first checks if one ore more participants made too much mistakes. If so then these results
    are not written to the export file.
     */
    public void writeToFile(File file) throws ExportDataException {
        createReactionTimeTable();
        transposeData();
        int totalImage = model.getTest().getTotalImageCount();
        float fraction = 1f / (100f / errorPerc);
        float maxErrors = (float) totalImage * fraction;
        for (String id : NoMistakes.keySet()) {
            float count = NoMistakes.get(id);
            System.out.println("Max errors " + maxErrors + " ID " + id + "has " + count);
            if (count > maxErrors) {
                removeID(id);
            }
        }
        if (exportData.getRowCount() > 0) {
            if(transposed) {
                exportData = this.transposeData();
            }
         //   CSVWriter writer = new CSVWriter(exportData);
            CSVWriter.writeData(file, false,exportData);
        } else {
            throw new ExportDataException("There is no data to export");
        }
    }

    /*
    Walks through the export Table and removes every instance of the given id
     */
    private void removeID(String IDremove) {
        for (int x = exportData.getRowCount() - 1; x >= 0; x--) {
            //     System.out.println("X waarde "+x);
            String userID = exportData.getValueAt(x, 0).toString();
            if (userID.equals(IDremove)) {
                exportData.removeRow(x);
            }
        }
    }

    public class ExportDataException extends Exception {

        public ExportDataException(String error) {
            super(error);
        }
    }

    private DynamicTableModel transposeData() {
        DynamicTableModel transposedData = new DynamicTableModel();
        ArrayList<Object> columnNames = new ArrayList<Object>();
        ArrayList<String> originalFileNames = model.getTest().getAllFileNames();
    //    ArrayList<String> practiceNames = model.getTest().getPracticeNames();     //Practice names are not necessary
        ArrayList<Integer> ids = new ArrayList<Integer>();
    //    boolean hasPractice = model.getTest().hasPractice();
        int trials = model.getTest().getRepeat();

        columnNames.add("ID"); //Create the collumn names List. Header for the output file
     //   for (String practice : practiceNames) {
       //     columnNames.add(practice);
      //  }
        for (int i = 0; i < trials; i++) {
            for (String fileName : originalFileNames) {
                System.out.println("orig "+fileName);
                columnNames.add(fileName+"_"+i);
            }
        }
        transposedData.setColumnNames(columnNames);

        //Find all id's

        for (int x = 0; x < exportData.getRowCount(); x++) {
            int currentID = Integer.parseInt(exportData.getValueAt(x, 0).toString());
            if (!ids.contains(currentID)) {
                ids.add(currentID);
            }
            //Create column names, based on filename, direction and type

        }
        //Doorloop de lijst met id's en voeg zo per id alle data toe aan de output tabel.
        for (int id : ids) {
            ArrayList<Object> data = new ArrayList<Object>();
            data.add(id);
       //     if (model.getTest().hasPractice()) {           //No need for practice in the results
         //       HashMap<String, Integer> results = getValuesForTrial(0, id);
           //     addTransposedData(results, practiceNames, data, id);
         //   }
            for (int x = 1; x < trials + 1; x++) {

                HashMap<String, Integer> results = getValuesForTrial(x, id);
                addTransposedData(results, originalFileNames, data, id);
            }
        //    System.out.println("Data length "+data.size());
            transposedData.add(data);
        }
         return transposedData;
    }

    private void addTransposedData(HashMap<String, Integer> results, ArrayList<String> list, ArrayList<Object> data, int id) {
        for (String file : list) {
     //       System.out.println("Find "+file);
            if (results.containsKey(file)) {
                data.add(results.get(file));
            } else {
                data.add("N/A");
            }
        }
    }
    //Nu de data doorlopen om de transposeData te vullen. Telkens zoeken naar het juiste plaatje

    /**
     * Returns the list of images for a certain
     * TODO: Exporteren van de demo gaat niet goed.
     * @param trial
     * @param id
     * @return
     */
    private HashMap<String, Integer> getValuesForTrial(int trial, int id) {
        HashMap<String, Integer> valuesForTrial = new HashMap<String, Integer>();
        for (int x = 0; x < exportData.getRowCount(); x++) {
            int currentID = Integer.parseInt(exportData.getValueAt(x, 0).toString());
            int currentTrial = Integer.parseInt(exportData.getValueAt(x, 1).toString());
            String direction = "pull";
            if (Integer.parseInt(exportData.getValueAt(x, 3).toString()) == AATImage.PUSH) {
                direction = "push";
            }

            String type = "practice";
            if (Integer.parseInt(exportData.getValueAt(x, 4).toString()) == AATImage.AFFECTIVE) {
                type = "affective";
            }
            if (Integer.parseInt(exportData.getValueAt(x, 4).toString()) == AATImage.NEUTRAL) {
                type = "neutral";
            }
            if (currentID == id && trial == currentTrial) {    //Only do something for the current id and trial number
                String fileName = exportData.getValueAt(x, 2).toString();
             //   System.out.println("Current file " + fileName);
                if (trial == 0) {//is practice
                    if (model.getTest().hasColoredBorders()) {    //Practice with colored border
                        if (model.getTest().practiceDir != null) {
                           fileName = fileName+"_" + direction;
                        } else {
                           fileName = fileName + "_" + direction;
                        }
                    } else {    //practice without colored borders
                        fileName = fileName +"_practice";
                    }

                } else {    //Anders dan practice
                    if (model.getTest().hasColoredBorders()) {
                        //list.add(image.getName() + "_pull_neutral");
                        fileName = fileName + "_" + direction + "_" + type;
                    } else {
                        fileName = fileName + "_" + type;
                    }

                    // }
                }
                int reactionTime = Integer.parseInt(exportData.getValueAt(x, 5).toString());
            //    System.out.println("FileName output " + fileName);
                valuesForTrial.put(fileName, reactionTime);
            }
        }
        return valuesForTrial;
    }
}


