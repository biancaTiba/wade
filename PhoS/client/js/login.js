function baseUrl() {
	return "http://192.168.0.105";
}

function onSignIn(googleUser) {
	var profile = googleUser.getBasicProfile();
	var url = baseUrl() + ':8081/Phos2/login';
	var xmlHttp = new XMLHttpRequest();
	xmlHttp.onreadystatechange = function() {
		if (xmlHttp.readyState == XMLHttpRequest.DONE) {
			console.log(xmlHttp.status);
			if (xmlHttp.status == 200) {
				var response = JSON.parse(xmlHttp.responseText);
				console.log(response);
				setCookie("auth_token", response.token);
    			window.location.href = 'home.html';
    		} else if (xmlHttp.status == 404) {
				alert("Error: Please contact the support team!");    			
    		}
		}
	}
	xmlHttp.open( "POST", url, true ); // false for synchronous request
	xmlHttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
	xmlHttp.send(JSON.stringify({"name":profile.getName(), 
								 "id":profile.getId(), 
								 "imageUrl":profile.getImageUrl(),
								 "email":profile.getEmail()
	}));
}

function onSignOut() {
    var auth2 = gapi.auth2.getAuthInstance();
    auth2.signOut().then(function () {
    	console.log('User signed out.');
    });
}

function setCookie(cookieName, cookieValue) {
    var date = new Date();
    date.setTime(date.getTime() + (3*60*60*1000));
    var expires = "expires="+date.toUTCString();
    document.cookie = cookieName + "=" + cookieValue + "; " + expires;
}

function getCookie(cookieName) {
    var name = cookieName + "=";
    var cookie = document.cookie.split(';');
    for(var i=0; i<cookie.length; i++) {
        var c = cookie[i];
        while (c.charAt(0)==' ') c = c.substring(1);
        if (c.indexOf(name) == 0) return c.substring(name.length, c.length);
    }
    return "";
}

function setLanguage(){
    var lang = getCookie("language");
    if (lang == "") {
        lang = "en";
    }
    var languageSelect = document.getElementById("lang");
    if (typeof languageSelect !== 'undefined') {
    	languageSelect.value = lang;
	}
    changeTextElementsForLang(lang);
}