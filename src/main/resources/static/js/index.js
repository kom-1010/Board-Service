const dropdownDefaultOption = document.querySelector(".default-option");
const dropdownList = document.querySelector(".dropdown-list");
const dropdownKeyword = document.querySelectorAll(".dropdown-keyword");
const searchInput = document.querySelector(".search-input");
const searchBtn = document.querySelector(".search-btn");
const logoutBtn = document.getElementById("logout-btn");

dropdownDefaultOption.addEventListener("click", () => {
  dropdownList.classList.toggle("active");
});

for (let i = 0; i < dropdownKeyword.length; i++) {
  dropdownKeyword[i].addEventListener("click", () => {
    dropdownDefaultOption.innerHTML = dropdownKeyword[i].innerHTML;
  });
}

searchBtn.addEventListener("click", () => {
    let type = dropdownDefaultOption.innerHTML;
    if (type == "제목")
        type = "title";
    else
        type = "author";

    const keyword = searchInput.value;
    location.href = "/board?type=" + type + "&keyword=" + keyword;
})