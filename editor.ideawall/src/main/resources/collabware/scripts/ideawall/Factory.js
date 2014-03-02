define(["ideawall/framework/ControllerFactory",
        "ideawall/presenters/Task",
        "ideawall/presenters/TaskBoard"], function (ControllerFactory, Task, TaskBoard) {			
	return 	function setupFactory(theDocument) {
			var factory = new ControllerFactory(theDocument);
			factory.create(TaskBoard).forPath("/");
			factory.create(Task).forPath("/card");
			return factory;
		}
});