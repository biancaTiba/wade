function changeLang(lang) {
        setCookie("language", lang.value);
        changeTextElementsForLang(lang.value);
        showAnimals();
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
        $("#about-me-title").text($.i18n.prop('aboutMe')); 
        $("#general-li").text($.i18n.prop('general'));  
        $("#about-me-li").text($.i18n.prop('aboutMe')); 
        $("#friends-li").text($.i18n.prop('friends'));
        $("#add-friend-btn").text($.i18n.prop('saveChanges'));
        $("#user-birthday-label").text($.i18n.prop('birthdate'));
        $("#user-gender-label").text($.i18n.prop('gender'));
        $("#user-pets-label").text($.i18n.prop('pets'));
        $("#user-children-label").text($.i18n.prop('children'));
        $("#yes").text($.i18n.prop('yes'));
        $("#no").text($.i18n.prop('no'));
        }
    });
}

window.onload = function() {
    setLanguage();
    showAnimals();
    completeFieldsWithUserData();
    gapi.load('auth2', function() {
        gapi.auth2.init();
    });
};

function showAddChildPopup(){
    $('.add-child-button').click(function(e){
        e.preventDefault(); 
        $('#addChildWrapper').show();
    })
    $('.add-child-button.close-button').click(function(e){
        e.preventDefault(); 
        $('#addChildWrapper').hide();
    })
}showAddChildPopup();