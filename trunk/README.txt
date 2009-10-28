Description
-----------
An extension for the Alfresco open source CMS that allows content contributors
to manually baseline version numbers.

This is particularly useful if the latest version of a document is being
ingested from elsewhere and the current version number has to be preserved in
Alfresco but the full version history does not.

Please don't hesitate to contact the author if you'd like to contribute!


Author
------
Peter Monks (pmonks@alfresco.com)


Pre-requisites
--------------
Alfresco 3 or better (tested on Alfresco Enterprise 3.1SP1)


Installation / Configuration
----------------------------
1. Build the AMP file using Maven2
2. Copy the resulting AMP file into the ${ALFRESCO_HOME}/amps directory
3. Run the ${ALFRESCO_HOME}/apply_amps[.sh|.bat] script to install the AMP
   into your Alfresco instance


Using the Functionality
-----------------------
This module includes a custom content type called "Version Baselined Content"
that must be used in order to control the baselined version number.  This type
includes a single mandatory property "Base Version Number" (a decimal value)
that controls the initial version number of documents of this type.

Note that this implies that versioning must be enabled *after* the content has
the correct type applied and the "Base Version Number" property populated -
specialising the type and/or modifying the property value after versioning is
already enabled will have no effect whatsoever on the version labels.



TODOS
-----
* Test with custom sub-types of vb:VersionBaselinedContent, to ensure that
  they also get picked up by this functionality.

* Consider introducing label ids and message bundles for the property sheet.

* Review the fix for https://issues.alfresco.com/jira/browse/ETHREEOH-3183
  when it becomes available.
