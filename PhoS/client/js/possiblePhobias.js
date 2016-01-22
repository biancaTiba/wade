function changeLang(lang) {
    setCookie(lang.value);
    changeTextElementsForLang(lang.value);
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
        $(".modal-link").text($.i18n.prop('suggestions'));  
        }
    });
}

function findSuggestedPhobias(){
    var url = document.location.href,
    params = url.split('?')[1].split('&'),
    data = {}, tmp;
    for (var i = 0, l = params.length; i < l; i++) {
        tmp = params[i].split('=');
        data[tmp[0]] = tmp[1];
    }
    var activity = data.activity;
    var subactivity = data.subactivity;
    var friend = data.friend;

    var url = baseUrl() + ':8081/Phos2/findPhobias';
    var authToken = getCookie("auth_token");
    var params = "?activity=" + activity + "&subactivity=" + subactivity + "&friend=" + friend + "&token="+ authToken+ "&lang=" + getCookie("language");
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function() {
        if (xmlHttp.status == 200) {
            
            var phobias = [];
            phobias = JSON.parse(xmlHttp.responseText);

            var panels = document.getElementById("phobia-pannels");
            if (phobias.length != 0) {
                $('#no-phobia').remove();
               // $('#phobia-pannels').empty();
            }
            for (var i = 0; i < phobias.length; i++) {
                console.log(phobias[i]);
                var panel = document.createElement("div");
                panel.className = "panel phobia-panel";
                var panelHeading = document.createElement("div");
                panelHeading.className = "panel-heading phobia-heading";
                var panelTitle = document.createElement("h3");
                panelTitle.className = "panel-title";
                panelTitle.innerHTML = phobias[i].title;
                var panelBody = document.createElement("div");
                panelBody.className = "panel-body phobia-body";
                panelBody.innerHTML = phobias[i].shortDesc;
                var panelFooter = document.createElement("div");
                panelFooter.className = "panel-footer phobia-footer";
                var aElement = document.createElement("a");
                aElement.href = "#modal-container";
                aElement.setAttribute("role", "button");
                aElement.className = "btn modal-link";
                aElement.innerHTML = "See suggestions";
                aElement.id = "see-suggestions";
                aElement.setAttribute("data-toggle", "modal");
                panelFooter.appendChild(aElement);
                panelHeading.appendChild(panelTitle);
                panel.appendChild(panelHeading);
                panel.appendChild(panelBody);
                panel.appendChild(panelFooter);
                panels.appendChild(panel);
                (function(value){
                aElement.addEventListener("click", function() {
                    showTreatmentForPhobia(value);
                }, false);})(phobias[i]);
            }
        } else if (xmlHttp.status == 401) {
            alert("You don't have access to this API. Please log in.");
        } else if (xmlHttp.status == 500) {
            alert("Server error!");
        }
    }
    xmlHttp.open( "GET", url+params, false );
    xmlHttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    xmlHttp.send();
}

function showTreatmentForPhobia(phobia){
    var treatmentsList = document.getElementById("treatments-list");
    var tipsList = document.getElementById("tips-list");
    
    var url = baseUrl() + ':8081/Phos2/context/getTreatment';
    var authToken = getCookie("auth_token");
    var params = "?phobia=" + phobia.title + "&token="+ authToken + "&lang=" + getCookie("language");
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function() {
        if (xmlHttp.status == 200) {
            
            var suggestions = {};
            suggestions = JSON.parse(xmlHttp.responseText);

            var modalTitle = document.getElementById("myModalLabel");
            modalTitle.innerHTML = phobia.title;
            $('#treatments-list li:not(:first)').remove();
            $('#tips-list li:not(:first)').remove();
            var suggestionsTreatments = suggestions.treatments;
            for (var i = 0; i < suggestionsTreatments.length; i++) {
                var treatment = document.createElement("li");
                var treatmentLink = document.createElement("a");
                treatmentLink.href = suggestionsTreatments[i].url;
                treatmentLink.innerHTML = suggestionsTreatments[i].title;
                treatment.appendChild(treatmentLink);
                treatmentsList.appendChild(treatment);
            }

            var suggestionsTips = suggestions.tips;
            for (var i = 0; i < suggestionsTips.length; i++) {
                var tip = document.createElement("li");
                var tipLink = document.createElement("a");
                tipLink.href = suggestionsTips[i].url;
                tipLink.innerHTML = suggestionsTips[i].title;
                tip.appendChild(tipLink);
                tipsList.appendChild(tip);
            }
        } else if (xmlHttp.status == 401) {
            alert("You don't have access to this API. Please log in.");
        } else if (xmlHttp.status == 500) {
            alert("Server error!");
        } else if (xmlHttp.status == 400) {
            alert("Missing parameters!");
        }
    }
    xmlHttp.open( "GET", url+params, true );
    xmlHttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    xmlHttp.send();
}

window.onload = function() {
    setLanguage();
    findSuggestedPhobias();
    gapi.load('auth2', function() {
        gapi.auth2.init();
    });
}