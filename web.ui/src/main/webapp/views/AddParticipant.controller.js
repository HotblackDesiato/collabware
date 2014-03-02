sap.ui.controller( "views.AddParticipant" ,{

	onInit: function(){
		this.oCollaborators = this.byId("Collaborators");
		this.oAddParticipant = this.byId("AddParticipant");
		this.oPopup = this.byId("AddParticipantPopup");
		var oContacts = new sap.ui.model.json.JSONModel();
		oContacts.loadData("/collabware/rest/contacts");
		this.oCollaborators.setModel(oContacts);
		
	},

	onBeforeRendering: function(){
	},

	onAfterRendering: function() {
	},
	
	filter: function(oEvent){
		var searchTerm = oEvent.getParameter("value");
		if (searchTerm == "") {
			this.oCollaborators.getBinding("items").filter([], sap.ui.model.FilterType.Application);
		} else {
			var oFilter = new sap.ui.model.Filter("displayName", sap.ui.model.FilterOperator.Contains, searchTerm);
			this.oCollaborators.getBinding("items").filter([oFilter], sap.ui.model.FilterType.Application);
		}
		this.oCollaborators.clearSelection();
		this.oCollaborators.rerender();
	},
	
	select: function(oEvent) {
		if (oEvent.getParameters().selectedIndices.length > 0) {
			this.oAddParticipant.setEnabled(true);
		} else {
			this.oAddParticipant.setEnabled(false);
		}
		this.oAddParticipant.rerender();
	},
	
	close: function () {
		this.oPopup.close();
	},
	
	addParticipant: function () {
		var participantId = this.oCollaborators.getSelectedKeys()[0];
		this.getView().fireEvent("addParticipant", {participantId: participantId});
		//collabwareSession.addParticipant();
	}
});