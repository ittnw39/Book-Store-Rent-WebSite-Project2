import { getImageUrl } from "../../aws-s3.js";
import * as Api from "../../api.js";
import {
    getUrlParams,
    addCommas,
    checkUrlParams,
    createNavbar,
} from "../../useful-functions.js";
import { addToDb, putToDb } from "../../indexed-db.js";

// 요소(element), input 혹은 상수
const productImageTag = document.querySelector("#productImageTag");
const titleTag = document.querySelector("#titleTag");
const descriptionTag = document.querySelector("#descriptionTag");
const publisherTag = document.querySelector("#publisherTag");
const publishedDateTag = document.querySelector("#publishedDateTag");
const priceTag = document.querySelector("#priceTag");
const addToCartButton = document.querySelector("#addToCartButton");
const purchaseButton = document.querySelector("#purchaseButton");
const submitReviewButton = document.querySelector("#submitReviewButton");
const reviewComment = document.querySelector("#reviewComment");
const reviewsContainer = document.querySelector("#reviewsContainer");
const sortOption = document.querySelector("#sortOption");

let currentUserEmail = null;

addAllElements();
addAllEvents();

// html에 요소를 추가하는 함수들을 묶어주어서 코드를 깔끔하게 하는 역할임.
function addAllElements() {
    createNavbar();
    insertProductData();
    addAllEvents();
}

// addEventListener들을 묶어주어서 코드를 깔끔하게 하는 역할임.
function addAllEvents() {
    addToCartButton.addEventListener("click", handleAddToCart);
    purchaseButton.addEventListener("click", handlePurchase);
    submitReviewButton.addEventListener("click", handleReviewSubmit);
    sortOption.addEventListener("change", fetchAndDisplayReviews);
}

async function handleAddToCart() {
    const { id } = getPathParams();
    const quantity = document.getElementById("quantity").value;
    const product = await Api.get(`/api/book/${id}`);
    try {
        await addToCart(id, quantity);
        alert("장바구니에 추가되었습니다.");

        // AJAX 요청으로 id와 quantity를 /cart 페이지로 보냄

    } catch (err) {

        console.log('장바구니 추가 에러', err);
    }
}

async function handlePurchase() {
    const { id } = getPathParams();
    const product = await Api.get(`/api/book/${id}`);
    try {
        await insertDb(product);
        window.location.href = "/order";
    } catch (err) {
        console.log(err);
        window.location.href = "/order";
    }
}

async function handleReviewSubmit() {
    const comment = reviewComment.value;
    if (comment.trim()) {
        await addReview(comment);
        reviewComment.value = "";
        await fetchAndDisplayReviews();
    }
}

// Path params를 가져오는 함수
function getPathParams() {
    const pathname = window.location.pathname;
    const pathParts = pathname.split('/');
    return { id: pathParts[pathParts.length - 1] };
}

// AJAX 요청을 보내는 함수
async function addToCart(id, quantity) {
    try {
        const response = await fetch('/cart/add', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ bookId: id, quantity: quantity }),
        });
        if (!response.ok) {
            throw new Error('Failed to add to cart');
        }
        console.log('Added to cart successfully');
        window.location.href = '/cart'; // 성공적으로 추가되면 장바구니 페이지로 리다이렉트
    } catch (error) {
        console.error('Error adding to cart:', error);
    }
}


// 제품 데이터를 삽입하는 함수
async function insertProductData() {
  console.log("insertProductData called");
  const { id } = getPathParams();
  const product = await Api.get(`/api/book/${id}`);
  console.log("Product data:", product);

  // 객체 destructuring
  const {
    title,
    category,
    author,
    description,
    publisher,
    publishedDate,
    isRecommended,
    imageURL,
    price
  } = product;

  productImageTag.src = imageURL;
  titleTag.innerText = title;
  categoryTag.innerText = category.name;
  authorTag.innerText = author.name;
  descriptionTag.innerText = description;
  publisherTag.innerText = publisher;
  publishedDateTag.innerText = formatPublishedDate(publishedDate);
  priceTag.innerText = `${addCommas(price)}원`;

  function formatPublishedDate(dateString) {
    const date = new Date(dateString);
    const year = String(date.getFullYear());
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  if (isRecommended) {
    titleTag.insertAdjacentHTML(
      "beforeend",
      '<span class="tag is-success is-rounded">추천</span>'
    );
  }

  // JWT 토큰에서 현재 사용자 이메일 추출
  const token = getJwtTokenFromCookie();
  if (token) {
      const decodedToken = parseJwt(token);
      currentUserEmail = decodedToken.sub; // JWT의 subject를 email로 가정
  }

  // 리뷰 데이터 불러오기
  await fetchAndDisplayReviews();
}

//// IndexedDB에 제품을 추가하는 함수
//async function insertDb(product) {
//    const { id, price } = product;
//    await addToDb("cart", { ...product, quantity: 1 }, id);
//    await putToDb("order", "summary", (data) => {
//        const count = data.productsCount;
//        const total = data.productsTotal;
//        const ids = data.ids;
//        const selectedIds = data.selectedIds;
//
//        data.productsCount = count ? count + 1 : 1;
//        data.productsTotal = total ? total + price : price;
//        data.ids = ids ? [...ids, id] : [id];
//        data.selectedIds = selectedIds ? [...selectedIds, id] : [id];
//    });
//}

// 리뷰 목록을 정렬 옵션에 따라 가져오는 함수
async function fetchAndDisplayReviews() {
    const { id } = getPathParams();
    const sortBy = sortOption.value || "date";
    const reviews = await Api.get(`/api/book/${id}/reviews?sort=${sortBy}`);
    console.log(reviews); // 리뷰 데이터를 콘솔에 출력하여 확인합니다.
    reviewsContainer.innerHTML = reviews.map(createReviewHtml).join("");

    document.querySelectorAll(".edit-review-button").forEach(button => {
        button.addEventListener("click", async () => {
            const reviewId = button.getAttribute("data-id");
            await editReview(reviewId);
        });
    });

    document.querySelectorAll(".delete-review-button").forEach(button => {
        button.addEventListener("click", async() => {
            const reviewId = button.getAttribute("data-id");
            await deleteReview(reviewId);
        });
    });
}

// 서버에 리뷰를 추가하는 함수
async function addReview(comment) {
    const { id } = getPathParams();
    const token = getJwtTokenFromCookie(); // 쿠키에서 토큰을 가져옴
    if (!token) {
        alert("로그인이 필요합니다.");
        return;
    }
    await Api.post(`/api/book/review/${id}`, { comment });
}

// 리뷰를 수정하는 함수
async function editReview(reviewId) {
    const newComment = prompt("새로운 리뷰를 입력하세요:");
    if (newComment) {
        await Api.put(`/api/book/review/${reviewId}`, { comment: newComment } );
        await fetchAndDisplayReviews();
    }
}

// 리뷰를 삭제하는 함수
async function deleteReview(reviewId) {
    if (confirm("정말로 이 리뷰를 삭제하시겠습니까?")) {
        try {
            await Api.delete(`/api/book/review/${reviewId}`);
            await fetchAndDisplayReviews();
        } catch (error) {
            console.error("리뷰 삭제 오류:", error);
        }
    }
}

// 리뷰에 좋아요를 추가하는 함수
async function likeReview(reviewId) {
    await Api.post(`/api/book/review/${reviewId}/like`);
    await fetchAndDisplayReviews();
}

// 리뷰 HTML을 생성하는 함수
function createReviewHtml(review) {
    const { id, comment, createdAt, likes, userDTO } = review;
    const isOwner = currentUserEmail && userDTO && userDTO.email === currentUserEmail;
    return `
    <div class="box review">
      <div class="content">
        <p><strong>${userDTO?.username ?? 'Unknown User'}</strong> <small>${new Date(createdAt).toLocaleString()}</small></p>
        <p>${comment}</p>
        <p>
          <span class="icon" onclick="likeReview(${id})">
            <i class="fas fa-heart"></i>
          </span>
          ${likes}
          ${isOwner ? `
          <button class="button is-small is-warning edit-review-button" data-id="${id}">수정</button>
          <button class="button is-small is-danger delete-review-button" data-id="${id}">삭제</button>
          ` : ""}
        </p>
      </div>
    </div>`;
}

// JWT 토큰을 쿠키에서 가져오는 함수
function getJwtTokenFromCookie() {
    const cookies = document.cookie.split("; ");
    for (const cookie of cookies) {
        const [name, value] = cookie.split("=");
        if (name === "jwtToken") {
            return value;
        }
    }
    return null;
}

// JWT 토큰을 디코딩하여 페이로드 정보를 추출하는 함수
function parseJwt(token) {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));

    return JSON.parse(jsonPayload);
}

// 페이지 로드 시 리뷰를 기본 정렬 옵션으로 가져오는 함수
//document.addEventListener("DOMContentLoaded", insertProductData);

window.addEventListener("load", () => {
    addAllElements();
});

