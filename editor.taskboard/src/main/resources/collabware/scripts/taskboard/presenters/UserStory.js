define(["taskboard/framework/Controller",  "taskboard/commands/ChangeUserStoryName", "taskboard/mixins/Editable", "taskboard/mixins/Movable", "taskboard/presenters/Utils"], 
		function (Controller, ChangeUserStoryName, Editable, Movable, Utils) {
	
	Utils.loadCss("taskboard/presenters/UserStory.css");
	return Controller([Editable("name")],{
		init: function () {
			this.userStoryDomNode = $("<tr class='UserStory'><td>" + this.getNode().attr("name") + "</td><td></td><td></td><td></td></tr>");
			this.userStoryDomNode.css("min-height", "150px");
			this.toDo = this.userStoryDomNode.children().eq(1);
			this.inProgress = this.userStoryDomNode.children().eq(2);
			this.done = this.userStoryDomNode.children().eq(3);
		},
		
		addTask: function (controller) {
			controller.setParentController(this);
			controller.position();
		},
		
		removeTask: function (controller) {
		},
		
		getView: function () {
			return this.userStoryDomNode;
		},
		
		getEditableView : function ()  {
			return this.userStoryDomNode.children().eq(0);
		},
		
		onNameChanged: function (newName) {
			this.userStoryDomNode.children().eq(0).text(newName);		
		},
		
		endEditing: function (newName) {
			this.execute(new ChangeUserStoryName(this.getNode(), newName));
		},
		
		getStateForPosition: function (pos) {
			return this.getParentController().getStateForPosition(pos);
		},
		startMoving: function () {
			this.userStoryDomNode.css("height", this.userStoryDomNode.height());
			this.userStoryDomNode.css("width", this.userStoryDomNode.width());
		},
		moving: function (x,y) {
			this.getParentController().showGhostAt(y, this);
		},
		endMoving: function () {
			this.getView().css("position", "static");
			this.getParentController().moveUserStory(this);
		},
		
		moveTaskToToDo: function (controller) {
			if (!$.contains(this.toDo.get()[0], controller.getView().get()[0])) {
				controller.getView().detach();
				this.toDo.append(controller.getView());
			}
		},
		
		moveTaskToDone: function (controller) {
			if (!$.contains(this.done.get()[0], controller.getView().get()[0])) {
				controller.getView().detach();
				this.done.append(controller.getView());
			}			
		},
		
		moveTaskToInProgress: function (controller) {
			if (!$.contains(this.inProgress.get()[0], controller.getView().get()[0])) {
				controller.getView().detach();
				this.inProgress.append(controller.getView());
			}
		},
		
		getRelativeCoordinates: function (task) {
			var position = task.offset();
			position.left -= 10 /*margin*/;
			position.top -= 10 /*margin*/;
						
			if (position.left < this.inProgress.offset().left) {
				position.left = this.inProgress.offset().left;
			}
	
			if (position.left + task.width() > this.inProgress.offset().left + this.inProgress.width() -20) {
				position.left = this.inProgress.offset().left + this.inProgress.width()-task.width() -20;
			}
			
			if (position.top < this.inProgress.offset().top) {
				position.top = this.inProgress.offset().top;
			}
			
			if (position.top + task.height() > this.inProgress.offset().top + this.inProgress.height() -20) {
				position.top = this.inProgress.offset().top+ this.inProgress.height()-task.height() -20;
			}
			var percentageLeft = ((position.left -this.inProgress.offset().left)/(this.inProgress.width()-task.width() -20)) * 100; 
			var percentageTop = ((position.top-this.inProgress.offset().top)/(this.inProgress.height()-task.height() -20)) * 100;
			return {left:percentageLeft, top:percentageTop};
		},
		
		getAbsoluteCoordinates: function (percentageCoordinates, task) {
			return {
				left:(percentageCoordinates.left/100) * (this.inProgress.width()-task.width() -20)+this.inProgress.offset().left, 
				top:(percentageCoordinates.left/100) * (this.inProgress.height()-task.height() -20)+this.inProgress.offset().top
			};
		}
	});
});