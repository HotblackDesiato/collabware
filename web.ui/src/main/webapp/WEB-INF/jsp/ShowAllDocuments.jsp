<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>Collabware</title>
		<script id="sap-ui-bootstrap"
		        src="https://sapui5.hana.ondemand.com/resources/sap-ui-core.js"
		        data-sap-ui-theme="sap_bluecrystal"
		        data-sap-ui-libs="sap.ui.commons"
		        data-sap-ui-xx-bindingSyntax="complex"
        >
        
        
        </script>
	<style type="text/css">
	.LeftMargin {
		margin-left:20px;
	}
	.DocumentTypes  img {
		height:30px;
	}
	</style>
</head>

<script type="text/javascript">
	jQuery.sap.registerModulePath("views", "/collabware/views");
	jQuery.sap.require("sap.ui.model.json.JSONModel");
	
	sap.ui.getCore().setModel(new sap.ui.model.json.JSONModel({user:{displayName:"${participant.user.displayName}"}}));
	var view = sap.ui.view({id:"idmyview1", viewName:"views.Main", type:sap.ui.core.mvc.ViewType.XML});
	view.placeAt("uiArea");
</script>

<body class="sapUiBody" id="uiArea" style="margin:0px">

</body>
</html>