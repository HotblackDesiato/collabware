(function () {
	var started = false;

	window.startCollaboration = function (collabware) {
		if (started) return;
		started = true;
		
		console.log("starting example editor");

		var theDocument = collabware.getDocument();
		
		theDocument.addDocumentChangeListener({
			nodeAdded: function (nodeId) {
				// update UI
			},
			nodeRemoved: function (nodeId) {
				// update UI
			},
			referenceAdded: function(nodeId, referenceName, index, targetId) {
				// update UI

				// When events are fired the graph has already been updated.
				// That's why we can access the text property here.
				if (referenceName == "textNodes") {
					var text = theDocument.getGraph().node(targetId).attr("text");
					var li = $("<li id='"+targetId+"'></li>").text(text);
					if ($("#theList").children().length == index) {
						$("#theList").append(li);
					} else {
						$( "#theList > *:eq("+index+")" ).insertBefore(li);
					}
				}
			},
			referenceRemoved: function(nodeId, referenceName, index, targetId) {
				// update UI
				// We also define what should happen if a reference is removed
				// even though it is not possible to remove entries via the UI.
				// However, it is required in order for the replay feature to work.
				if (referenceName == "textNodes") {
					$( "#theList > *:eq("+index+")" ).remove();
				}
			},
			unaryReferenceSet: function(nodeId, referenceName, newTargetId) {
				// update UI
				// newTargetId my be undefined if the reference was unset
			},
			attributeSet: function(nodeId, attributeName, value) {
				// update UI
				if (attributeName=="text") {
					$("#"+nodeId).text(value);
				}
			},
			complexChangeEnded: function(description) {
				// done doing a complex change (i.e. sequence of the above changes)
			}
		});
		
		collabware.addSessionListener({
			joined:function () {
				if (theDocument.isEmpty()) {
					// initialize the document
					theDocument.applyChange( function (graph) {
						// see collabware.web.client.wrappers.GraphWrapper for documentation
						graph.addNode("TheList");
					}, "Initialize the document.");
				}
			}
		});
		
		$(function initializeUI() {
			$("#addText").click(function() {
				var inputText = $("#theText").val();
				// Do not manipulate the UI directly. 
				// Make changes to the document and update UI through the
				// above event listener. This allows you treat local changes 
				// and remote changes the same way w/o writing additional code.
				theDocument.applyChange(function(graph) {
					// create a new node and set text attribute
					var newNode = graph.addNode().attr("text", inputText);
					// add the new node to the root node.
					graph.node("TheList").orderedRef("textNodes", newNode);
				}, "Add some text");
				
			})
		});
	};
})();