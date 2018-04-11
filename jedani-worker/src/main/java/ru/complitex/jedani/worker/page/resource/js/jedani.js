$(function () {
    $("#menu").metisMenu({
        toggle: false
    });
});

$(function () {
    $("#content").css('min-height', ($("#menu").height()) + 'px');
});

