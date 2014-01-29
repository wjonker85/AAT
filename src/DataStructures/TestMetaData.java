package DataStructures;

import org.w3c.dom.Document;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by marcel on 1/29/14.
 *
 * This data structure contains all the import variables that are needed to correctly interpret the data coming from the data.xml file.
 */
public class TestMetaData {

    private int export_id, trials, centerpos;
    private String afflabel,neutLabel,pushTag,pullTag;
    private Document data;
    private ArrayList<File> neutralImages,affectiveImages,practiceImages;

    public TestMetaData(int export_id
            ,int trials
            , Document data
            , String affLabel
            , String neutLabel
            , String pushTag
            , String pullTag
            , int centerpos
          )
    {
         this.export_id = export_id;
        this.trials = trials;
        this.data = data;
        this.afflabel = affLabel;
        this.neutLabel = neutLabel;
        this.pullTag = pullTag;
        this.pushTag = pushTag;
        this.centerpos = centerpos;
    }


    public int getExport_id() {
        return export_id;
    }

    public int getCenterPos() {
        return centerpos;
    }

    public Document getData() {
        return data;
    }

    public String getAfflabel() {
        return afflabel;
    }

    public String getNeutLabel() {
        return neutLabel;
    }

    public String getPushTag() {
        return pushTag;

    }

    public String getPullTag() {
        return pullTag;
    }

    public int getCenterpos() {
        return centerpos;
    }

    public void setNeutralImages(ArrayList<File> neutralImages)
    {
        this.neutralImages = neutralImages;
    }

    public ArrayList<File> getNeutralImages()
    {
        return neutralImages;
    }

    public void setAffectiveImages(ArrayList<File> affectiveImages)
    {
        this.affectiveImages = affectiveImages;
    }


    public ArrayList<File> getAffectiveImages()
    {
        return affectiveImages;
    }

    public void setPracticeImages(ArrayList<File> practiceImages)
    {
        this.practiceImages = practiceImages;
    }

    public ArrayList<File> getPracticeImages() {
        return practiceImages;
    }

}
