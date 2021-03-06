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

package AAT.Configuration;

import org.w3c.dom.Document;

/**
 * Created by marcel on 1/29/14.
 * <p/>
 * This data structure contains all the import variables that are needed to correctly interpret the data coming from the data.xml file.
 */
public class TestMetaData {

    private int export_id, trials, centerpos;
    private String afflabel, neutLabel, pushTag, pullTag;
    private Document data;
    private boolean coloredBorders;

    public TestMetaData(int export_id
            , int trials
            , Document data
            , String affLabel
            , String neutLabel
            , String pushTag
            , String pullTag
            , int centerpos
            , boolean coloredBorders
    ) {
        this.export_id = export_id;
        this.trials = trials;
        this.data = data;
        this.afflabel = affLabel;
        this.neutLabel = neutLabel;
        this.pullTag = pullTag;
        this.pushTag = pushTag;
        this.centerpos = centerpos;
        this.coloredBorders = coloredBorders;
    }


    public int getTrials() {
        return trials;
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

    public boolean hasColoredBorders() {
        return coloredBorders;
    }

}
