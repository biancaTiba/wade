function changeLang(lang) {
        setCookie("language", lang.value);
        changeTextElementsForLang(lang.value);
        showPhobias();
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
        $("#home").text($.i18n.prop('home')); 
        $("#default-option").text($.i18n.prop('selectActivity')); 
        $("#add-to-user-list-btn ").text($.i18n.prop('addPhobia'));  
        $("#my-phobias-button").text($.i18n.prop('myPhobias'));  
        $("#phobias-button").text($.i18n.prop('phobias'));
        $("#phobias-title ").text($.i18n.prop('phobias')); 
        }
    });
}

window.onload = function() {
    setLanguage();
    gapi.load('auth2', function() {
        gapi.auth2.init();
    });
    showPhobias();
};