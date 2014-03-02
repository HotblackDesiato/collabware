define(["taskboard/framework/AbstractCommand"], function (Command) {
	return Command({
		init: function (userStory) {
			this.userStory = userStory;
		},
		
		apply: function execute(graph) {
			var task = graph.addNode();
			task.attr("name", "New Task");
			task.attr("state", "TODO");
			this.userStory.orderedRef("task", task);
		}, 
		
		description: function () {
			return "Adding new task to user story '"+this.userStory.attr("name")+"'.";
		}
	});
});