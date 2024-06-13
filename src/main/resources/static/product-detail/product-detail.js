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
}

// addEventListener들을 묶어주어서 코드를 깔끔하게 하는 역할임.
function addAllEvents() {
    submitReviewButton.addEventListener("click", async () => {
        const comment = reviewComment.value;
        if (comment.trim()) {
            await addReview(comment);
            reviewComment.value = "";
            await fetchAndDisplayReviews();
        }
    });

    sortOption.addEventListener("change", fetchAndDisplayReviews);
}

// Path params를 가져오는 함수
function getPathParams() {
    const pathname = window.location.pathname;
    const pathParts = pathname.split('/');
    return { id: pathParts[pathParts.length - 1] };
}

// 제품 데이터를 삽입하는 함수
async function insertProductData() {
    const { id } = getPathParams();
    const product = await Api.get(`/api/book/${id}`);

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

    addToCartButton.addEventListener("click", async () => {
        try {
            await insertDb(product);

            alert("장바구니에 추가되었습니다.");
        } catch (err) {
            // Key already exists 에러면 아래와 같이 alert함
            if (err.message.includes("Key")) {
                alert("이미 장바구니에 추가되어 있습니다.");
            }

            console.log(err);
        }
    });

    purchaseButton.addEventListener("click", async () => {
        try {
            await insertDb(product);

            window.location.href = "/order";
        } catch (err) {
            console.log(err);

            //insertDb가 에러가 되는 경우는 이미 제품이 장바구니에 있던 경우임
            //따라서 다시 추가 안 하고 바로 order 페이지로 이동함
            window.location.href = "/order";
        }
    });

    // JWT 토큰에서 현재 사용자 이메일 추출
    const token = getJwtTokenFromSession();
    if (token) {
        const decodedToken = parseJwt(token);
        currentUserEmail = decodedToken.sub; // JWT의 subject를 email로 가정
    } else {
        currentUserEmail = null; // JWT 토큰이 없으면 이메일을 null로 설정
    }

    // 리뷰 데이터 불러오기
    await fetchAndDisplayReviews();
}


// IndexedDB에 제품을 추가하는 함수
async function insertDb(product) {
    // 객체 destructuring
    const { id, price } = product;

    // 장바구니 추가 시, indexedDB에 제품 데이터 및
    // 주문수량 (기본값 1)을 저장함.
    await addToDb("cart", { ...product, quantity: 1 }, id);

    // 장바구니 요약(=전체 총합)을 업데이트함.
    await putToDb("order", "summary", (data) => {
        // 기존 데이터를 가져옴
        const count = data.productsCount;
        const total = data.productsTotal;
        const ids = data.ids;
        const selectedIds = data.selectedIds;

        // 기존 데이터가 있다면 1을 추가하고, 없다면 초기값 1을 줌
        data.productsCount = count ? count + 1 : 1;

        // 기존 데이터가 있다면 가격만큼 추가하고, 없다면 초기값으로 해당 가격을 줌
        data.productsTotal = total ? total + price : price;

        // 기존 데이터(배열)가 있다면 id만 추가하고, 없다면 배열 새로 만듦
        data.ids = ids ? [...ids, id] : [id];

        // 위와 마찬가지 방식
        data.selectedIds = selectedIds ? [...selectedIds, id] : [id];
    });
}


// 리뷰 목록을 정렬 옵션에 따라 가져오는 함수
async function fetchAndDisplayReviews() {
    const { id } = getPathParams();
    const sortBy = sortOption.value || "date";
    console.log("Selected sort option:", sortOption.value);
    const reviews = await Api.get(`/api/book/${id}/reviews?sort=${sortBy}`);
    console.log("Fetched reviews:", reviews);
    reviewsContainer.innerHTML = reviews.map(createReviewHtml).join("");

    // 수정, 삭제 버튼에 이벤트 리스너 추가
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

    // 좋아요 버튼에 이벤트 리스너 추가
    document.querySelectorAll(".like-review-button").forEach(button => {
        button.addEventListener("click", async () => {
            const reviewId = button.getAttribute("data-id");
            await likeReview(reviewId);
        });
    });
}

// 서버에 리뷰를 추가하는 함수
async function addReview(comment) {
    const { id } = getPathParams();
    const token = getJwtTokenFromSession();
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

            // DOM에서 해당 리뷰 요소 제거
            const reviewElement = document.querySelector(`.review[data-id="${reviewId}"]`);
            if (reviewElement) {
                reviewElement.remove();
            }
            // 삭제 후 리뷰 목록 다시 불러오기
            await fetchAndDisplayReviews();
        } catch (error) {
            console.error("리뷰 삭제 오류:", error);
        }
    }
}

// 리뷰에 좋아요를 추가하는 함수
async function likeReview(reviewId) {
    await Api.post(`/api/book/review/${reviewId}/like`, {});
    await fetchAndDisplayReviews();
}

// 리뷰 HTML을 생성하는 함수
function createReviewHtml(review) {
    const { id, comment, createdAt, likes, userDTO } = review;
    const isOwner = currentUserEmail && userDTO && userDTO.email === currentUserEmail;

    return `
    <div class="box review" data-id="${id}">
      <div class="content">
        <p><strong>${userDTO?.username ?? 'Unknown User'}</strong> <small>${new Date(createdAt).toLocaleString()}</small></p>
        <p>${comment}</p>
        <p>
          <span class="icon like-review-button" data-id="${id}">
            <i class="fas fa-heart" style="color: ${likes > 0 ? 'red' : 'gray'};"></i>
          </span>
          ${likes}
          ${currentUserEmail && isOwner ? `
          <button class="button is-small is-warning edit-review-button" data-id="${id}">수정</button>
          <button class="button is-small is-danger delete-review-button" data-id="${id}">삭제</button>
          ` : ""}
        </p>
      </div>
    </div>`;
}


function getJwtTokenFromSession() {
    return sessionStorage.getItem("token");
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


