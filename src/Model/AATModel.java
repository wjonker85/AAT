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

package Model;

import AAT.AbstractAAT;
import AAT.Configuration.Validation.AATValidator;
import AAT.Configuration.Validation.FalseConfigException;
import AAT.HighMemoryAAT;
import DataStructures.AATDataRecorder;
import DataStructures.AATImage;
import DataStructures.ParticipantData;
import org.w3c.dom.Document;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;


/**
 * Created by IntelliJ IDEA.
 * User: marcel
 * Date: 10/4/11
 * Time: 3:12 PM
 * This model does all the logic for the AAT. It uses the Model-View-Controller pattern. So it does the communication between
 * the controllers and the Views. Controllers are the joystick and the user input from the main Frame. Views are the test view and
 * results view.
 * <p/>
 * When a picture has to be pulled or pushed all joystick movements are recorded with their corresponding reaction times.
 * All the data for all participants are stored in a file and selections from those results can be exported so it can be analyzed
 * with R or SPSS. A researcher can choose the way the data has to be prepared. E.g. include mistakes or not.
 * <p/>
 * When a user has done the test he can see his results with four boxplots. One for every condition.
 * <p/>
 * <p/>
 * <p/>
 */

public class AATModel extends Observable {


    private AbstractAAT newAAT;

    //Test status
    private final static int TEST_STOPPED = 0;
    private final static int TEST_WAIT_FOR_QUESTIONS = 1;
    private final static int PRACTICE_IMAGE_LOADED = 2;
    private final static int TEST_WAIT_FOR_TRIGGER = 3;
    private final static int IMAGE_LOADED = 4;
    private final static int TEST_SHOW_FINISHED = 5;
    private final static int TEST_SHOW_RESULTS = 6;

    //Test variables
    private int repeat;
    private int breakAfter;
    private int count; //Counts the number of images shown.
    private ArrayList<AATImage> testList; //Random list that contains the push or pull images.

    //progress variables
    private int run;
    private boolean practice;
    //Measurement

    private AATDataRecorder AATDataRecorder;
    private ParticipantData newParticipant;
    private long startMeasure;
    private int resize;

    //Current image and current status of the test
    private AATImage current;
    private int testStatus;

    private int previousPos; //TTo keep track of the joystick position
    private int previousDataPos;

    private int lastSize;
    private boolean saveData;

    private int export_id;
    private Document exportDocument;
    private AATDataRecorder exportAATDataRecorder;

    //Constructor.
    public AATModel() {
        //   testStatus = AATModel.TEST_STOPPED;
    }

//------------------------------initialise AAT --------------------------------------------------

    //Load a new AAT from file
    public void loadNewAAT(File configFile) throws FalseConfigException {
        newAAT = new HighMemoryAAT(AATValidator.validatedTestConfiguration(configFile));
        AATDataRecorder = new AATDataRecorder(newAAT, this);
    }

    //Starts a new instance of the AAT. With no. times it has to repeat and when there will be a break.
    public void startTest(boolean saveData) {
        this.saveData = saveData; //Don't save the data when testing configuration
        this.repeat = newAAT.getTestConfiguration().getTrials();
        this.breakAfter = newAAT.getTestConfiguration().getBreakAfter();
        this.previousDataPos = (newAAT.getTestConfiguration().getDataSteps() + 1) / 2; //Set the previous position to the center Data position
        this.previousPos = (newAAT.getTestConfiguration().getStepSize() + 1) / 2;  //Set the previous position to the center display position
        if (newAAT.getTestConfiguration().getHasPractice()) {        //If set in the config, first do a practice.
            practice = true; //Set the test to practice mode
            repeat++;     //Make these one higher, because of the practice
            breakAfter++;
            count = 0; //reset the counter
            testList = newAAT.createRandomPracticeList();
        } else {   //Test has no practice so create random list of images
            practice = false;
            if (newAAT.getTestConfiguration().getColoredBorders()) {
                testList = newAAT.createRandomListBorders(); //create a new Random list
            } else {
                testList = newAAT.createRandomListNoBorders();
            }
        }
        count = 0;        //reset counters
        run = 0;
        int id = AATDataRecorder.getHighestID();
        id++;          //new higher id
        newParticipant = new ParticipantData(id, newAAT.getTestConfiguration().getTestID());

        if (newAAT.getTestConfiguration().getDisplayQuestions().equals("Before")) {
            testStatus = AATModel.TEST_WAIT_FOR_QUESTIONS;
            this.setChanged();
            notifyObservers("Show questions");      //Notify the observer that a new test is started.
        } else {     //When there are no questions te begin with, just start the test
            this.setChanged();
            notifyObservers("Start");
            testStatus = AATModel.TEST_WAIT_FOR_TRIGGER;   //Start the test
        }

    }

    public final AbstractAAT getTest() {
        return newAAT;
    }

    public final AATDataRecorder getAATDataRecorder() {
        return AATDataRecorder;
    }

    public void setExport_id(int id, boolean current) {
        this.export_id = id;
        this.setChanged();
        if (current) {
            notifyObservers("Export");
        } else {
            notifyObservers("Export_foreign");
        }
    }

    public void EscapeAction() {
        this.setChanged();
        notifyObservers("Escape");
    }

    public void setExportAATDataRecorder(AATDataRecorder AATDataRecorder) {
        exportAATDataRecorder = AATDataRecorder;
    }

    public AATDataRecorder getExportAATDataRecorder() {
        return exportAATDataRecorder;
    }

    public void setExport_idNoNotify(int id) {
        this.export_id = id;
    }

    public void setDataLoadedExport() {
        this.setChanged();
        notifyObservers("Data_loaded_export");
    }

    public Document getExportDocument() {
        return exportDocument;
    }

    public void setExportDocument(Document doc) {
        this.exportDocument = doc;
    }

    public int getExport_id() {
        return export_id;
    }

//---------------------------Test Progress---------------------------------------------------------

    /**
     * This method determines the next step to be taken in the test.
     * Based on testStatus and the current image shown to the participant
     * Makes sure that the correct images are shown(practice/real test). Puts the test on break
     * if that's necessary. And watches when the test is finished. Together with the joystick inputs this method
     * determines the progress of the AAT.
     */
    private synchronized void NextStep() {

        if (testStatus == AATModel.PRACTICE_IMAGE_LOADED) {
            if (count < testList.size()) {
                showNextImage();
                return;
            } else {
                testStatus = AATModel.TEST_WAIT_FOR_TRIGGER;

                if (newAAT.getTestConfiguration().getColoredBorders()) {
                    testList = newAAT.createRandomListBorders(); //create a new Random list
                } else {
                    testList = newAAT.createRandomListNoBorders();
                }

                this.setChanged();
                this.notifyObservers("Practice ended");
                run++;
                practice = false; //Practice has ended

                count = 0; //reset the counter
                return;
            }
        }
        if (testStatus == AATModel.IMAGE_LOADED) {
            if (count < testList.size()) {     //Just show the next image
                showNextImage();
            } else {          //No more images in the list
                testStatus = AATModel.TEST_WAIT_FOR_TRIGGER;
                this.setChanged();
                run++;
                count = 0;
                if (run == breakAfter) {    //Test needs a break
                    if (newAAT.getTestConfiguration().getColoredBorders()) {
                        testList = newAAT.createRandomListBorders(); //create a new Random list
                    } else {
                        testList = newAAT.createRandomListNoBorders();
                    }
                    current = testList.get(0);
                    notifyObservers("Break");      //Notify observer that there is a break.

                } else if (run == repeat) {   //No more runs left, Test has ended
                    testStatus = AATModel.TEST_SHOW_FINISHED;    //Notify observers about it
                    if (!newAAT.getTestConfiguration().getDisplayQuestions().equals("After")) {  //Questionnaire has to be added at the end
                        if (saveData) {
                            AATDataRecorder.addParticipant(newParticipant);
                        }
                    }

                    this.setChanged();
                    notifyObservers("Show finished");   //First show black screen

                } else {           //Continue with a new run

                    if (newAAT.getTestConfiguration().getColoredBorders()) {
                        testList = newAAT.createRandomListBorders(); //create a new Random list
                    } else {
                        testList = newAAT.createRandomListNoBorders();
                    }
                    testStatus = AATModel.IMAGE_LOADED;
                    count = 0;
                    showNextImage();
                }
            }
        }
    }

    /**
     * Displays the next image. Will display from the practicelist or the normal testlist, depending on the status
     * of the test
     */
    private synchronized void showNextImage() {
        current = testList.get(count);    //change current to the next image
        count++;
        this.setChanged();
        notifyObservers("Show Image");      //Notify observers
        startMeasure(); //Start the measurement.
    }

    //Returns the next Image
    public synchronized BufferedImage getNextImage() {
        return current.getImage();
    }

//---------------------Getter and Setter methods for the different Observers and Controllers -------------

    /**
     * @return an integer from 1 to stepSize, which determines how much larger or smaller the picture
     * has to be shown on the screen
     */
    public int getPictureSize() {
        return resize;
    }

    /**
     * The AATImage object also stores the direction (push or pull) of an image. This method returns the
     * direction for the image that is currently shown on the screen
     *
     * @return Direction for the current image
     */
    public int getDirection() {
        return current.getDirection();
    }

//-------------------- Measure the data and write them to file when finished ---------------

    /**
     * Every time when a new image is shown on the screen a new measure is started.
     */
    private void startMeasure() {
        newParticipant.newMeasure(run, current.toString(), current.getDirection(), current.getType()); //Begin met de metingen opslaan.
        startMeasure = System.currentTimeMillis();  //Begintijd
    }

    /**
     * This method is called every time when the joystick is pulled or pushed while a picture is shown on the
     * screen. It returns the time between the start of the measure and the current joystick movement
     *
     * @return ReactionTime in ms.
     */
    private long getMeasurement() {
        return System.currentTimeMillis() - startMeasure;
    }


//------------------------------Get results from the test -------------------------------

    /**
     * This methode creates a hashmap with float arrays containing the results of a single participant.
     * This data is used as input for the boxplots shown at the end of the test
     *
     * @return Hashmap with results for the boxplot
     */
    public synchronized HashMap<String, float[]> getResultsPerCondition() {
        HashMap<String, float[]> results = new HashMap<String, float[]>();
        String pull = newAAT.getTestConfiguration().getPullTag();
        String push = newAAT.getTestConfiguration().getPushTag();
        String nDir = newAAT.getTestConfiguration().getNeutralDir().getName();
        String aDir = newAAT.getTestConfiguration().getAffectiveDir().getName();
        results.put(pull + " & " + nDir, newParticipant.getMeasures(AATImage.PULL, AATImage.NEUTRAL));
        results.put(pull + " & " + aDir, newParticipant.getMeasures(AATImage.PULL, AATImage.AFFECTIVE));
        results.put(push + " & " + nDir, newParticipant.getMeasures(AATImage.PUSH, AATImage.NEUTRAL));
        results.put(push + " & " + aDir, newParticipant.getMeasures(AATImage.PUSH, AATImage.AFFECTIVE));
        return results;
    }

    public String getLabelPerCondition(int type, int direction) {
        if (type == AATImage.AFFECTIVE) {
            String aDir = newAAT.getTestConfiguration().getAffectiveDir().getName();
            if (direction == AATImage.PULL) {
                String pull = newAAT.getTestConfiguration().getPullTag();
                return pull + " & " + aDir;
            } else if (direction == AATImage.PUSH) {
                String push = newAAT.getTestConfiguration().getPushTag();
                return push + " & " + aDir;
            }
        } else if (type == AATImage.NEUTRAL) {
            String nDir = newAAT.getTestConfiguration().getNeutralDir().getName();
            if (direction == AATImage.PULL) {
                String pull = newAAT.getTestConfiguration().getPullTag();
                return pull + " & " + nDir;
            } else if (direction == AATImage.PUSH) {
                String push = newAAT.getTestConfiguration().getPushTag();
                return push + " & " + nDir;
            }
        }
        return "";
    }

    public HashMap<String, float[]> getResultsPerCondition(int type, int direction) {
        HashMap<String, float[]> results = new HashMap<String, float[]>();
        String pull = newAAT.getTestConfiguration().getPullTag();
        String push = newAAT.getTestConfiguration().getPushTag();
        String nDir = newAAT.getTestConfiguration().getNeutralDir().getName();
        String aDir = newAAT.getTestConfiguration().getAffectiveDir().getName();
        if (type == AATImage.AFFECTIVE) {
            if (direction == AATImage.PULL) {
                results.put(pull + " & " + aDir, newParticipant.getMeasures(AATImage.PULL, AATImage.AFFECTIVE));
            } else if (direction == AATImage.PUSH) {
                results.put(push + " & " + aDir, newParticipant.getMeasures(AATImage.PUSH, AATImage.AFFECTIVE));
            }
        } else if (type == AATImage.NEUTRAL) {
            if (direction == AATImage.PULL) {
                results.put(pull + " & " + nDir, newParticipant.getMeasures(AATImage.PULL, AATImage.NEUTRAL));
            } else if (direction == AATImage.PUSH) {
                results.put(push + " & " + nDir, newParticipant.getMeasures(AATImage.PUSH, AATImage.NEUTRAL));
            }
        }
        return results;
    }

    //--------------Input from the joystick controller-------------------------------------//


    /**
     * Gets changes in the movement from the y-axis of the joystick.
     * Test status has to be changed when the joystick reaches the maximum distance. It depends on the direction that belongs to
     * the current image. When the user performs the requested action, when finished the test status has to go to wait for trigger.
     *
     * @param displayValue This value represents the position of the joystick. This information comes from the joystick controller
     */
    public synchronized void changeYaxis(int value, int displayValue) {
        if (testStatus == AATModel.IMAGE_LOADED || testStatus == AATModel.PRACTICE_IMAGE_LOADED) { //Only listen when there is an image loaded
            if (displayValue != previousPos) {
                previousPos = displayValue;
                if (value != previousDataPos) {
                    newParticipant.addResult(value, getMeasurement());     //add results to the other measurements
                    previousDataPos = value;
                }
                resize = displayValue;
                this.setChanged();
                notifyObservers("Y-as");
            }
        }
    }

    public synchronized void maxPullorPush(int value) {
        if (testStatus == AATModel.IMAGE_LOADED || testStatus == AATModel.PRACTICE_IMAGE_LOADED) { //Only listen when there is an image loaded
            newParticipant.addResult(value, getMeasurement());     //adds last results to the other measurements
            if (current.getDirection() == AATImage.PULL && value == newAAT.getTestConfiguration().getDataSteps()) {   //check if the requested action has been performed
                testStatus = AATModel.TEST_WAIT_FOR_TRIGGER;
                lastSize = newAAT.getTestConfiguration().getStepSize();
                removeImage();
            } else if (current.getDirection() == AATImage.PUSH && value == 1) {
                testStatus = AATModel.TEST_WAIT_FOR_TRIGGER;
                lastSize = 1;
                removeImage();
            }
        }
    }

    public int getLastSize() {
        return lastSize;
    }


    /**
     * This method is called when a participant has pulled or pushed the image completely. It notifies the observers
     * that the current image has to be removed from the view.
     */


    private synchronized void removeImage() {
        this.setChanged();
        notifyObservers("Wait screen");
        previousPos = resize;
        previousDataPos = (newAAT.getTestConfiguration().getDataSteps() + 1) / 2;
    }

    /**
     * When the test is waiting for the trigger, check if the trigger is pressed and then change the test status
     * to image loaded. then call nextstep so the model can determine the appropriate next action to take
     */
    public synchronized void triggerPressed() {

        switch (testStatus) {
            case AATModel.TEST_SHOW_RESULTS:
                testStatus = AATModel.TEST_STOPPED;
                this.setChanged();
                if (newAAT.getTestConfiguration().getDisplayQuestions().equals("After")) {
                    this.notifyObservers("Show questions");
                } else {
                    this.notifyObservers("Finished");
                }
                break;


            case AATModel.TEST_SHOW_FINISHED:
                if (newAAT.getTestConfiguration().getPlotType().length() > 0) {
                    testStatus = AATModel.TEST_SHOW_RESULTS;
                    this.setChanged();
                    this.notifyObservers("Display results");
                } else {
                    testStatus = AATModel.TEST_STOPPED;
                    if (newAAT.getTestConfiguration().getDisplayQuestions().equals("After")) {
                        this.setChanged();
                        this.notifyObservers("Show questions");
                    } else {
                        this.setChanged();
                        this.notifyObservers("Finished");
                    }
                }
                break;

            case AATModel.TEST_WAIT_FOR_TRIGGER:
                resize = (newAAT.getTestConfiguration().getStepSize() + 1) / 2; //Set back to center
                if (practice) {
                    testStatus = AATModel.PRACTICE_IMAGE_LOADED;
                } else {
                    testStatus = AATModel.IMAGE_LOADED;
                }
                NextStep();    //Determine next step in the test
                break;
        }
    }

//--------------------------User input, answers to the questions ----------------------------------------

    /**
     * This method is called when the participant has answered the optional questions. The answers are added to
     * the participants data file and the Views are notified that the AAT can be started
     *
     * @param extraQuestions The answers to the extra questions. The hashmap contains the key and the answer
     */
    public void collectQuestionnaireAnswers(HashMap<String, String> extraQuestions) {
        this.newParticipant.addQuestionData(extraQuestions);
        this.setChanged();
        if (newAAT.getTestConfiguration().getDisplayQuestions().equals("After")) {
            if (saveData) {
                AATDataRecorder.addParticipant(newParticipant);
            }
            this.notifyObservers("Finished");
        } else {
            this.notifyObservers("Start");
            testStatus = AATModel.TEST_WAIT_FOR_TRIGGER;   //Test status is wait for the user to press the trigger button.
        }
    }
}