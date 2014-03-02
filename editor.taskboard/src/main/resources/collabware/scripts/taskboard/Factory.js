define(["taskboard/framework/ControllerFactory",
        "taskboard/presenters/UserStory",
        "taskboard/presenters/Task",
        "taskboard/presenters/TaskBoard"], function (ControllerFactory, UserStory, Task, TaskBoard) {			
	return 	function setupFactory(theDocument) {
			var factory = new ControllerFactory(theDocument);
			factory.create(TaskBoard).forPath("/");
			factory.create(UserStory).forPath("/userStory");
			factory.create(Task).forPath("/userStory/task");
			return factory;
		}
});