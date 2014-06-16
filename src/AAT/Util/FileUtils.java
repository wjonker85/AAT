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

package AAT.Util;

import org.w3c.dom.Document;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by marcel on 1/19/14.
 * Different helper methods to deal with files. Read images from disk or get the relative path compared to the current working directory.
 */


public class FileUtils {


    private static final String IMAGE_PATTERN =
            "([^\\s]+(\\.(?i)(jpeg|jpg|png|gif|bmp))$)";
    /**
     * Filter so that only the image files in a directory will be selected
     */
    static FileFilter extensionFilter = new FileFilter() {
        public boolean accept(File file) {
            Pattern pattern = Pattern.compile(IMAGE_PATTERN);
            Matcher matcher = pattern.matcher(file.getName());
            return matcher.matches();
        }
    };

    /**
     * Loads all image files in a given directory. Extension filter with regular expression.
     *
     * @param dir Directory containing images
     * @return ArrayList<File> with all image files in a directory
     */
    public static ArrayList<File> getImages(File dir) {
        File[] files = dir.listFiles(extensionFilter);
        return new ArrayList<File>(Arrays.asList(files));
    }

    /**
     * Computes the path for a file relative to a given base, or fails if the only shared
     * directory is the root and the absolute form is better.
     *
     * @param base File that is the base for the result
     * @param name File to be "relativized"
     * @return the relative name
     */

    public static String getRelativePath(File base, File name) {
        if (base != null && name != null) {
            File parent = base.getParentFile();
            if (name.getName().length() > 0) {
                try {
                    if (parent == null) {
                        return name.getAbsolutePath();
                    }
                    String bpath = base.getCanonicalPath();
                    String fpath = name.getCanonicalPath();

                    if (fpath.startsWith(bpath)) {
                        return fpath.substring(bpath.length() + 1);
                    } else {
                        return (".." + File.separator + getRelativePath(parent, name));
                    }
                } catch (Exception e) {
                    return name.getAbsolutePath();
                }
            }
        }
        return "";
    }


    public static void writeDataToFile(File file, Document doc) {
        try {
            // Prepare the DOM document for writing
            Source source = new DOMSource(doc);

            // Prepare the output file
            Result result = new StreamResult(file);

            // Write the DOM document to the file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", 4);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
