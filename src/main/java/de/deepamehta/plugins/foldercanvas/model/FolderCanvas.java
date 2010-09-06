package de.deepamehta.plugins.foldercanvas.model;

import de.deepamehta.plugins.files.FilesPlugin;
import de.deepamehta.plugins.topicmaps.TopicmapsPlugin;
import de.deepamehta.plugins.topicmaps.model.Topicmap;
import de.deepamehta.plugins.topicmaps.model.TopicmapTopic;

import de.deepamehta.core.model.RelatedTopic;
import de.deepamehta.core.model.Relation;
import de.deepamehta.core.model.Topic;
import de.deepamehta.core.service.CoreService;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.awt.Point;
import java.io.File;

import static java.util.Arrays.asList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;



/**
 * A topicmap that is bound to a file system folder and that can synchronize with that folder.
 */
public class FolderCanvas extends Topicmap {

    // ---------------------------------------------------------------------------------------------- Instance Variables

    private TopicmapsPlugin topicmapsPlugin = (TopicmapsPlugin) dms.getPlugin("de.deepamehta.3-topicmaps");
    private FilesPlugin filesPlugin         = (FilesPlugin)     dms.getPlugin("de.deepamehta.3-files");

    private Logger logger = Logger.getLogger(getClass().getName());

    // ---------------------------------------------------------------------------------------------------- Constructors

    public FolderCanvas(long topicmapId, CoreService dms) {
        super(topicmapId, dms);
    }

    // -------------------------------------------------------------------------------------------------- Public Methods

    public SyncStats synchronize() throws Exception {
        Topic folderTopic = getFolderTopic();
        String syncPath = (String) dms.getTopicProperty(folderTopic.id, "de/deepamehta/core/property/Path");
        SyncStats syncStats = new SyncStats();
        GridPositioning positioning = new Topicmap.GridPositioning(600, 0);
        // 1) scan files and possibly add file/folder topics to topicmap
        File[] files = new File(syncPath).listFiles();
        for (File file : files) {
            syncFile(file, positioning, syncStats);
        }
        // 2) iterate through topicmap and possibly remove file/folder topics
        List fileList = asList(files);
        for (TopicmapTopic topic : topics.values()) {
            syncTopic(topic, fileList, syncStats);
        }
        //
        logger.info("### Synchronization of \"" + syncPath + "\" with topicmap " + topicmapId + " complete =>\n" +
            syncStats.filesAdded   + " files added\n"   + syncStats.foldersAdded   + " folders added\n" +
            syncStats.filesRemoved + " files removed\n" + syncStats.foldersRemoved + " folders removed");
        //
        return syncStats;
    }

    // -------------------------------------------------------------------------------------------- Public Inner Classes

    public class SyncStats {

        public int filesAdded = 0;
        public int foldersAdded = 0;
        public int filesRemoved = 0;
        public int foldersRemoved = 0;

        private void countAsAdded(File file) {
            if (file.isDirectory()) {
                logger.info("# Adding folder: " + file);
                foldersAdded++;
            } else {
                logger.info("# Adding file: " + file);
                filesAdded++;
            }
        }

        private void countAsRemoved(File file, boolean isDirectory) {
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

    // ------------------------------------------------------------------------------------------------- Private Methods

    private void syncFile(File file, GridPositioning positioning, SyncStats syncStats) throws Exception {
        String path = file.getPath();
        Topic topic = dms.getTopic("de/deepamehta/core/property/Path", path);
        boolean addToTopicmap;
        if (topic != null) {
            addToTopicmap = !containsTopic(topic.id);
        } else {
            if (file.isDirectory()) {
                topic = filesPlugin.createFolderTopic(path);
            } else {
                topic = filesPlugin.createFileTopic(path);  // throws Exception
            }
            addToTopicmap = true;
        }
        //
        if (addToTopicmap) {
            Point pos = positioning.nextPosition();
            topicmapsPlugin.addTopicToTopicmap(topic.id, pos.x, pos.y, topicmapId);
            syncStats.countAsAdded(file);
        }
    }

    private void syncTopic(Topic topic, List files, SyncStats syncStats) {
        boolean isFile   = topic.typeUri.equals("de/deepamehta/core/topictype/File");
        boolean isFolder = topic.typeUri.equals("de/deepamehta/core/topictype/Folder");
        if (!isFile && !isFolder) {
            return;
        }
        //
        String path = (String) dms.getTopicProperty(topic.id, "de/deepamehta/core/property/Path");
        File file = new File(path);
        if (!files.contains(file)) {
            dms.deleteTopic(topic.id);
            syncStats.countAsRemoved(file, isFolder);
        }
    }

    // ---

    private Topic getFolderTopic() {
        List<RelatedTopic> relTopics = dms.getRelatedTopics(topicmapId,
            asList("de/deepamehta/core/topictype/Folder"), asList("FOLDER_CANVAS;OUTGOING"), null);
        // error check
        if (relTopics.size() == 0) {
            throw new RuntimeException("Topicmap " + topicmapId + " is not related to any Folder topic");
        } else if (relTopics.size() > 1) {
            throw new RuntimeException("Ambiguity: topicmap " + topicmapId + " is related to more than one " +
                "Folder topic");
        }
        //
        return relTopics.get(0).getTopic();
    }
}
