import * as Api from "../api.js";
import { createNavbar } from "../useful-functions.js";

// 요소(element), input 혹은 상수
const categoryDropdown = document.querySelector("#category-dropdown");
const searchForm = document.querySelector("#search-form");
const searchInput = searchForm.querySelector("input[name='search']");
const topBooksContainer = document.querySelector("#top-books-container");
const topOrderedBooksContainer = document.querySelector("#top-ordered-books-container");

addAllElements();
addAllEvents();

// html에 요소를 추가하는 함수들을 묶어주어서 코드를 깔끔하게 하는 역할임.
async function addAllElements() {
  createNavbar();
  await populateCategories();
  await populateTopReviewedBooks(); // 리뷰 많은 3위 책 목록 추가
  await populateTopOrderedBooks(); // 주문 많은 상위 3위 책 목록 추가
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

// 상위 3위 주문 많은 책 목록을 추가
async function populateTopOrderedBooks() {
  try {
    const topOrderedBooks = await Api.get("/api/books/top-ordered");
    console.log("Top Ordered Books:", topOrderedBooks);

    topOrderedBooks.forEach((book, index) => {
      const {id, title, imageURL, price} = book;
      const bookHtml = `
        <div class="column is-one-third top-book">
          <div class="card top-ordered-book" data-id="${id}">
            <div class="card-image">
              <figure class="image is-4by3">
                <img src="${imageURL}" alt="${title}">
              </figure>
            </div>
            <div class="card-content">
              <div class="media">
                <div class="media-content">
                  <p class="rank">${index + 1}</p>
                  <p class="title is-4">${title}</p>
                  <p class="subtitle is-6">${price}원</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      `;
      topOrderedBooksContainer.insertAdjacentHTML("beforeend", bookHtml);
    });

    document.querySelectorAll(".top-ordered-book").forEach(card => {
      card.addEventListener("click", () => {
        const bookId = card.getAttribute("data-id");
        window.location.href = `/book/${bookId}`;
      });
    });

  } catch (error) {
    console.error("Failed to fetch top-ordered books:", error);
  }
}

// 상위 3위 책 목록 추가
async function populateTopReviewedBooks() {
  try {
    const topBooks = await Api.get("/api/books/top-reviewed");
    console.log("Top Books:", topBooks);

    topBooks.forEach((book, index) => {
      const { id, title, imageURL, price } = book;
      const bookHtml = `
        <div class="column is-one-third top-book">
          <div class="card top-reviewed-book" data-id="${id}">
            <div class="card-image">
              <figure class="image is-4by3">
                <img src="${imageURL}" alt="${title}">
              </figure>
            </div>
            <div class="card-content">
              <div class="media">
                <div class="media-content">
                  <p class="rank">${index + 1}</p>
                  <p class="title is-4">${title}</p>
                  <p class="subtitle is-6">${price}원</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      `;
      topBooksContainer.insertAdjacentHTML("beforeend", bookHtml);
    });

    document.querySelectorAll(".top-reviewed-book").forEach(card => {
      card.addEventListener("click", () => {
        const bookId = card.getAttribute("data-id");
        window.location.href = `/book/${bookId}`;
      });
    });

  } catch (error) {
    console.error("Failed to fetch top-reviewed books:", error);
  }
}
