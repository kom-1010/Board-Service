const email = document.getElementById("email");
const password = document.getElementById("password");
const loginBtn = document.getElementById("login-btn");

loginBtn.addEventListener("click", () => {
    const item = {
        "email": email.value,
        "password": password.value
    };

    call("/api/v1/member/login", "POST", item)
        .then(data => {
            alert("로그인에 성공하였습니다.");
            location.href = "/";
        })
        .catch(err => {
            err.json().then(e => alert(e.message));
        });
})