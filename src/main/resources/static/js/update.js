const title = document.getElementById("title");
const content = document.getElementById("content");
const updateBtn = document.getElementById("update-btn");

updateBtn.addEventListener("click", () => {
    const item = {
        "title": title.value,
        "content": content.value
    };

    call("/api/v1/posts/" + id, "PUT", item)
    .then(data => {
        alert("게시글이 수정되었습니다.");
        location.href = "/posts/" + id;
    })
    .catch(err => err.json().then(e => alert(e.message)));
})