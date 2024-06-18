import * as Api from "../api.js";
import { createNavbar } from "../useful-functions.js";

// 요소(element), input 혹은 상수
const categoryDropdown = document.querySelector("#category-dropdown");
const searchForm = document.querySelector("#search-form");
const searchInput = searchForm.querySelector("input[name='search']");

addAllElements();
addAllEvents();

// 메인 페이지 로드 시 사용자 정보 가져오기
window.addEventListener('DOMContentLoaded', async () => {
  const token = sessionStorage.getItem('token');
  if (token) {
    fetchUserInfo(token);
    createNavbar();
  }
});

// 네이버 OAuth2 인증 성공 후 토큰 받기
window.addEventListener('message', (event) => {
  if (event.origin === window.location.origin && event.data.token) {
    const token = event.data.token;
    sessionStorage.setItem('token', token);
    fetchUserInfo(token);
    createNavbar();
  }
});

async function fetchUserInfo(token) {
  try {
    const response = await fetch('/users/me', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });

    if (response.ok) {
      const data = await response.json();
      const { email, roles } = data;
      updateUserInfo(email, roles);
    } else {
      console.error('Failed to fetch user info:', response.status);
    }
  } catch (error) {
    console.error('Error fetching user info:', error);
  }
}

function updateUserInfo(email, roles) {
  const isAdmin = roles.includes("ADMIN");
  if (isAdmin) {
    sessionStorage.setItem("isAdmin", "true");
  } else {
    sessionStorage.removeItem("isAdmin");
  }
}


// HTML에 요소를 추가하는 함수들을 묶어주어서 코드를 깔끔하게 하는 역할임.
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
  event.preventDefault(); // 기본 제출 동작을 막음
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
  const token = sessionStorage.getItem('token');
  const categories = await Api.get("/categories", token);
  console.log(categories);

  for (const category of categories) {
    const { id, name } = category;

    categoryDropdown.insertAdjacentHTML(
      "beforeend",
      `<a class="navbar-item" href="/books/category?categoryId=${id}">${name}</a>`
    );
  }
}