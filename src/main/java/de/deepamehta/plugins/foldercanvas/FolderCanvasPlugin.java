package de.deepamehta.plugins.foldercanvas;

import de.deepamehta.plugins.foldercanvas.model.FolderCanvas;
import de.deepamehta.plugins.foldercanvas.model.SyncStats;
import de.deepamehta.plugins.files.service.FilesService;
import de.deepamehta.plugins.topicmaps.service.TopicmapsService;

import de.deepamehta.core.model.ClientContext;
import de.deepamehta.core.service.Plugin;
import de.deepamehta.core.service.PluginService;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.Map;
import java.util.logging.Logger;



public class FolderCanvasPlugin extends Plugin {

    // ---------------------------------------------------------------------------------------------- Instance Variables

    private TopicmapsService topicmapsService;
    private FilesService filesService;

    private Logger logger = Logger.getLogger(getClass().getName());

    // -------------------------------------------------------------------------------------------------- Public Methods



    // **************************************************
    // *** Core Hooks (called from DeepaMehta 3 Core) ***
    // **************************************************



    @Override
    public JSONObject executeCommandHook(String command, Map params, ClientContext clientContext) {
        if (command.equals("deepamehta3-foldercanvas.synchronize")) {
            long topicmapId = -1;
            try {
                topicmapId = (Integer) params.get("topicmap_id");
                FolderCanvas folderCanvas = new FolderCanvas(topicmapId, dms, topicmapsService, filesService);
                SyncStats stats = folderCanvas.synchronize();
                //
                JSONObject result = new JSONObject();
                result.put("status", "success");
                result.put("files_added", stats.filesAdded);
                result.put("folders_added", stats.foldersAdded);
                result.put("files_removed", stats.filesRemoved);
                result.put("folders_removed", stats.foldersRemoved);
                return result;
            } catch (Throwable e) {
                throw new RuntimeException("Error while synchronizing folder canvas " + topicmapId, e);
            }
        }
        return null;
    }

    // ---

    @Override
    public void serviceArrived(PluginService service) {
        if (service instanceof TopicmapsService) {
            topicmapsService = (TopicmapsService) service;
        } else if (service instanceof FilesService) {
            filesService = (FilesService) service;
        }
    }

    @Override
    public void serviceGone(PluginService service) {
        if (service == topicmapsService) {
            topicmapsService = null;
        } else if (service == filesService) {
            filesService = null;
        }
    }
}
