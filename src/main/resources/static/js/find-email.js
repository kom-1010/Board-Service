const name = document.getElementById("name");
const findEmailBtn = document.getElementById("find-email-btn");

findEmailBtn.addEventListener("click", () => {
    call("/api/v1/member/id?name="+name.value, "GET")
        .then(data => location.href = "/get-email?id=" + data.id)
        .catch(err => {
            err.json().then(e => alert(e.message));
        });

})