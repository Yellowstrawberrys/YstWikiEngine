function docMenu(){
    const menu = document.getElementById("docMenu");
    if(menu.style.visibility === "hidden" || !menu.style.visibility){
        menu.style.visibility = "visible";
    }else
        menu.style.visibility = "hidden";
}

function userInfo(){
    const menu = document.getElementById("userInfo");
    if(menu.style.visibility === "hidden" || !menu.style.visibility){
        menu.style.visibility = "visible";
    }else
        menu.style.visibility = "hidden";
}