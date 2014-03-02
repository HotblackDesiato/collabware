<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>Collabware</title>

<script id="sap-ui-bootstrap"
        src="https://sapui5.hana.ondemand.com/resources/sap-ui-core.js"
        data-sap-ui-theme="sap_bluecrystal"
        data-sap-ui-libs="sap.ui.commons">
        
        
        </script>
        <script type="text/javascript" src="/collabware/js/lib/cometd.js"></script>
<script type="text/javascript" src="/collabware/jsclient/jsclient.nocache.js"></script>
<script type="text/javascript" src="/collabware/js/lib/json2.js"></script>
<script type="text/javascript" src="/collabware/js/lib/jquery.cometd.js"></script>
        
	<style type="text/css">
	html {
		height:100%;
		width:100%;
	}
	body {
		height:100%;
		width:100%;
	}
	</style>
</head>

<script type="text/javascript">
</script>
<body class="sapUiBody" id="uiArea" style="margin:0px">
<script type="text/javascript">
	jQuery.sap.registerModulePath("views", "/collabware/views");
	var oDocumentView = sap.ui.view({id:"idmyview1", viewName:"views.Document", 
	                        type:sap.ui.core.mvc.ViewType.XML});
	oDocumentView.setHeight("100%");
	oDocumentView.setBusy(true);
	oDocumentView.placeAt("uiArea");
gwtClientLoaded = function () {
	$(function(){
		var sessionId = "${collaboration.id}";
		function injectSessionIntoIFrame(session) {
			var editorIFrame = document.getElementById("idmyview1--EditorIFrame");
			editorIFrame.contentWindow.collabwareSession = session;
			// do not allow iframed page to navigate back to us.
			editorIFrame.contentWindow.parent = undefined;
		}
		
		function editorLoaded() {
			var editorIFrame = document.getElementById("idmyview1--EditorIFrame");
			injectSessionIntoIFrame(session);
			
			startEditorWhenReady(editorIFrame.contentWindow, function () {
				session.connect(sessionId);
			});
		}
		
		function startEditorWhenReady(wnd, callback) {
			setTimeout(function() {
				if (wnd.startCollaboration) {
					wnd.startCollaboration(session);
					callback();
				} else {
					 startEditorWhenReady(wnd);
				}
			}, 100);
		}
		
		
		function loadEditorIntoIFrame(editorUrl, editorLoadedCallback) {
			var editorIFrame = document.getElementById("idmyview1--EditorIFrame");
			editorIFrame.onload = editorLoadedCallback;
			editorIFrame.src = editorUrl;
		}
		
		function jsonizeParticipants(participants) {
			var aParticipants = $.map(participants, function (p) {
				var jsonParticipant = {};
				jsonParticipant.displayName = p.getDisplayName();
				jsonParticipant.id = p.getId();
				jsonParticipant.imageUrl = p.getImageUrl();
				return jsonParticipant;
			});
			return aParticipants;
		}
		
		 var oModel = new sap.ui.model.json.JSONModel();
		 oModel.loadData("/collabware/rest/documents/" + sessionId, {}, false/*load synchronous*/);
		 sap.ui.getCore().setModel(oModel);
		 
		var session = new collabware.Session();
		window.collabwareSession = session;
		
		session.addSessionListener({
			joined: function () {
				console.log("Joined");
				oModel.setProperty("/participants", jsonizeParticipants(session.getAllParticipants()));
				oDocumentView.setBusy(false);
			},
			
			disconnected: function () {
				console.log("Disconnected");
				oDocumentView.setBusy(true);
			},
			participantAdded: function () {
				console.log("Participants added");
				oModel.setProperty("/participants", jsonizeParticipants(session.getAllParticipants()));
			}
		});
				
		$(window).unload(session.disconnect);
	
		injectSessionIntoIFrame(session);
		loadEditorIntoIFrame("/collabware/editor/${editor.contentType}/index.html", editorLoaded);
	});
};
</script>
</body>
</html>