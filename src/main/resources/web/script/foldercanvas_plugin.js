function foldercanvas_plugin() {

    var LOG_FOLDERCANVAS = false

    // ------------------------------------------------------------------------------------------------ Overriding Hooks

    this.process_files_drop = function(files) {
        if (files.get_directory_count() != 1 || files.get_file_count() != 0) {
            alert("WARNING: dropped items ignored.\n\nTo create a Folder Canvas drop a single folder here.")
            return
        }
        //
        var dir = files.get_directory(0)
        if (LOG_FOLDERCANVAS) dm3c.log("Directory dropped: " + dir.path)
        //
        var folder_topic = get_folder_topic_for_path(dir.path)
        if (folder_topic) {
            if (LOG_FOLDERCANVAS) dm3c.log("..... Folder topic exists already (ID " + folder_topic.id + ")")
            var topicmap_topic = get_topicmap_topic(folder_topic.id)
            if (!topicmap_topic) {
                create_folder_canvas(dir, folder_topic.id)
            } else {
                if (LOG_FOLDERCANVAS) dm3c.log("..... Topicmap exists already (ID " + topicmap_topic.id + ")" +
                    " - Selecting")
                dm3c.get_plugin("topicmaps_plugin").select_topicmap(topicmap_topic.id)
            }
        } else {
            folder_topic = create_folder_topic(dir)
            if (LOG_FOLDERCANVAS) dm3c.log("..... Folder topic created (ID " + folder_topic.id + ")")
            create_folder_canvas(dir, folder_topic.id)
        }
    }

    this.add_canvas_commands = function(cx, cy) {
        var commands = []
        var topicmap_id = dm3c.get_plugin("topicmaps_plugin").get_topicmap_id()
        // provide sync-command only for folder canvases (not for regular topicmaps)
        if (get_folder_topic(topicmap_id)) {
            commands.push({
                label: "Synchronize with folder", handler: do_synchronize, context: "context-menu"
            })
        }
        return commands

        function do_synchronize() {
            var result = dm3c.restc.execute_command("deepamehta3-foldercanvas.synchronize", {topicmap_id: topicmap_id})
            dm3c.get_plugin("topicmaps_plugin").refresh_topicmap(topicmap_id)
            alert("Synchronization complete!\n\n" + JSON.stringify(result))
        }
    }

    // ----------------------------------------------------------------------------------------------- Private Functions

    /**
     * Returns the folder topic for a given path, or null.
     */
    function get_folder_topic_for_path(path) {
        return dm3c.restc.get_topic_by_property("de/deepamehta/core/property/Path", path)
    }

    /**
     * Returns the topicmap topic for a given folder, or undefined.
     */
    function get_topicmap_topic(folder_topic_id) {
        var topicmap_topics = dm3c.restc.get_related_topics(folder_topic_id, ["de/deepamehta/core/topictype/Topicmap"],
                                                                             ["FOLDER_CANVAS;INCOMING"])
        if (topicmap_topics.length > 1) {
            alert("WARNING (foldercanvas_plugin.get_topicmap_topic):\n\n" +
                "More than one topicmaps for folder " + folder_topic_id)
        }
        return topicmap_topics[0]
    }

    /**
     * Returns the folder topic for a given topicmap, or undefined.
     */
    function get_folder_topic(topicmap_id) {
        var folder_topics = dm3c.restc.get_related_topics(topicmap_id, ["de/deepamehta/core/topictype/Folder"],
                                                                       ["FOLDER_CANVAS;OUTGOING"])
        if (folder_topics.length > 1) {
            alert("WARNING (foldercanvas_plugin.get_folder_topic):\n\n" +
                "More than one folder topics for topicmap " + topicmap_id)
        }
        return folder_topics[0]
    }

    // ---

    /**
     * Creates a folder topic for a given directory.
     */
    function create_folder_topic(dir) {
        return dm3c.create_topic("de/deepamehta/core/topictype/Folder", {
            "de/deepamehta/core/property/FolderName": js.filename(dir.path),
            "de/deepamehta/core/property/Path": dir.path
        })
    }

    /**
     * Creates a "folder canvas" (a topicmap) and fills it with the items (file and folder topics) contained
     * in the given directory. Relates the folder canvas to the underlying folder (a folder topic).
     */
    function create_folder_canvas(dir, folder_topic_id) {
        // create topicmap
        var topicmap_topic = dm3c.get_plugin("topicmaps_plugin").create_topicmap(dir.name)
        if (LOG_FOLDERCANVAS) dm3c.log("..... Topicmap created (ID " + topicmap_topic.id + ")")
        // relate to folder
        dm3c.create_relation("FOLDER_CANVAS", folder_topic_id, topicmap_topic.id)
        // fill topicmap
        dm3c.canvas.start_grid_positioning()
        dm3c.get_plugin("dm3_files").create_file_topics(dir, true)
        dm3c.canvas.stop_grid_positioning()
    }
}
