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
        $("#friends-title").text($.i18n.prop('friends')); 
        $("#general-li").text($.i18n.prop('general'));  
        $("#about-me-li").text($.i18n.prop('aboutMe')); 
        $("#friends-li").text($.i18n.prop('friends'));
        $("#add-friend-btn").text($.i18n.prop('add'));
        $("#close-button").text($.i18n.prop('close'));
        $("#add-button-popover").text($.i18n.prop('add'));
        $("#popover-data").text($.i18n.prop('userEmail'));
        $("#popover-title").text($.i18n.prop('addFriend'));
        }
    });
}

window.onload = function() {
    setLanguage();
    getUserFriends();
    gapi.load('auth2', function() {
        gapi.auth2.init();
    });
}

function showAddFriendPopup(){
    $('#add-friend-btn').click(function(e){
        e.preventDefault(); 
        $('#addChildWrapper').show();
    })
    $('#close-button').click(function(e){
        e.preventDefault(); 
        $('#addChildWrapper').hide();
    })
}showAddFriendPopup();