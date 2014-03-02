define(["taskboard/framework/AbstractCommand"], function (Command) {
	return Command({
		apply: function execute(graph) {
			var root = graph.node("---ROOT_NODE");
			var userStory1 = graph.addNode();
			root.orderedRef("userStory", userStory1);
			userStory1.attr("name", "A user story");
			var task1 = graph.addNode();
			userStory1.orderedRef("task", task1);
			task1.attr("name", "New task");
			task1.attr("state", "TODO");
		}, 
		
		description: "Initializing the task board."
	});
});