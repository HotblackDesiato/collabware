define([
        "taskboard/framework/Controller", 
        "taskboard/commands/ChangeTaskName", 
        "taskboard/commands/DeleteTask", 
        "taskboard/commands/MoveTaskToDone", 
        "taskboard/commands/MoveTaskToToDo",
        "taskboard/commands/MoveTaskToInProgress",
        "taskboard/mixins/Editable", 
        "taskboard/mixins/Movable",
        "taskboard/presenters/Utils",
        "taskboard/Collaboration"], 
        function (Controller, ChangeTaskName, DeleteTask, MoveTaskToDone, MoveTaskToToDo, MoveTaskToInProgress, Editable, Movable, Utils, Collaboration) {
	
	Utils.loadCss("taskboard/presenters/Task.css");
	
	var taskHtml = '<div class="Taskboard_Task"><img class="Taskboard_Task_Image" title="" src="images/anonymous.png"/><div class="Taskboard_Task_Content"></div></div>';
	
	function getParticipantById(id) {
		var theParticipant;
		$.each(collabwareSession.getAllParticipants(), function (_, p) {
			if (p.getId() === id) {
				theParticipant = p;
				return true;
			}
		});
		return theParticipant;
	}
	
	return Controller([Movable, Editable("name")], {
		isEditing: false,
		init: function () {
			this.task = $(taskHtml);
			this.onNameChanged(this.getNode().attr("name"));
			this.onAssignedToUserChanged(this.getNode().attr("assignedToUser"));
			var that = this;
			$(window).resize(function (){
				that.position();
			});
		},
		
		position: function () {
			this.onXChanged(this.getNode().attr("x") || 0);
			this.onYChanged(this.getNode().attr("y") || 0);
			this.onStateChanged(this.getNode().attr("state") || "TODO");
		},
		
		getView: function () {
			return this.task;
		},
		
		getEditableView: function () {
			return $(".Taskboard_Task_Content", this.task);
		},
		
		onNameChanged: function(newName) {
			$(".Taskboard_Task_Content", this.task).text(newName);
		},
		
		onAssignedToUserChanged: function (newAssignedUserId) {
			if (newAssignedUserId) {
				var assignedParticipant = getParticipantById(newAssignedUserId)
				$(".Taskboard_Task_Image", this.task).attr("src", assignedParticipant.getImageUrl());
				$(".Taskboard_Task_Image", this.task).attr("title", assignedParticipant.getDisplayName());
				$(".Taskboard_Task_Image", this.task).css("visibility", "visible");
			} else {
				$(".Taskboard_Task_Image", this.task).css("visibility", "hidden");
			}
		},
		
		onXChanged: function (newX) {
			var userStory = this.getParentController();
			var position = userStory.getAbsoluteCoordinates({left:newX, top:0}, this.task);
			this.task.css("left", position.left);
		},
		
		onYChanged: function (newY) {
			var userStory = this.getParentController();
			var position = userStory.getAbsoluteCoordinates({left:0, top:newY}, this.task);
			this.task.css("top", position.top);
		},
		
		onStateChanged: function (newState) {
			var userStory = this.getParentController();
			if (newState === "TODO") {
				this.task.css("position", "static");
				userStory.moveTaskToToDo(this);
			} else if (newState === "DONE") {
				this.task.css("position", "static");
				userStory.moveTaskToDone(this);
			} else if (newState === "INPROGRESS") {
				this.getView().css("position", "absolute");
				userStory.moveTaskToInProgress(this);
			}
		},
		
		endMoving: function (x,y) {
			if (this.isEditing) return;
			var userStory = this.getParentController();
			var currentState = this.getNode().attr("state");
			var newState = userStory.getStateForPosition({left:x, top:y});
			if (newState === "DELETING" && newState !== currentState) {
				this.execute(new DeleteTask(this.getNode()));
			} else if (newState === "TODO" && newState !== currentState) {
				this.execute(new MoveTaskToToDo(this.getNode()));
			} else if (newState === "DONE" && newState !== currentState) {
				this.execute(new MoveTaskToDone(this.getNode()));
			} else if (newState === "INPROGRESS") {
				var precentageCoordinates = userStory.getRelativeCoordinates(this.getView());
				this.execute(new MoveTaskToInProgress(this.getNode(), precentageCoordinates.left, precentageCoordinates.top));
			} else {
				this.task.css("position", "static");
			}
		},
		
		endEditing: function (newName) {
			this.execute(new ChangeTaskName(this.getNode(), newName));
		}
	});
});
