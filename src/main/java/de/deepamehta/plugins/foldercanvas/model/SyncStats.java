package de.deepamehta.plugins.foldercanvas.model;

import java.io.File;
import java.util.logging.Logger;



public class SyncStats {

    // ---------------------------------------------------------------------------------------------- Instance Variables

    public int filesAdded = 0;
    public int foldersAdded = 0;
    public int filesRemoved = 0;
    public int foldersRemoved = 0;

    private Logger logger = Logger.getLogger(getClass().getName());

    // ----------------------------------------------------------------------------------------- Package Private Methods

    void countAsAdded(File file) {
        if (file.isDirectory()) {
            logger.info("# Adding folder: " + file);
            foldersAdded++;
        } else {
            logger.info("# Adding file: " + file);
            filesAdded++;
        }
    }

    void countAsRemoved(File file, boolean isDirectory) {
        // Note: the file doesn't exist anymore. We must rely on external info to detect directories.
        if (isDirectory) {
            logger.info("# Removing folder: " + file);
            foldersRemoved++;
        } else {
            logger.info("# Removing file: " + file);
            filesRemoved++;
        }
    }
}
