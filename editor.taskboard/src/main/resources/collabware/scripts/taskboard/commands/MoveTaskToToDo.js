define(["taskboard/framework/AbstractCommand"], function (Command) {
	return Command({
		init: function (task) {
			this.task = task;
		},
		apply:function apply(graph) {
			this.task.
				attr("state", "TODO").
				attr("assignedToUser", undefined);
		}, 
		description: function () {
			return "Task '" + this.task.attr("name") + "' moved to TODO." 
		}
	});
});