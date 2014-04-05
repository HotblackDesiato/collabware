Collabware
==========
A componentized, extensible realtime collaboration-framework.

Collabware was designed to be extensible. It can be extended in the following ways:
* Document types: Collabware can be extended with additional document types. It currently only provides a graph-based document, but tree-based or string based documents can be easily added.
* Editors: Any number of editors can be added, currently collabware provides examples editors for a SCRUM task board and an idea wall, which both use the graph-based document. You could for instance add an text editor, a UML editor, etc

Collabware consists of a number of Eclipse projects/OSGi bundles which fall into 3 tiers: Core, Shell and Applications.

Core:
* core.api: Provides Interfaces and classes shared between other parts of collabware. As such it provides abstractions for Documents, Operations a Transformation

* core.model: Implements a graph-based Document (provided by collabware.api) and the corresponding Operations and Transformation.

* core.transfomer: Implements a context-base transformation algorithm independent of the actual operations provided by collabware.model (or any other implementation of Document)

* core.collaboration: Provides collaboration clients and servers and manages collaboration life cycles.

* core.userManagement: a rudimentary implementation of a user management.


Web Shell:
* web.ui: Exposes collaborations via REST services and CometD to web browsers.

* web.registy: Registry for editors. Editors must register for a given content type


Editors:
* editor.ideawall: An editor that allows you to create sticky notes and group them on a virtual wall.

* editor.taskboard: An editor that provides a virtual scrum task board.

Building Collabware
===================
Collabware uses Maven as its build system. To build collabware cd into 
	parent/
and do a 
	mvn clean install


Deployment
==========
collabware runs in an OSGI container. I’ve used virgo-tomcat-server-3.5.0.RELEASE you can download it here: https://www.eclipse.org/virgo/download/. 

To run collabware, copy collabware.par/target/collabware.app-0.0.1-SNAPSHOT.par into Virgo’s pickup folder and start Virgo.

If you are using Eclipse as a development environment you can also use the Virgo server connector. Therefore, follow the instructions here: http://wiki.eclipse.org/Virgo/Tooling

Once Virgo has started, point your browser to 
	http://localhost:8080/collabware/

You can log in with the following users:
	james.bond
	jason.bourne
	justin.case
	justin.time
	doris.open
All have the same password: 1234

ToDos
=====
* User registration. So far they are hard coded.
* Implement persistance
* Security in collabware.web
* Add more document types

Known Issues
============
* Connecting to the first collaboration document fails after a server start. You need to refresh you browser.

How To Add Your Own Editor
==========================
You can use editor.example/ as a starting point. 
It is worthwhile to check out the following files:
* editor.example/src/main/java/collabware/example/ExampleEditor.java
  It registers the editor with collabware and makes it immediately available.
* editor.example/src/main/resources/collabware/index.html
  Is the editor’s UI and links in any required resources.
* editor.example/src/main/resources/collabware/scripts/main.js
  Contains the editor’s logic. 
* See web.ui/src/main/java/collabware/web/client/js/JS*.java for more documentation about the objects and functions which get exposed to JavaScript.
