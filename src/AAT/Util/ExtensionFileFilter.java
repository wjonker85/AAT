package AAT.Util;

import java.io.File;

/**
 * Created by marcel on 1/29/14.
 */

//filter voor de file extensions. Komt ook van het internet. Wordt nu gebruik om .input en csv bestanden te filteren.
public class ExtensionFileFilter extends javax.swing.filechooser.FileFilter {
    String description;

    String extensions[];

    public ExtensionFileFilter(String description, String extensions[]) {
        if (description == null) {
            this.description = extensions[0];
        } else {
            this.description = description;
        }
        this.extensions = extensions.clone();
        toLower(this.extensions);
    }

    private void toLower(String array[]) {
        for (int i = 0, n = array.length; i < n; i++) {
            array[i] = array[i].toLowerCase();
        }
    }

    public String getDescription() {
        return description;
    }

    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        } else {
            String path = file.getAbsolutePath().toLowerCase();
            for (String extension : extensions) {
                if ((path.endsWith(extension) && (path.charAt(path.length() - extension.length() - 1)) == '.')) {
                    return true;
                }
            }
        }
        return false;
    }
}

