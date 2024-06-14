import { getImageUrl } from "../../aws-s3.js";
import * as Api from "../../api.js";
import {
  randomId,
  getUrlParams,
  addCommas,
  navigate,
  checkUrlParams,
  createNavbar,
} from "../../useful-functions.js";

// 요소(element), input 혹은 상수
const productItemContainer = document.querySelector("#producItemContainer");
const categoryDropdown = document.querySelector("#category-dropdown");
const searchForm = document.querySelector("#search-form");
const searchInput = searchForm.querySelector("input[name='search']");

//checkUrlParams("category");
addAllElements();
addAllEvents();

// html에 요소를 추가하는 함수들을 묶어주어서 코드를 깔끔하게 하는 역할임.
async function addAllElements() {
  createNavbar();
  addProductItemsToContainer();
  await populateCategories();
}

// addEventListener들을 묶어주어서 코드를 깔끔하게 하는 역할임.
function addAllEvents() {
  searchForm.addEventListener("submit", handleSearch);
}

async function addProductItemsToContainer() {
  const { categoryId, keyword } = getUrlParams();
  let products;

  try {
    if (categoryId) {
        products = await Api.get(`/api/books/category?categoryId=${categoryId}`);
      } else if (keyword) {
        products = await Api.get(`/api/books/search?keyword=${keyword}`);
      } else {
        products = await Api.get(`/api/books`);
      }

    if(products.status === 404) {
      throw new Error(`예외가 발생했습니다.`);
    }
  } catch (error) {
      alert(`검색 결과가 없습니다.`);
  }

  for (const product of products) {
    // 객체 destructuring
    const { id, title, description, imageURL, isRecommended, price, publisher, totalStockQuantity, author } =
      product;
    const imageUrl = await getImageUrl(imageURL);
    const random = randomId();

    productItemContainer.insertAdjacentHTML(
        "beforeend",
        `
      <div class="message media product-item" id="a${random}">
        <div class="media-left">
          <figure class="image">
            <img
              src="${imageURL}"
              alt="제품 이미지"
            />
          </figure>
        </div>
        <div class="media-content">
          <div class="content">
            <p class="title">
              ${title}
              ${
            isRecommended
                ? '<span class="tag is-success is-rounded">추천</span>'
                : ""
        }
            </p>
            <p class="description">저자 : ${author.name}</p>
            <p class="description">${description}</p>
            <span class="price">${addCommas(price)}원</span>
            <span class="stock"> (재고 : ${totalStockQuantity}개)</span>
          </div>
        </div>
      </div>
      `
    );

    const productItem = document.querySelector(`#a${random}`);
    productItem.addEventListener(
        "click",
        navigate(`/book/${id}`)
    );
  }
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
