var started = false;
require(["taskboard/Factory", "taskboard/commands/InitializeDocument", "taskboard/commands/AddTask", "taskboard/framework/DnDTool"], function (ControllerFactory, InitializeDocument, AddTask, DnDTool) {
	window.startCollaboration = function (collabware) {
		if (started) return;
		started = true;
		
		console.log("starting taskboard");

		var theDocument = collabware.getDocument();
		var factory = new ControllerFactory(theDocument);
		var theTaskBoard = factory.getControllerForNode("---ROOT_NODE");

		function execute(command) {
			theDocument.applyChange(function (graph) {
				command.apply(graph);
			}, command.getDescription());
		}
		
		function initializeDocument(theDocument) {
			var initDocument = new InitializeDocument();
			execute(initDocument);
		}
		
		function addTask (event) {
			var userStory = theTaskBoard.getUserStoryForPosition({left:event.pageX, top:event.pageY});
			if (userStory){
				execute(new AddTask(userStory.getNode()));
			}
		}
		
		collabware.addSessionListener({
			joined:function () {
				if (theDocument.getGraph().nodes().length === 1) {
					initializeDocument(theDocument);	
				}
				$(window).resize();
			}
		});
		
		$(function initializeUI() {
			DnDTool($(".NewTask"))
				.ghost($("<div class='NewTask'>New Task</div>"))
				.onDrop(addTask);
		});
	};
});