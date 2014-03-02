define(["taskboard/framework/Controller", "taskboard/presenters/Utils"], function (Controller, Utils) {
	
	Utils.loadCss("taskboard/presenters/TaskBoard.css");
	
	return Controller({
		init: function () {
			this.domRef = $("#TaskBoard");
			this.trash = $("#DeleteTask");
			this.newUserStory = $("#NewUserStory")
			this.userStories = [];
			this.ghost = $("<tr class='UserStory' style='height:30px;'><td></td><td></td><td></td><td></td></tr>");
		},
		
		addUserStory: function (controller) {
			this.userStories.push(controller);
			controller.setParentController(this);
			this.newUserStory.before(controller.getView());
		},
		
		removeUserStory: function (controller) {
			
		},
		
		getUserStoryForPosition: function (position) {
			var theUserStory = undefined;
			$.each(this.userStories, function (_, controller) {
				var userStory = controller.getView();
				if (userStory.offset().top <= position.top && userStory.offset().top + userStory.height() >= position.top) {
					theUserStory = controller;
				} 
			});
			if (this.newUserStory.offset().top <= position.top && this.newUserStory.offset().top + this.newUserStory.height() >= position.top) {
				this.execute({
					apply: function (graph) {
						theUserStory = graph.addNode();
						theUserStory.attr("name", "New User Story");
						graph.node("---ROOT_NODE").orderedRef("userStory", theUserStory);
					},
					
					getDescription: function () {
						return "adding new user story";
					}
				})
				return this.getControllerForNode(theUserStory);
			} else {
				return theUserStory;			
			}
		},
		
		getDomRef: function() {
			return $("#TaskBoard");
		},
		
		getStateForPosition : function (position) {
			function isInProgress(offset) {
				return offset.left > $("#InProgress").offset().left;
			}
	
			function isDone(offset) {
				return offset.left > $("#Done").offset().left;
			}
	
			function isToDo(offset) {
				return offset.left > $("#ToDo").offset().left;
			}
			
			function isTrash(position) {
				var trash = $("#DeleteTask");
				return position.left >= trash.offset().left &&
					position.top >= trash.offset().top &&
					position.left <= trash.offset().left + trash.width() &&
					position.top <= trash.offset().top + trash.height();
			}
			
			if (isTrash(position))
				return "DELETING";
			if (isDone(position))
				return "DONE";
			if (isInProgress(position))
				return "INPROGRESS";
			if (isToDo(position))
				return "TODO";
		},
		
		showGhostAt: function(y, movingUserStory) {
			var ghost = this.ghost;
			var userStories = this.userStories;
			var beforeUserStory = this.beforeUserStory;
			$.each(userStories, function (i, controller) {
				var userStory = controller.getView();
				var previousUserStory;
				if (i === 0) {
					if (userStory.offset().top + userStory.height()/2 > y && userStory !== movingUserStory && beforeUserStory !== userStory) {
						console.log("show ghost before " + userStory.text());
						beforeUserStory = userStory;
						ghost.detach();
						userStory.before(ghost);
					}
				} 
				else {
					previousUserStory = userStories[i-1].getView();
					if (userStory.offset().top + userStory.height()/2 > y && previousUserStory.offset().top + previousUserStory.height()/2 < y && userStory !== movingUserStory && beforeUserStory !== userStory) {
						console.log("show ghost before " + userStory.text());											
						ghost.detach();
						beforeUserStory = userStory;
						userStory.before(ghost);
					} else if (i === userStories.length && userStory.offset().top + userStory.height()/2 < y && userStory !== movingUserStory && beforeUserStory !== userStory) {
						beforeUserStory = userStory;
						ghost.detach();
						userStory.after(ghost);
					}
				} 
			});
			this.beforeUserStory = beforeUserStory;
		},
		moveUserStory: function (userStory) {
			userStory.getView().detach();
			this.ghost.after(userStory.getView());
			this.ghost.detach();
		}
	});
});