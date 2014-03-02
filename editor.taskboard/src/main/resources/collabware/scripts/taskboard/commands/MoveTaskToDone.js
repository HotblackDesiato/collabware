define(["taskboard/framework/AbstractCommand"], function (Command) {
	return Command({
		init: function (task) {
			this.task = task;
		},
		apply:function apply(graph) {
			this.task.attr("state", "DONE");
			this.task.attr("assignedToUser", collabwareSession.getLocalParticipant().getId());
		}, 
		description: function () {
			return "Task '" + this.task.attr("name") + "' moved to DONE." 
		}
	});
});