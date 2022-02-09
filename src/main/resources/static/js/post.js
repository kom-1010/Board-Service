const deleteBtn = document.getElementById("delete-btn");
const recommendBtn = document.getElementById("recommend-btn");
const recommendCancelBtn = document.getElementById("recommend-cancel-btn");

deleteBtn.addEventListener("click", () => {
    call("/api/v1/posts/" + id, "DELETE")
    .then(data => {
        alert("게시글이 삭제되었습니다.");
        location.href = "/";
    })
    .catch(err => err.json().then(e => alert(e.message)));
});

function recommend(){
    call("/api/v1/posts/" + id + "/recommend", "POST")
    .then(data => {
        alert("게시글을 추천하였습니다.");
        location.href = "/posts/" + id;
    })
    .catch(err => err.json().then(e => alert(e.message)));
}

function recommendCancel(){
    call("/api/v1/posts/" + id + "/recommend", "DELETE")
    .then(data => {
        alert("게시글의 추천을 취소하였습니다.");
        location.href = "/posts/" + id;
    })
    .catch(err => err.json().then(e => alert(e.message)));
}

//recommendBtn.addEventListener("click", () => {
//    call("/api/v1/posts/" + id + "/recommend", "POST")
//    .then(data => {
//        alert("게시글을 추천하였습니다.");
//        location.href = "/posts/" + id;
//    })
//    .catch(err => err.json().then(e => alert(e.message)));
//});
//
//recommendCancelBtn.addEventListener("click", () => {
//    call("/api/v1/posts/" + id + "/recommend", "DELETE")
//    .then(data => {
//        alert("게시글의 추천을 취소하였습니다.");
//        location.href = "/posts/" + id;
//    })
//    .catch(err => err.json().then(e => alert(e.message)));
//});