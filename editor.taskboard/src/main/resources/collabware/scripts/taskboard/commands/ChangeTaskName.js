define(["taskboard/framework/AbstractCommand"], function (Command) {
return Command({
	init: function (task, newName) {
		this.task = task;
		this.newName = newName;
	},
	
	apply: function execute(graph) {
		this.task.attr("name", this.newName);
	}, 
	
	description: function () {
		return "Renaming Task '" + this.task.attr("name") + "' to '" + this.newName + "'";
	}
});
});