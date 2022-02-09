const email = document.getElementById("email");
const name = document.getElementById("name");
const findPasswordBtn = document.getElementById("find-password-btn");

findPasswordBtn.addEventListener("click", () => {
    call("/api/v1/member/id?email=" + email.value + "&name=" + name.value, "GET")
            .then(data => location.href = "/change-password?id=" + data.id)
            .catch(err => {
                err.json().then(e => alert(e.message));
            });
})