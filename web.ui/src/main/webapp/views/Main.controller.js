
formatFlexibleDate = function (oValue) {
	var MINUTE = 60*1000;
	var HOUR = 60*MINUTE;
	var DAY = 24*HOUR;
	
	var now = new Date();
	var date = new Date(Date.parse(oValue));
	var elapsed = now.getTime() - date.getTime();
	
	if (elapsed < MINUTE) {
		return "just now";
	} else if (elapsed < HOUR) {
		return Math.round(elapsed / MINUTE) + " minutes ago";
	} else if (elapsed < DAY) {
		return Math.round(elapsed / HOUR) + " hours ago";
	} else if (elapsed < 2*DAY) {
		return date.toLocaleTimeString() + " yesterday";
	} else {
		return date.toLocaleString();
	}
};

formatExactDate = function (oValue) {
	var date = new Date(Date.parse(oValue));
	return date.toLocaleString();
};

sap.ui.controller( "views.Main" ,{

	onInit: function(){
		this.oModel = new sap.ui.model.json.JSONModel();
		var oAddParticipantView = sap.ui.view({viewName:"views.AddParticipant", type:sap.ui.core.mvc.ViewType.XML});
		this.oAddParticipantPopup = oAddParticipantView.byId("AddParticipantPopup");
		oAddParticipantView.attachEvent("addParticipant", this.addParticipant, this);
	},

	 onBeforeRendering: function(){
		 this.refresh();
		 var that = this;
		 setInterval(function() {that.refresh();}, 20*1000);
		 
		 var oTable = this.byId("DocumentTable");
		 oTable.sort(oTable.getColumns()[1]);
		 oTable.setModel(this.oModel);
		 
		 var oCreateDocument = sap.ui.view({viewName:"views.CreateDocument", type:sap.ui.core.mvc.ViewType.XML});
		 var oCreateDocumentButton = this.getView().byId("CreateDocument");
		  oCreateNewDocumentPopup = oCreateDocument.byId("CreateNewDocumentPopup");
		 
		 oCreateDocumentButton.attachPress(function(){
			 oCreateNewDocumentPopup.open();
		 });

		 oCreateNewDocumentPopup.attachClosed(function(){
			 that.refresh();
		 });
		 oCreateNewDocumentPopup.setOpener(oCreateDocumentButton);
		 oCreateNewDocumentPopup.close();		 
	},

	logoff: function () {
		window.location.href = '../j_spring_security_logout';
	},
	
	onAfterRendering: function() {
	},
	
	createDocument: function () {
		
	},
	refresh: function () {
		this.oModel.loadData("/collabware/rest/documents");
	},
	
	showParticipantSelector: function(e){
		this.collaborationId = e.getSource().data()["collaborationId"];
		this.oAddParticipantPopup.setOpener(e.getSource());
		this.oAddParticipantPopup.open(sap.ui.core.Popup.Dock.BeginTop, sap.ui.core.Popup.Dock.EndTop);
	},
	
	addParticipant: function (e) {
		var participantId = e.getParameter("participantId");
		var that = this; 
		$.post("/collabware/rest/documents/"+this.collaborationId+"/participants", {collaborationId: this.collaborationId, userName: participantId}, function () {that.refresh();});
	},
	
	logoff: function () {
		window.location.href = '../j_spring_security_logout';
	},
});