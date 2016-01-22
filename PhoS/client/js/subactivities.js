function getSubactivities(){
    var url = document.location.href,
    params = url.split('?')[1].split('&'),
    data = {}, tmp;
    for (var i = 0, l = params.length; i < l; i++) {
        tmp = params[i].split('=');
        data[tmp[0]] = tmp[1];
    }
   
    var defaultOption = document.getElementById("default-activity");
    if ( data.subactivity == "WATCH") {
        defaultOption.innerHTML = "Select movie type";
    } else if ( data.subactivity == "VISIT"){
        defaultOption.innerHTML = "Select place";
    } else if ( data.subactivity == "PRACTICE"){
        defaultOption.innerHTML = "Select sport type";
    }

    var url = baseUrl() + ':8081/Phos2/getSubactivities';
    var authToken = getCookie("auth_token")+ "&lang=" + getCookie("language");;
    var params = "activity=" + data.subactivity + "&token="+ authToken;
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function() {
        if (xmlHttp.status == 200) {
            
            var subactivities = [];
            subactivities = JSON.parse(xmlHttp.responseText);
            var select = document.getElementById("activities-select");
            $('#activities-select option:not(:first)').remove();
            for (var i = 0; i < subactivities.length; i++) {
                var newOption = document.createElement("option");
                newOption.id = subactivities[i].resource;
                newOption.innerHTML = subactivities[i].name;
                select.appendChild(newOption);
            }

         //   window.location.href = 'subactivities.html';
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

function getFriends() {
    var url = baseUrl() + ':8081/Phos2/user/friends';
    var authToken = getCookie("auth_token");
    var params = "token="+authToken;
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function() {
        if (xmlHttp.status == 200) {
    
            var userFriends = [];
            userFriends = JSON.parse(xmlHttp.responseText);

            var select = document.getElementById("friends-select");
            $('#friends-select option:not(:first)').remove();
            for (var i = 0; i < userFriends.length; i++) {
                var newOption = document.createElement("option");
                newOption.id = userFriends[i].email;
                newOption.innerHTML = userFriends[i].name;
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

function findPhobias() {
    var url = document.location.href,
    params = url.split('?')[1].split('&'),
    data = {}, tmp;
    for (var i = 0, l = params.length; i < l; i++) {
        tmp = params[i].split('=');
        data[tmp[0]] = tmp[1];
    }
    
    var activity = data.subactivity;
    var friendSelect = document.getElementById("friends-select");
    var selectedFriend = "";
    if (friendSelect.selectedIndex != 0) {
        selectedFriend = friendSelect.options[friendSelect.selectedIndex].id;    
    }
    var mailExtension = "@gmail.com";
    var selectedFriendUsername = selectedFriend.replace(mailExtension, ""); 

    var subactivitySelect = document.getElementById("activities-select");
    var selectedActivity = "";
    if (subactivitySelect.selectedIndex != 0) {
        selectedActivity = subactivitySelect.options[subactivitySelect.selectedIndex].id;    
    }
    var dbr = "http://dbpedia.org/resource/";
    var subactivityResourceName = selectedActivity.replace(dbr, ""); 
    dbr = "Category:";
    subactivityResourceName = subactivityResourceName.replace(dbr, ""); 

    var params = "?activity=" + activity + "&subactivity=" + subactivityResourceName + "&friend=" + selectedFriendUsername;
    window.location.href = 'possiblePhobias.html' + params;
}

window.onload = function() {
    setLanguage();
    getSubactivities();
    getFriends();
    gapi.load('auth2', function() {
        gapi.auth2.init();
    });
}

function changeLang(lang) {
    setCookie("language", lang.value);
    changeTextElementsForLang(lang.value);
    getSubactivities();
}

function changeTextElementsForLang(lang) {
    jQuery.i18n.properties({
        name: 'Messages', 
        path: 'bundle/', 
        mode: 'both', 
        language: lang, 
        callback: function() { 
        $("#profile").text($.i18n.prop('profile'));
        $("#signOut").text($.i18n.prop('signOut')); 
        $("#home").text($.i18n.prop('home'));  
        $("#with-option").text($.i18n.prop('friendWith'));  
        }
    });
}