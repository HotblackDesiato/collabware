define(["ideawall/framework/AbstractCommand"], function (Command) {
	return Command({
		init: function (task, x, y) {
			this.task = task;
			this.x = x;
			this.y = y;
		},
		apply:function apply(graph) {
			this.task.
				attr("x", this.x).
				attr("y", this.y).
				attr("assignedToUser", collabwareSession.getLocalParticipant().getId());
		}, 
		description: function () {
			return "Card '" + this.task.attr("name") + "' moved." 
		}
	});
});