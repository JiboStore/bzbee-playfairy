/**
 * source: https://www.w3schools.com/howto/howto_js_sidenav.asp
 * Javascript is my weakpoint coz I hate hardcore hardcoding values that doesn't translate well
 * in multiple screen, so I just copy and paste
 * @returns
 */

/* Set the width of the side navigation to 250px and the left margin of the page content to 250px */
function openNav() {
    document.getElementById("mySidenav").style.width = "250px";
    document.getElementById("main").style.marginLeft = "250px";
}

/* Set the width of the side navigation to 0 and the left margin of the page content to 0 */
function closeNav() {
    document.getElementById("mySidenav").style.width = "0";
    document.getElementById("main").style.marginLeft = "0";
}