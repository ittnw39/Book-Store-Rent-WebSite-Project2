import * as Api from "../api.js";
import {
  checkLogin,
  addCommas,
  convertToNumber,
  navigate,
  randomPick,
  createNavbar,
} from "../useful-functions.js";

const receiverNameElem = document.querySelector(".receiverName");
const receiverPhoneNumberElem = document.querySelector(".receiverPhoneNumber");
const addressElem = document.querySelector(".userAddress");
const requestElem = document.querySelector(".request");
const orderStatusElem = document.querySelector(".orderStatus");
const checkoutButton = document.querySelector("#checkoutButton");
const productsTitleElem = document.querySelector("#productsTitle");
const deliveryFeeElem = document.querySelector("#deliveryFee");
const discountRateElem = document.querySelector("#discountRate");
const orderTotalElem = document.querySelector("#orderTotal");

let orderId;
let globalUserId;

checkLogin();
addAllElements();
addAllEvents();
loadOrderDetails();

// html에 요소를 추가하는 함수들을 묶어주어서 코드를 깔끔하게 하는 역할임.
function addAllElements() {
  createNavbar();
}

function addAllEvents() {
  checkoutButton.addEventListener("click", enableAddressEdit);
}

// 주문 상세 정보 가져오기
async function loadOrderDetails() {
  const token = sessionStorage.getItem("token");

  const urlParams = new URLSearchParams(window.location.search);
  orderId = urlParams.get("orderId");

  if (!orderId) {
    alert("주문 ID가 누락되었습니다.");
    return navigate("/");
  }

  try {
    const response = await fetch(`/orders/details/${orderId}`);
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    const responseText = await response.text();
    const orderData = JSON.parse(responseText);

    console.log("Order Data:", orderData);

    globalUserId = orderData.userId;

    displayOrderDetails({
      userAddress: orderData.userAddress,
      request: orderData.request,
      orderStatus: orderData.orderStatus,
    });

    const userData = await Api.get("/users/data");

    console.log("User Data:", userData);

    displayUserDetails({
      username: userData.username,
      phone_number: userData.phone_number,
    });

    await loadOrderLineDetails(orderId);

  } catch (error) {
    console.error("Error loading order details:", error);
    alert("주문 정보를 불러오는 중 오류가 발생했습니다.");
  }
}

// 주문 상세 정보를 화면에 집어넣음
function displayOrderDetails({ userAddress, request, orderStatus }) {
  addressElem.innerText = userAddress;
  requestElem.innerText = request;
  orderStatusElem.innerText = orderStatus;

  if (orderStatus !== "상품 준비중") {
    checkoutButton.disabled = true;
  }
}

// 사용자 정보를 화면에 넣음
function displayUserDetails({ username, phone_number }) {
  receiverNameElem.innerText = username;
  receiverPhoneNumberElem.innerText = phone_number;
}

// 주소 수정 버튼 클릭 시 입력창으로 변경
function enableAddressEdit() {
  addressElem.innerHTML = `
    <input class="input" type="text" id="newAddress" value="${addressElem.innerText.trim()}">
    <button class="button is-info" id="confirmAddressButton">확인</button>
  `;
  document.querySelector("#confirmAddressButton").addEventListener("click", updateAddress);
}

// 주소를 업데이트하는 함수
async function updateAddress() {
  const newAddress = document.querySelector("#newAddress").value;
  if (!newAddress) {
    alert("새 주소를 입력하세요.");
    return;
  }

  try {
    console.log("Updating address to:", newAddress);
    // 서버에 새로운 주소를 업데이트하는 API 호출
    const response = await Api.put(`/orders/address/${orderId}`, { userAddress: newAddress });
    console.log("Response from update:", response);

    if (!response.ok) {
      throw new Error("Network response was not ok");
    }

    addressElem.innerHTML = `<p class="input userAddress is-static">${newAddress}</p>`;
    alert("주소가 성공적으로 업데이트되었습니다.");

    // 페이지 리다이렉트
    window.location.href = `/orders/detail?orderId=${orderId}`;

  } catch (error) {
    alert("주소가 성공적으로 업데이트되었습니다.");
    window.location.href = `/orders/detail?orderId=${orderId}`;
  }
}

// 주문 라인과 주문 라인북 상세 정보를 가져옴
async function loadOrderLineDetails(orderId) {
  try {
    const [orderLines, books] = await Promise.all([
      Api.get(`/orderLine/${orderId}`),
      Api.get("/api/books")
    ]);

    console.log("Order Line Data:", orderLines);
    console.log("Books Data:", books);

    let productsTitle = "";
    let totalAmount = 0;
    const deliveryFee = 3000; // 배송비
    const discountRate = 0.1; // 할인율

    for (const orderLine of orderLines) {
      for (const book of orderLine.orderLineBooks) {
        const bookData = books.find(b => b.id === book.bookId);
        if (bookData) {
          productsTitle += `${bookData.title} x ${book.quantity}\n`;
        } else {
          productsTitle += `알 수 없는 제목 x ${book.quantity}\n`;
        }
      }
      totalAmount += orderLine.price * orderLine.quantity;
    }

    productsTitleElem.innerText = productsTitle;
    deliveryFeeElem.innerText = addCommas(deliveryFee) + "원";
    discountRateElem.innerText = (discountRate * 100).toFixed(0) + "%";
    orderTotalElem.innerText = addCommas(totalAmount + deliveryFee - (totalAmount * discountRate)) + "원";

  } catch (error) {
    console.error("Error loading order line details:", error);
    alert("주문 라인 정보를 불러오는 중 오류가 발생했습니다.");
  }
}