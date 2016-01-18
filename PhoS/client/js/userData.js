function getUserDetails() {
	var url = baseUrl() + ':8081/Phos2/user/userDetails';
	var authToken = getCookie("auth_token");
	var params = "token="+authToken;
	var xmlHttp = new XMLHttpRequest();
	xmlHttp.onreadystatechange = function() {
		if (xmlHttp.status == 200) {
	
    		var userDetails = {};
    		userDetails = JSON.parse(xmlHttp.responseText);

        	var name = document.getElementById("user-name");
        	var email = document.getElementById("user-email");
        	var img = document.getElementById("user-img");
	    	name.innerHTML = userDetails.name;
	    	email.innerHTML = userDetails.email;
	    	img.src = userDetails.photo;
   		} else if (xmlHttp.status == 401) {
			alert("You don't have access to this API. Please log in.");
		} else if (xmlHttp.status == 500) {
			alert("Server error!");
		}
	}
	xmlHttp.open( "GET", url+"?"+params, true ); 
	xmlHttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
	xmlHttp.send();
}

function addUserAsFriend(){
	var url = baseUrl() + ':8081/Phos2/user/addFriend';
	var authToken = getCookie("auth_token");
	var username = document.getElementById("friend-username").value;
	var params = "friendUsername=" + username + "&token="+authToken;
	var xmlHttp = new XMLHttpRequest();
	xmlHttp.onreadystatechange = function() {
		if (xmlHttp.status == 200) {
			$('#addChildWrapper').hide();
			getUserFriends();
   		} else if (xmlHttp.status == 401) {
			alert("You don't have access to this API. Please log in.");
		} else if (xmlHttp.status == 500) {
			alert("Server error!");
		} else if (xmlHttp.status == 400) {
			alert("Missing parameters!");
		} else if (xmlHttp.status == 406) {
			alert("User not exist!");
		}
	}
	xmlHttp.open( "POST", url+"?"+params, false ); 
	xmlHttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
	xmlHttp.send();
}

function getUserFriends() {
	var url = baseUrl() + ':8081/Phos2/user/friends';
	var authToken = getCookie("auth_token");
	var params = "token="+authToken;
	var xmlHttp = new XMLHttpRequest();
	xmlHttp.onreadystatechange = function() {
		if (xmlHttp.status == 200) {
	
    		var userFriends = [];
    		userFriends = JSON.parse(xmlHttp.responseText);

        	var list = document.getElementById("friends-list");
        	$('#friends-list li').remove();
        	for (var i = 0; i < userFriends.length; i++) {
    			var newLi = document.createElement("li");
    			newLi.className = "img-with-text";
    			var image = document.createElement("img");
    			image.className = "friend-image";
    			if (userFriends[i].imageUrl == "") {
    				image.src = "http://events.funnewjersey.com/uploads/profile_photos/nophoto-0.jpg";
    			} else {
	    			image.src = userFriends[i].imageUrl;
	    		}
    			var text = document.createElement("p");
    			text.innerHTML = userFriends[i].name;
    			newLi.appendChild(image);
    			newLi.appendChild(text);
    			list.appendChild(newLi);
    		}
   		} else if (xmlHttp.status == 401) {
			alert("You don't have access to this API. Please log in.");
		} else if (xmlHttp.status == 500) {
			alert("Server error!");
		}
	}
	xmlHttp.open( "GET", url+"?"+params, false ); 
	xmlHttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
	xmlHttp.send();
}

function completeFieldsWithUserData(){
	var url = baseUrl() + ':8081/Phos2/user/getUserData';
	var authToken = getCookie("auth_token");
	var params = "token="+authToken;
	var xmlHttp = new XMLHttpRequest();
	xmlHttp.onreadystatechange = function() {
		if (xmlHttp.status == 200) {
	
    		var userDetails = {};
    		userDetails = JSON.parse(xmlHttp.responseText);
    		//document.getElementById("datepicker").value = "1/05/1993";

    		var select = document.getElementById("animals-select");
			if (userDetails.animal != "null") {
				select.value = userDetails.animal;
			}

			var genderRadios = document.getElementsByName('gender');
			for (var i = 0, length = genderRadios.length; i < length; i++) {
			    if (genderRadios[i].value == userDetails.gender) {
			    	genderRadios[i].checked = true;
			        break;
			    }
			}

			var childrenRadios = document.getElementsByName('children');
			for (var i = 0, length = childrenRadios.length; i < length; i++) {
			    if (childrenRadios[i].value == userDetails.hasChildren){
			    	childrenRadios[i].checked = true;
			        break;
			    }
			}
        	
   		} else if (xmlHttp.status == 401) {
			alert("You don't have access to this API. Please log in.");
		} else if (xmlHttp.status == 500) {
			alert("Server error!");
		}
	}
	xmlHttp.open( "GET", url+"?"+params, true );
	xmlHttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
	xmlHttp.send();
}

function showAnimals(){
	var url = baseUrl() + ':8081/Phos2/user/getAnimals';
    var authToken = getCookie("auth_token");
    var params = "token="+authToken+ "&lang=" + getCookie("language");
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function() {
        if (xmlHttp.status == 200) {
            
            var animals = {};
            animals = JSON.parse(xmlHttp.responseText);

            var select = document.getElementById("animals-select");
            $('#animals-select option:not(:first)').remove();
            for (var key in animals) {
                var newOption = document.createElement("option");
                newOption.id = animals[key];
                newOption.innerHTML = animals[key];
                select.appendChild(newOption);
            }
        } else if (xmlHttp.status == 401) {
            alert("You don't have access to this API. Please log in.");
        } else if (xmlHttp.status == 500) {
            alert("Server error!");
        }
    }
    xmlHttp.open( "GET", url+"?"+params, true );
    xmlHttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    xmlHttp.send();
}

function saveUserData(){
	//var birthDate = new Date(document.getElementById("datepicker").value);
	//var dateFormatted = birthDate.getDate() + "-" + (birthDate.getMonth() + 1) + "-" + birthDate.getFullYear();
	
	var genderRadios = document.getElementsByName('gender');
	var selectedGender;
	for (var i = 0, length = genderRadios.length; i < length; i++) {
	    if (genderRadios[i].checked) {
	    	selectedGender = genderRadios[i].value;
	        break;
	    }
	}

	console.log(selectedGender);
	var childrenRadios = document.getElementsByName('children');
	var hasChildren;
	for (var i = 0, length = childrenRadios.length; i < length; i++) {
	    if (childrenRadios[i].checked) {
	    	hasChildren = childrenRadios[i].value;
	        break;
	    }
	}
	console.log(hasChildren);
	
	var select = document.getElementById("animals-select");
    var selectedAnimal = select.options[select.selectedIndex].id;
	console.log(selectedAnimal);  

	var url = baseUrl() + ':8081/Phos2/user/saveUserData';
	var authToken = getCookie("auth_token");
	// "?birthDate=" + dateFormatted + 
	var params = "?gender=" + selectedGender + "&hasChildren=" + hasChildren + "&animal=" + selectedAnimal +"&token="+authToken;
	var xmlHttp = new XMLHttpRequest();
	xmlHttp.onreadystatechange = function() {
		if (xmlHttp.status == 200) {
			alert("Data was succesfully updated.");
		} else if (xmlHttp.status == 401) {
			alert("You don't have access to this API. Please log in.");
		} else if (xmlHttp.status == 500) {
			alert("Server error!");
		} else if (xmlHttp.status == 400) {
			alert("Missing parameters!");
		}
	}
	xmlHttp.open( "POST", url+params, false );
	xmlHttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
	xmlHttp.send();
}