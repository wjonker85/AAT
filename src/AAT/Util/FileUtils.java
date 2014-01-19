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
 */




public class FileUtils {


    //regex for extension filtering
    private static Pattern pattern;

    private static final String IMAGE_PATTERN =
            "([^\\s]+(\\.(?i)(jpeg|jpg|png|gif|bmp))$)";

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
     * Filter so that only the image files in a directory will be selected
     */
    static FileFilter extensionFilter = new FileFilter() {
        public boolean accept(File file) {
            pattern = Pattern.compile(IMAGE_PATTERN); //create regex
            Matcher matcher = pattern.matcher(file.getName());
            return matcher.matches();
        }
    };


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