const newPassword = document.getElementById("new-password");
const checkPassword = document.getElementById("check-password");
const changePasswordBtn = document.getElementById("change-password-btn");

changePasswordBtn.addEventListener("click", () => {
    const item = {
        "newPassword": newPassword.value,
        "checkPassword": checkPassword.value
    };
    call("/api/v1/member/password?id=" + id, "PUT", item)
        .then(data => {
            alert("비밀번호를 변경하였습니다.");
            location.href = "/login";
        })
        .catch(err => {
            err.json().then(e => alert(e.message));
        });
});