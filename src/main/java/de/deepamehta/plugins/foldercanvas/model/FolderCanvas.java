package de.deepamehta.plugins.foldercanvas.model;

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

    // ---------------------------------------------------------------------------------------------------- Constructors

    public FolderCanvas(long topicmapId, CoreService dms) {
        super(topicmapId, dms);
    }

    // -------------------------------------------------------------------------------------------------- Public Methods

    public void synchronize() {
        Topic folderTopic = getFolderTopic();
        String path = (String) dms.getTopicProperty(folderTopic.id, "de/deepamehta/core/property/Path");
        for (File file : new File(path).listFiles()) {
            file.getPath();
        }
    }

    // ------------------------------------------------------------------------------------------------- Private Methods

    private Topic getFolderTopic() {
        List<RelatedTopic> relTopics = dms.getRelatedTopics(topicmapId,
            asList("de/deepamehta/core/topictype/Folder"), asList("FOLDER_CANVAS;OUTGOING"), null);
        // error check
        if (relTopics.size() == 0) {
            throw new RuntimeException("Topic Map " + topicmapId +
                " is not related to any Folder topic");
        } else if (relTopics.size() > 1) {
            throw new RuntimeException("Ambiguity: Topic Map " + topicmapId +
                " is related to more than one Folder topic");
        }
        //
        return relTopics.get(0).getTopic();
    }

    private boolean isContainedInTopicmap(File file) {
        return false;
    }
}
