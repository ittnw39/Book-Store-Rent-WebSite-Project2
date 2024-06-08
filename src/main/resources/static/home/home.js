import * as Api from "../api.js";
import { createNavbar } from "../useful-functions.js";

// 요소(element), input 혹은 상수
const categoryDropdown = document.querySelector("#category-dropdown");
const searchForm = document.querySelector("#search-form");
const searchInput = searchForm.querySelector("input[name='search']");

addAllElements();
addAllEvents();

// html에 요소를 추가하는 함수들을 묶어주어서 코드를 깔끔하게 하는 역할임.
async function addAllElements() {
  createNavbar();
  await populateCategories();
}

// 여러 개의 addEventListener들을 묶어주어서 코드를 깔끔하게 하는 역할임.
function addAllEvents() {
  searchForm.addEventListener("submit", handleSearch);
}

// 검색 폼 제출 처리 함수
function handleSearch(event) {
  event.preventDefault();  // 기본 제출 동작을 막음
  console.log('handleSearch called');
  const keyword = searchInput.value.trim();
  console.log('Search keyword:', keyword);
  if (keyword) {
    const encodedKeyword = encodeURIComponent(keyword);
    console.log('Encoded keyword:', encodedKeyword);
    window.location.href = `/books/search?keyword=${encodedKeyword}`;
  }
}

// 드롭다운 메뉴에 카테고리 목록을 추가
async function populateCategories() {
  const categories = await Api.get("/categories");
  console.log(categories);

  for (const category of categories) {
    const { id, name } = category;

    categoryDropdown.insertAdjacentHTML(
        "beforeend",
        `<a class="navbar-item" href="/books/category?categoryId=${id}">${name}</a>`
    );
  }
}
