function changeLang(lang) {
    setCookie("language", lang.value);
    changeTextElementsForLang(lang.value);
    getActivities();
}

function changeTextElementsForLang(lang) {
    jQuery.i18n.properties({
        name: 'Messages', 
        path: 'bundle/', 
        mode: 'both', 
        language: lang, 
        callback: function() { 
        $("#myPhobias").text($.i18n.prop('myPhobias'));
        $("#phobias").text($.i18n.prop('phobias'));
        $("#profile").text($.i18n.prop('profile'));
        $("#signOut").text($.i18n.prop('signOut')); 
        $("#default-option").text($.i18n.prop('selectActivity'));  
        }
    });
}

function goToSubactivitiesPage(){
    var select = document.getElementById("activities-select");
    if (select.selectedIndex == 0) {
        window.location.href = 'subactivities.html?subactivity=';
    } else {
        var selectedActivity = select.options[select.selectedIndex].id;
        window.location.href = 'subactivities.html?subactivity=' + encodeURIComponent(selectedActivity);
    }
}

function getActivities(){
    var url = baseUrl() + ':8081/Phos2/getActivities';
    var authToken = getCookie("auth_token");
    var params = "token="+authToken + "&lang=" + getCookie("language");
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function() {
        if (xmlHttp.status == 200) {
            
            var activities = {};
            activities = JSON.parse(xmlHttp.responseText);

            var select = document.getElementById("activities-select");
            $('#activities-select option:not(:first)').remove();
            for (var key in activities) {
                var newOption = document.createElement("option");
                newOption.id = key;
                newOption.innerHTML = activities[key] + "...";
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

window.onload = function() {
    setLanguage();
    getActivities();
    gapi.load('auth2', function() {
        gapi.auth2.init();
    });
}