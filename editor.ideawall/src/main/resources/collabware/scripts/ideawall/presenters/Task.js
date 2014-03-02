define([
        "ideawall/framework/Controller", 
        "ideawall/commands/ChangeTaskName", 
        "ideawall/commands/DeleteTask", 
        "ideawall/commands/MoveCard",
        "ideawall/mixins/Editable", 
        "ideawall/mixins/Movable",
        "ideawall/presenters/Utils",
        "ideawall/Collaboration"], 
        function (Controller, ChangeTaskName, DeleteTask, MoveCard, Editable, Movable, Utils, Collaboration) {
	
	Utils.loadCss("ideawall/presenters/Task.css");
	
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
			this.onColorChanged(this.getNode().attr("color"));
			this.onAssignedToUserChanged(this.getNode().attr("assignedToUser"));
			var that = this;
			$(window).resize(function (){
				that.position();
			});
			this.position();
		},
		
		getEditableView: function () {
			return $(".Taskboard_Task_Content", this.task);
		},
		
		position: function () {
			this.onXChanged(this.getNode().attr("x") || 0);
			this.onYChanged(this.getNode().attr("y") || 0);
		},
		
		getView: function () {
			return this.task;
		},
		
		onNameChanged: function(newName) {
			$(".Taskboard_Task_Content", this.task).text(newName);
		},
		
		onColorChanged: function(color) {
			this.task.addClass(color);
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
			this.task.css("left", newX);
		},
		
		onYChanged: function (newY) {
			this.task.css("top", newY);
		},
		
		endMoving: function (x,y, mouseX, mouseY) {
			if (this.isEditing) return;
			if (this.isTrash({top:mouseY, left:mouseX})) {
				this.execute(new DeleteTask(this.getNode()));
			} else {
				this.execute(new MoveCard(this.getNode(), x, y));
			}
		},
		
		isTrash: function(position) {
			var trash = $("#DeleteCard");
			return position.left >= trash.offset().left &&
				position.top >= trash.offset().top &&
				position.left <= trash.offset().left + trash.width() &&
				position.top <= trash.offset().top + trash.height();
		},
		
		endEditing: function (newName) {
			this.execute(new ChangeTaskName(this.getNode(), newName));
		}
	});
});
