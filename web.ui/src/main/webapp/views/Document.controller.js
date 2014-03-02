sap.ui.controller( "views.Document" ,{

	onInit: function(){
		 var oReplayView = sap.ui.view({viewName:"views.Replay", type:sap.ui.core.mvc.ViewType.XML});
		 var oStartReplayButton = this.byId("StartReplay");
		 this.oReplayPopup = oReplayView.byId("ReplayPopup");
		 this.oReplayPopup.setOpener(oStartReplayButton);

		 var oAddParticipantView = sap.ui.view({viewName:"views.AddParticipant", type:sap.ui.core.mvc.ViewType.XML});
		 oAddParticipantView.attachEvent("addParticipant", this.addParticipant, this);
		 var oAddParticipantButton = this.byId("AddParticipant");
		 this.oAddParticipantPopup = oAddParticipantView.byId("AddParticipantPopup");
		 this.oAddParticipantPopup.setOpener(oAddParticipantButton);
		 
	},

	onBeforeRendering: function(){
	},

	onAfterRendering: function() {
		$(".DocumentToolBar").removeClass("sapUiTb");
	},
	
	startReplay: function() {
		this.oReplayPopup.open(sap.ui.core.Popup.Dock.RightTop, sap.ui.core.Popup.Dock.CenterBottom);
	},
	
	showParticipantSelector: function() {
		this.oAddParticipantPopup.open(sap.ui.core.Popup.Dock.RightTop, sap.ui.core.Popup.Dock.CenterBottom);
	},
	addParticipant: function (e) {
		var participantId = e.getParameter("participantId");
		collabwareSession.addParticipant(participantId);
	}
});