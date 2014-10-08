emf2web
=======

Lightweight Web Renderer for EMF Forms

Includes a configured Play Application and Java API to communicate with the Server


First Time Setup
----------------

### Setup Eclipse

1. Download Eclipse Modeling Edition
2. Install Xtend (https://www.eclipse.org/xtend/download.html)
3. Import Projects from Github into Workspace
4. (Optional: Execute "New > Example... > EMF Forms > Make it happen: example model" to resolve remaining errors in api.test)
5. Run Eclipse Application only with emf2web, emf2web.api and emf2web.examples plugins

### Setup Play Application

1. Install sbt (http://www.scala-sbt.org/download.html)
2. Install and run mongoDB (http://www.mongodb.org/downloads)
3. Make sure "javac" is found on commandline or JAVA_HOME is set
3. In the running Eclipse Application execute "New > Example... > EMF Forms > Create Web Application and Java API Testcase"
4. (Optional: Execute "New > Example... > EMF Forms > Make it happen: example model" to resolve remaining errors in api.test)
5. (Optional: The Play Application can be copied to any directory, it does not have to reside in the workspace)
6. Open command shell in the newly generated playapplication directory
7. Execute "sbt". Wait until sbt shell opens
8. Execute "run"
9. Open URL "http://localhost:9000/#/user"


Typical Usage
-------------

1. Start Play Application
2. Create your data and view models in Eclipse
3. Open contextual menu (right-click) of your data model (.ecore file)
4. Execute "EMF Forms > Convert to Web"
5. Open URL "http://localhost:9000/#/eclassname" where "eclassname" is one of the exported eClasses. 

Deploy as Dropins
-----------------

To deploy the plugins as dropins simply export emf2web, emf2web.api and emf2web.examples and put them in the dropin folder.

Make sure the following criteria are met:

* The Dropin-Eclipse is based on the Modeling Edition
* Xtend is installed
* The emf2web.examples plugin must be deployed as a directory, not as .jar (simply extract the exported .jar)

