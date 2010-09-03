package de.deepamehta.plugins.foldercanvas.model;

import de.deepamehta.plugins.files.FilesPlugin;
import de.deepamehta.plugins.topicmaps.TopicmapsPlugin;
import de.deepamehta.plugins.topicmaps.model.Topicmap;

import de.deepamehta.core.model.RelatedTopic;
import de.deepamehta.core.model.Relation;
import de.deepamehta.core.model.Topic;
import de.deepamehta.core.service.CoreService;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.File;

import static java.util.Arrays.asList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;



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

    public void synchronize() throws Exception {
        Topic folderTopic = getFolderTopic();
        String syncPath = (String) dms.getTopicProperty(folderTopic.id, "de/deepamehta/core/property/Path");
        SyncStats syncStats = new SyncStats();
        for (File file : new File(syncPath).listFiles()) {
            String path = file.getPath();
            Topic topic = dms.getTopic("de/deepamehta/core/property/Path", path);
            if (topic != null) {
                if (!containsTopic(topic.id)) {
                    topicmapsPlugin.addTopicToTopicmap(topic.id, 0, 0, topicmapId); // FIXME: positioning
                    syncStats.countAsAdded(file);
                }
            } else {
                if (file.isDirectory()) {
                    topic = filesPlugin.createFolderTopic(path);
                } else {
                    topic = filesPlugin.createFileTopic(path);
                }
                topicmapsPlugin.addTopicToTopicmap(topic.id, 0, 0, topicmapId);     // FIXME: positioning
                syncStats.countAsAdded(file);
            }
        }
        // TODO: remove topics from map
        logger.info("### Synchronization of " + syncPath + " with topicmap " + topicmapId + " complete =>\n" +
            syncStats.filesAddedCount + " files added\n" +
            syncStats.foldersAddedCount + " folders added");
    }

    // ------------------------------------------------------------------------------------------------- Private Methods

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

    // --------------------------------------------------------------------------------------------- Private Inner Class

    private class SyncStats {

        int filesAddedCount = 0;
        int foldersAddedCount = 0;

        void countAsAdded(File file) {
            if (file.isDirectory()) {
                foldersAddedCount++;
            } else {
                filesAddedCount++;
            }
        }
    }
}
