sap.ui.controller( "views.CreateDocument" ,{

	onInit: function(){
		var oEditors = new sap.ui.model.json.JSONModel();
		oEditors.loadData("/collabware/rest/editors");
		this.oTypeList = this.byId("DocumentTypes");
		this.oName = this.byId("DocumentName");
		this.oTypeList.setModel(oEditors);

//		var oContacts = new sap.ui.model.json.JSONModel();
//		oContacts.loadData("/collabware/rest/contacts");
//		this.oCollaborators = this.byId("Collaborators");
//		this.oCollaborators.setModel(oContacts);
	},

	onBeforeRendering: function(){
	},

	onAfterRendering: function() {
	},
	
	create: function () {
		var that = this;
		$.ajax({
			  type: "POST",
			  url: "/collabware/rest/documents",
			  data: {type:this.oTypeList.getSelectedKeys()[0],name:this.oName.getValue()},
			  success: function(data) {
				  that.byId("CreateNewDocumentPopup").close();
			  },
			  dataType: "json"
			});
	},
	cancel: function () {
		this.byId("CreateNewDocumentPopup").close();
	},
	
	open: function(){
		this.oName.setValue("");
		this.oTypeList.setSelectedIndex(0);
	}
});