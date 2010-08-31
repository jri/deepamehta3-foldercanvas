package de.deepamehta.plugins.foldercanvas.model;

import de.deepamehta.plugins.topicmaps.model.Topicmap;

import de.deepamehta.core.model.RelatedTopic;
import de.deepamehta.core.model.Relation;
import de.deepamehta.core.model.Topic;
import de.deepamehta.core.service.DeepaMehtaService;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import static java.util.Arrays.asList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;



public class FolderCanvas extends Topicmap {

    // ---------------------------------------------------------------------------------------------------- Constructors

    public FolderCanvas(long topicmapId, DeepaMehtaService dms) {
        super(topicmapId, dms);
    }

    // -------------------------------------------------------------------------------------------------- Public Methods

    public void synchronize() {
        Topic folderTopic = getFolderTopic();
        String path = (String) folderTopic.getProperty("de/deepamehta/core/property/Path");
        // String path = (String) dms.getTopicProperty(fileTopicId, "de/deepamehta/core/property/Path");
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
}
