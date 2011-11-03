package io;

import DataStructures.AATImage;
import DataStructures.DynamicTableModel;
import DataStructures.ResultsDataTableModel;
import Model.AATModel;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/29/11
 * Time: 4:24 PM
 * This class is used to export all the registered data in a form that can be used to do the data analysis.
 */
public class DataExporter {

    private DynamicTableModel partipantsTable;
    private DynamicTableModel exportData;
    private ResultsDataTableModel resultsData;
    private HashMap<String, Integer> NoMistakes;
    private AATModel model;
    private int centerPos;
    private int minRtime, maxRtime, errorPerc;

    //Constructor, based on information from the views.ExportDataDialog
    public DataExporter(AATModel model, int minRTime, int maxRtime, int errorPerc) {
        this.model = model;
        this.minRtime = minRTime;
        this.maxRtime = maxRtime;
        this.errorPerc = errorPerc;
        centerPos = (model.getStepRate() + 1) / 2;
        CSVReader partReader = new CSVReader(model.getParticipantsFile());       //Read all stored participants data
        CSVReader dataReader = new CSVReader(model.getDataFile());              //read all stored measurement data.
        partipantsTable = new DynamicTableModel();
        partipantsTable.setColumnNames(partReader.getColumnNames());
        partipantsTable.add(partReader.getData());
        resultsData = new ResultsDataTableModel();
        resultsData.add(dataReader.getData());
        exportData = new DynamicTableModel();
        ArrayList<String> columnNames = new ArrayList<String>();
        columnNames.add("ID");
        columnNames.add("Run");
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
        String nextImage = "";
        String nextDirection = "";
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
                } else {
                    System.out.println("Results not added");
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
        if (pos != centerPos - 1 && pos != centerPos + 1) {
            return false;
        }
        return true;
    }


    /*
      Checks if the joystick was pushed or pulled in the right direction, or that the participant pulled the wrong direction first
      This is only a problem if it happens too often.
     */
    private boolean wrongFirstDirection(String firstPos, String direction) {
        int dir = Integer.parseInt(direction);
        int pos = Integer.parseInt(firstPos);
        if (dir == AATImage.PULL) {
            if (pos != centerPos - 1) {
                return true;           //Add a mistake
            }
        } else if (dir == AATImage.PUSH) {
            if (pos != centerPos + 1) {     //Wrong start movement
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
        if (reactionTime > low && reactionTime < high) {
            return true;
        } else {
            return false;
        }
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
        int totalImage = model.getTotalImageCount();
        float fraction = 1f / (100f / errorPerc);
        float maxErrors = (float) totalImage * fraction;

        for (String id : NoMistakes.keySet()) {
            float count = NoMistakes.get(id);
            if (count > maxErrors) {
                removeID(id);
            }
        }
        if(exportData.getRowCount()>0) {
        CSVWriter writer = new CSVWriter(exportData);
        writer.writeData(file,false);
        }
        else {
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
}


