<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>Collabware</title>
  <script id="sap-ui-bootstrap" 
        src="https://sapui5.hana.ondemand.com/resources/sap-ui-core.js"
    data-sap-ui-theme="sap_bluecrystal"
    data-sap-ui-libs="sap.ui.commons"
    data-sap-ui-modules="sap.ui.core.plugin.DeclarativeSupport"
    >
  </script>
  <style type="text/css">
	.header {
		height: 30px;
	}
	
	.header .sapUiAppHdrLogo span {
		font-size: 20px!important;
		vertical-align: baseline;
		padding-left: 6px;
		padding-top: 3px;
	}
		.header .sapUiAppHdrLogo img {
		display:none;
	}	
	
	.LoginPanel {
		position: absolute;
		margin: -125px 0 0 -125px;
		top: 50%;
		left: 50%;
	}
	
	.LoginPanel h5 {
		font-size:20px;
		margin:5px;
		margin-left:15px;
	}
	
	.LoginForm * {
		margin:5px;
		font-size:15px;
	}
	.LoginForm label {
		margin-bottom:-5px;
		font-size:15px;
		
	}
	.LoginForm input {
		width:250px!important;
		height:30px;
	}
	.LoginButton {
		float:right;
		margin-bottom: 10px;
	}
  </style>
  <script type="text/javascript">
  $(function () {
	  sap.ui.getCore().getElementById("username").focus();
  });
  function userNameEntered(e) {
	  sap.ui.getCore().getElementById("password").focus();
  }
  function passwordEntered(e) {
	  sap.ui.getCore().getElementById("loginButton").focus();
  }
  
  function login(e) {
	  $("#loginForm").submit();
  }
  </script>
</head>
<body class="sapUiBody" id="uiArea" style="margin:0px">
	<div class="header" data-sap-ui-type="sap.ui.commons.ApplicationHeader" data-logo-src="" data-display-logoff="false" data-display-welcome="false" data-logo-text="Collabware"></div>
	<form id="loginForm" action="../<c:url value='j_spring_security_check' />" method="POST">
		<div data-sap-ui-type="sap.ui.commons.Panel" data-text="Login" class="LoginPanel" data-width="" data-show-collapse-icon="false">
			<div data-sap-ui-type="sap.ui.commons.layout.VerticalLayout" class="LoginForm">
				<div data-sap-ui-type="sap.ui.commons.Label" data-text="User name" data-placeholder="User Name"></div>
				<div id="username" data-name='j_username' data-sap-ui-type="sap.ui.commons.TextField"  data-change="userNameEntered"></div>
				<div data-sap-ui-type="sap.ui.commons.Label" data-text="Password"></div>
				<div id="password" data-name='j_password' data-sap-ui-type="sap.ui.commons.PasswordField" data-change="passwordEntered"></div>
				<div id="loginButton" data-sap-ui-type="sap.ui.commons.Button" data-text="Login" class="LoginButton" data-press="login"></div>
			</div>
		</div>
	</form>
</body>
</html>