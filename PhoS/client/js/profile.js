function changeLang(lang) {
    setCookie("language", lang.value);
    changeTextElementsForLang(lang.value);
}

function changeTextElementsForLang(lang) {
    jQuery.i18n.properties({
        name: 'Messages', 
        path: 'bundle/', 
        mode: 'both', 
        language: lang, 
        callback: function() { 
        $("#home").text($.i18n.prop('home'));
        $("#signOut").text($.i18n.prop('signOut')); 
        $("#general-title").text($.i18n.prop('general')); 
        $("#general-li").text($.i18n.prop('general'));  
        $("#about-me-li").text($.i18n.prop('aboutMe')); 
        $("#friends-li").text($.i18n.prop('friends'));
        $("#user-name-label").text($.i18n.prop('name'));
        $("#user-email-label").text($.i18n.prop('email'));
        $("#user-image-label").text($.i18n.prop('image'));
        }
    });
}

window.onload = function() {
    setLanguage();
    getUserDetails();
    gapi.load('auth2', function() {
        gapi.auth2.init();
    });
}