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

//checkUrlParams("category");
addAllElements();
addAllEvents();

// html에 요소를 추가하는 함수들을 묶어주어서 코드를 깔끔하게 하는 역할임.
function addAllElements() {
  createNavbar();
  addProductItemsToContainer();
}

// addEventListener들을 묶어주어서 코드를 깔끔하게 하는 역할임.
function addAllEvents() {}

async function addProductItemsToContainer() {
  const { categoryId, keyword } = getUrlParams();
  let products;

  if (categoryId) {
    products = await Api.get(`/api/books/category?categoryId=${categoryId}`);
  } else if (keyword) {
    products = await Api.get(`/api/books/search?keyword=${keyword}`);
  } else {
    products = await Api.get(`/api/books`);
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
