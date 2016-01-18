function showPhobias() {
	var url = baseUrl() + ':8081/Phos2/phobias';
	var authToken = getCookie("auth_token");
	var params = "maxNumber=100&token="+authToken + "&lang=" + getCookie("language");
	var xmlHttp = new XMLHttpRequest();
	xmlHttp.onreadystatechange = function() {
		if (xmlHttp.status == 200) {
	
    		var phobias = [];
    		phobias = JSON.parse(xmlHttp.responseText);

    		if (phobias.length != 0) {
    			$('#no-phobia').remove();
    			$('#phobias-data-table').show();
    		}

        	var list = document.getElementById("phobias-list");
        	$('#phobias-list').empty();
	    	for (var i = 0; i < phobias.length; i++) {
    			var newLi = document.createElement("li");
    			newLi.id = phobias[i].resource;
    			newLi.innerHTML = phobias[i].title;
    			list.appendChild(newLi);
    			(function(value){
    				newLi.addEventListener("click", function() {
       					changeDisplayedPhobia(value);
    				}, false);})(phobias[i]);
    		}
    		changeDisplayedPhobia(phobias[0]);
		} else if (xmlHttp.status == 401) {
			alert("You don't have access to this API. Please log in.");
		} else if (xmlHttp.status == 500) {
			alert("Server error!");
		}
	}
	xmlHttp.open( "GET", url+"?"+params, true ); // false for synchronous request
	xmlHttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
	xmlHttp.send();
}

function showMyPhobias() {
	var url = baseUrl() + ':8081/Phos2/phobias/myPhobias';
	var authToken = getCookie("auth_token");
	var params = "maxNumber=20&token="+authToken+ "&lang=" + getCookie("language");
	var xmlHttp = new XMLHttpRequest();
	xmlHttp.onreadystatechange = function() {
		if (xmlHttp.status == 200) {
			
			var phobias = [];
    		phobias = JSON.parse(xmlHttp.responseText);

    		if (phobias.length != 0) {
    			$('#no-phobia').remove();
    			$('#phobias-data-table').show();
    		}

        	var list = document.getElementById("phobias-list");
        	$('#phobias-list').empty();
	    	for (var i = 0; i < phobias.length; i++) {
    			var newLi = document.createElement("li");
    			newLi.id = phobias[i].resource;
    			newLi.innerHTML = phobias[i].title;
    			list.appendChild(newLi);
    			(function(value){
    				newLi.addEventListener("click", function() {
       					changeDisplayedPhobia(value);
    				}, false);})(phobias[i]);
    		}
    		changeDisplayedPhobia(phobias[0]);
		} else if (xmlHttp.status == 401) {
			alert("You don't have access to this API. Please log in.");
		} else if (xmlHttp.status == 500) {
			alert("Server error!");
		}
	}
	xmlHttp.open( "GET", url+"?"+params, true ); // false for synchronous request
	xmlHttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
	xmlHttp.send();
}


function changeDisplayedPhobia(phobia) {
	var title = document.getElementById("phobia-title");
	title.innerHTML = phobia.title;

	var description = document.getElementById("phobia-description");
	description.innerHTML = phobia.longDesc;

	var image = document.getElementById("phobia-image");
	if (phobia.photo !== '') {
		image.src = phobia.photo; 
		image.style.display = "block";
	} else {
		image.style.display = "none";
	}

	var button = document.getElementById("add-to-user-list-btn");
	button.value = phobia.resource;
}

function addSelectedPhobia(resource) {
	var url = baseUrl() + ':8081/Phos2/phobias/add/';
	var authToken = getCookie("auth_token");

	var dbr = "http://dbpedia.org/resource/";
	var resourceName = resource.replace(dbr, ""); 
	var params = resourceName + "?token=" + authToken;
	var xmlHttp = new XMLHttpRequest();
	xmlHttp.onreadystatechange = function() {
		if (xmlHttp.status == 200) {
			alert("Phobia was succesfully added.");
		} else if (xmlHttp.status == 401) {
			alert("You don't have access to this API. Please log in.");
		} else if (xmlHttp.status == 500) {
			alert("Server error!");
		} else if (xmlHttp.status == 400) {
			alert("Missing parameters!");
		}
	}
	xmlHttp.open( "POST", url+params, false ); // false for synchronous request
	xmlHttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
	xmlHttp.send();
}

function removeSelectedPhobia(resource) {
	var url = baseUrl() + ':8081/Phos2/phobias/remove/';
	var authToken = getCookie("auth_token");
	var dbr = "http://dbpedia.org/resource/";
	var resourceName = resource.replace(dbr, ""); 
	var params = resourceName + "?token=" + authToken;
	var xmlHttp = new XMLHttpRequest();
	xmlHttp.onreadystatechange = function() {
		if (xmlHttp.status == 200) {
			alert("Phobia was succesfully removed.");
			document.getElementById(resource).remove();
			document.getElementById("phobias-list")
        .getElementsByTagName("li")[0].click();
		} else if (xmlHttp.status == 401) {
			alert("You don't have access to this API. Please log in.");
		} else if (xmlHttp.status == 500) {
			alert("Server error!");
		} else if (xmlHttp.status == 400) {
			alert("Missing parameters!");
		}
	}
	xmlHttp.open( "POST", url+params, false ); // false for synchronous request
	xmlHttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
	xmlHttp.send();
}