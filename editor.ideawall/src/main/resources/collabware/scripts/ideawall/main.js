var started = false;
require(["ideawall/Factory", "ideawall/commands/InitializeDocument", "ideawall/commands/AddCard", "ideawall/framework/DnDTool"], function (ControllerFactory, InitializeDocument, AddCard, DnDTool) {
	window.startCollaboration = function (collabware) {
		if (started) return;
		started = true;
		
		console.log("starting ideawall");

		var theDocument = collabware.getDocument();
		var factory = new ControllerFactory(theDocument);
		var theBoard = theDocument.getGraph().node("---ROOT_NODE");

		function execute(command) {
			theDocument.applyChange(function (graph) {
				command.apply(graph);
			}, command.getDescription());
		}
		
		function initializeDocument(theDocument) {
			var initDocument = new InitializeDocument();
			execute(initDocument);
		}
		
		function createAddCardHandler (colorClass) {
			return	function addCard (event) {
				execute(new AddCard(theBoard, colorClass, event.pageX, event.pageY));
			}
		}
		
		collabware.addSessionListener({
			joined:function () {
				if (theDocument.getGraph().nodes().length === 1) {
					initializeDocument(theDocument);	
				}
			}
		});
		
		$(function initializeUI() {
			DnDTool($("#YellowNewCard"))
				.ghost($("<div class='NewCard Yellow'>New Card</div>"))
				.onDrop(createAddCardHandler("Yellow"));
			DnDTool($("#RedNewCard"))
				.ghost($("<div class='NewCard Red'>New Card</div>"))
				.onDrop(createAddCardHandler("Red"));
			DnDTool($("#BlueNewCard"))
				.ghost($("<div class='NewCard Blue'>New Card</div>"))
				.onDrop(createAddCardHandler("Blue"));
			DnDTool($("#GreenNewCard"))
			.ghost($("<div class='NewCard Green'>New Card</div>"))
			.onDrop(createAddCardHandler("Green"));
		});
	};
});