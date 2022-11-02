let refs = [];
let openedRefs = [];

let onMouseEnter = function (index) {
    openedRefs.push(index);
    let div = document.createElement('div');
    div.id = 'description_ref_'+index;
    div.className = 'description_ref';
    div.innerHTML = "<p>" + refs[index-1] + "</p>";

    document.getElementById("ref_"+index).append(div);

    div.onmouseleave = function() {
        document.getElementById("description_ref_"+index).remove();
        openedRefs.splice(openedRefs.indexOf(index), 1);
    }
}

document.onreadystatechange = function (event) {
    if (document.readyState === "complete") {
        let i = 1;

        while (document.getElementById("ref_"+i) != null) {
            const index = i;
            document.getElementById("ref_"+i).addEventListener("mouseenter", () => {
                if(openedRefs.indexOf(index) < 0)
                    onMouseEnter(index);
            },false);
            refs.push(document.getElementById("rref_"+i).innerHTML);
            i++;
        }
    }
}