import { checkAdmin } from "../../useful-functions.js";
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
checkAdmin();

// html에 요소를 추가하는 함수들을 묶어주어서 코드를 깔끔하게 하는 역할임.
function addAllElements() {
  createNavbar();
  addProductItemsToContainer();
}

// addEventListener들을 묶어주어서 코드를 깔끔하게 하는 역할임.
function addAllEvents() {}

async function addProductItemsToContainer() {
  const { category } = getUrlParams();
  const products = await Api.get(`/admin/api/books`);

  for (const product of products) {
    // 객체 destructuring
    const { id, title, description, imageURL, isRecommended, price, publisher, totalStockQuantity, author, category } =
      product;
    const random = randomId();

    productItemContainer.insertAdjacentHTML(
      "beforeend",
      `
      <div class="message media product-item" id="a${random}">
        <div class="media-left" onclick="location.href='/book/${id}';">
          <figure class="image">
            <img
              src="${imageURL}"
              alt="제품 이미지"
            />
          </figure>
        </div>
        <div class="media-content">
          <div class="content">
            <div>
              <p class="title">
                ${title}
                ${
                  isRecommended
                    ? '<span class="tag is-success is-rounded">추천</span>'
                    : ""
                }
              </p>
              <button class="button button-delete">삭제</button>
              <button class="button button-modify">수정</button>
            </div>
            <p class="category">${category.name}</p>
            <p class="author">작가 : ${author.name}</p>
            <span class="price">${addCommas(price)}원</span>
            <span class="stock"> (재고 : ${totalStockQuantity}개)</span>
          </div>
        </div>
      </div>
      `
    );

    const productItem = document.querySelector(`#a${random}`);

    // 제품 항목 클릭 이벤트
    productItem.addEventListener('click', (event) => {
      navigate(`/book/${id}`);
    });

    // 삭제 버튼 클릭 이벤트
    const deleteButton = productItem.querySelector('.button-delete');
    deleteButton.addEventListener('click', async () => {
      const confirmation = confirm('정말로 삭제하시겠습니까?');
      if (!confirmation) {
        return;
      }

      try {
        const response = await fetch(`/admin/api/book/${id}`, {
          method: 'DELETE',
        });
        if (!response.ok) {
          throw new Error('Failed to delete book');
        }
        productItem.remove();
      } catch (error) {
        alert('Failed to delete book');
      }
    });

    //수정 버튼 클릭 이벤트
    const modifyButton = productItem.querySelector('.button-modify');
    modifyButton.addEventListener('click', () => {
      location.href = `/admin/book?id=${id}`;
    });
  }
}
