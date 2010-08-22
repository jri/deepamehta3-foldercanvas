function dm3_foldercanvas() {

    // ------------------------------------------------------------------------------------------------ Overriding Hooks

    this.process_files_drop = function(files) {
        if (files.get_directory_count() != 1 || files.get_file_count() != 0) {
            alert("WARNING: dropped items ignored.\n\nTo create a Folder Canvas drop a single folder here.")
            return
        }
        //
        var dir = files.get_directory(0)
        get_plugin("dm3_topicmaps").create_topicmap(dir.name)
        //
        canvas.start_grid_positioning()
        get_plugin("dm3_files").create_file_topics(dir, true)
        canvas.stop_grid_positioning()
    }

    // ------------------------------------------------------------------------------------------------- Private Methods
}
