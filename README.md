
DeepaMehta 3 Folder Canvas
==========================

This plugin establishes a special mode of file handling in DeepaMehta 3: a special type of topic map (a *Folder Canvas*) is bound to an OS folder and can synchronize its content (that is *File* topics) with it. The DeepaMehta 3 Folder Canvas plugin builds on services provided by the [DeepaMehta 3 Files](http://github.com/jri/deepamehta3-files) plugin.

DeepaMehta 3 is a platform for collaboration and knowledge management.  
<http://github.com/jri/deepamehta3>


Installing
----------

The DeepaMehta 3 Folder Canvas plugin is an optional installation. When you install it you must deactivate the DeepaMehta 3 File Manager plugin (which is part of the DeepaMehta 3 standard installation).

Perform the installation via the Apache Felix shell (the terminal window that opens while starting DeepaMehta):

1. Deactivate the DeepaMehta 3 File Manager plugin:

        stop 38

   Note: here, 38 is supposed to be the bundle ID of the DeepaMehta 3 File Manager plugin.
   Use the `lb` command (list bundles) to get out the actual bundle ID on your DeepaMehta installation.

2. Download and start the DeepaMehta 3 Folder Canvas plugin:

        start http://www.deepamehta.de/maven2/de/deepamehta/deepamehta3-foldercanvas/0.4.3/deepamehta3-foldercanvas-0.4.3.jar

   When using the `lb` command again you should now see the DeepaMehta 3 Folder Canvas plugin as *Active* and the DeepaMehta 3 File Manager plugin as *Resolved*.

3. You're done. Open the DeepaMehta browser window (resp. press reload):  
   <http://localhost:8080/de.deepamehta.3-client/index.html>

Note: the DeepaMehta 3 File Manager plugin and the DeepaMehta 3 Folder Canvas plugins must not be activated at the same time. The behavoir would be unexpected.


Version History
---------------

**v0.4.3** -- Jan 3, 2011

* Compatible with DeepaMehta 3 v0.4.4

**v0.4.2** -- Nov 25, 2010

* Compatible with DeepaMehta 3 v0.4.3

**v0.4.1** -- Oct 16, 2010

* Compatible with DeepaMehta 3 v0.4.1


------------
Jörg Richter  
Jan 3, 2011
