const title = document.getElementById("title");
const content = document.getElementById("content");
const createBtn = document.getElementById("create-btn");

createBtn.addEventListener("click", () => {
    const item = {
        "title": title.value,
        "content": content.value
    };

    call("/api/v1/posts", "POST", item)
        .then(data => {
            alert("글 작성에 성공하셨습니다.");
            console.log(data);
            location.href = "/posts/" + data.id;
        })
        .catch(err => err.json().then(e => alert(e.message)));
});