define(["taskboard/framework/AbstractCommand"], function (Command) {
	return Command({
		init: function (task) {
			this.task = task;
		},
		apply:function apply(graph) {
			graph.remove(this.task);
		}, 
		description: function () {
			return "Task '" + this.task.attr("name") + "' removed from task board.";
		}
	});
});