const email = document.getElementById("email");
const name = document.getElementById("name");
const phone = document.getElementById("phone");
const password = document.getElementById("password");
const signupBtn = document.getElementById("signup-btn");

signupBtn.addEventListener("click", () => {
    const item = {
        "email": email.value,
        "password": password.value,
        "name": name.value,
        "phone": phone.value
    };

    call("/api/v1/member/signup", "POST", item)
    .then(data => {
    location.href = "/login";
    alert("회원가입에 성공하였습니다.");
    })
    .catch(err => {
        err.json().then(e => alert(e.message));
    });
});