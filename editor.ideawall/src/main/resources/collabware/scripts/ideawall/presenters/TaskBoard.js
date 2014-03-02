define(["ideawall/framework/Controller", "ideawall/presenters/Utils"], function (Controller, Utils) {
	
	Utils.loadCss("ideawall/presenters/TaskBoard.css");
	
	return Controller({
		init: function () {
			this.domRef = $("#Board");
			this.trash = $("#DeleteCard");
		},
				
		getDomRef: function() {
			return this.domRef;
		},
		
		addCard: function (controller) {
			this.domRef.append(controller.getView());
		}
	});
});