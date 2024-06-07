import * as Api from "../api.js";
import { createNavbar } from "../useful-functions.js";

// 요소(element), input 혹은 상수
const categoryDropdown = document.querySelector("#category-dropdown");

addAllElements();
addAllEvents();

// html에 요소를 추가하는 함수들을 묶어주어서 코드를 깔끔하게 하는 역할임.
async function addAllElements() {
  createNavbar();
  await populateCategories();
}

// 여러 개의 addEventListener들을 묶어주어서 코드를 깔끔하게 하는 역할임.
function addAllEvents() {}

// 드롭다운 메뉴에 카테고리 목록을 추가
async function populateCategories() {
  const categories = await Api.get("/categories");
  console.log(categories);

  for (const category of categories) {
    const { name } = category;

    categoryDropdown.insertAdjacentHTML(
        "beforeend",
        `<a class="navbar-item" href="/product/list?category=${name}">${name}</a>`
    );
  }
}
