import { addCommas, checkAdmin, createNavbar } from "../useful-functions.js";
import * as Api from "../api.js";

// 요소(element), input 혹은 상수
const ordersCount = document.querySelector("#ordersCount");
const prepareCount = document.querySelector("#prepareCount");
const deliveryCount = document.querySelector("#deliveryCount");
const completeCount = document.querySelector("#completeCount");
const ordersContainer = document.querySelector("#ordersContainer");
const modal = document.querySelector("#modal");
const modalBackground = document.querySelector("#modalBackground");
const modalCloseButton = document.querySelector("#modalCloseButton");
const deleteCompleteButton = document.querySelector("#deleteCompleteButton");
const deleteCancelButton = document.querySelector("#deleteCancelButton");
const searchInput = document.querySelector('#searchInput');

const paginationList = document.querySelector("#paginationList");
const prevPageButton = document.querySelector("#prevPage");
const nextPageButton = document.querySelector("#nextPage");

let currentPage = 0;
const pageSize = 10; // 한 페이지에 표시할 주문 수
let totalPages = 1;
let orders = [];

checkAdmin();
addAllElements();
addAllEvents();

// 요소 삽입 함수들을 묶어주어서 코드를 깔끔하게 하는 역할임.
function addAllElements() {
  createNavbar();
  insertOrders();
}

// 여러 개의 addEventListener들을 묶어주어서 코드를 깔끔하게 하는 역할임.
function addAllEvents() {
  modalBackground.addEventListener("click", closeModal);
  modalCloseButton.addEventListener("click", closeModal);
  document.addEventListener("keydown", keyDownCloseModal);
  deleteCompleteButton.addEventListener("click", deleteOrderData);
  deleteCancelButton.addEventListener("click", cancelDelete);
  prevPageButton.addEventListener("click", () => changePage(currentPage - 1));
  nextPageButton.addEventListener("click", () => changePage(currentPage + 1));
  addStatusChangeEventListener(); // 추가
  searchInput.addEventListener('input', handleSearch);
}

// 검색 기능 구현
async function handleSearch() {
  const searchTerm = searchInput.value.trim();
  const filteredOrders = orders.filter(order =>
    String(order.id).includes(searchTerm)
  );
  renderOrders(filteredOrders);
  updateSummary(filteredOrders);
  updatePagination(filteredOrders.length);
}

// 페이지 로드 시 실행, 삭제할 주문 id를 전역변수로 관리함
let orderIdToDelete;
async function insertOrders() {
  try {
    const response = await fetch(
      `/admin/orders/all?page=${currentPage}&size=${pageSize}`
    );

    console.log("Response status:", response.status);
    console.log("Response headers:", response.headers);

    const responseText = await response.text();
    //        console.log('Response text:', responseText);

    if (!response.ok) {
      throw new Error("Network response was not ok");
    }

    // JSON으로 변환
    const data = JSON.parse(responseText);
    const { content: ordersData, totalPages: newTotalPages } = data;

    orders = ordersData; // 전역 변수에 주문 데이터 할당
    totalPages = newTotalPages;
    renderOrders(orders);
    updateSummary(orders);
    updatePagination(orders.length);
  } catch (err) {
    console.error("Error fetching orders:", err);
    alert("주문 데이터를 가져오는 중 오류가 발생했습니다.");
  }
}

// 주문 데이터를 HTML 요소로 변환하는 함수
function renderOrders(orders) {
  ordersContainer.innerHTML = ""; // 기존 주문 정보 초기화

  const headerHTML = `
    <div class="columns notification is-info is-light is-mobile orders-top">
      <div class="column is-2">날짜</div>
      <div class="column is-4">주문정보</div>
      <div class="column is-2">주문총액</div>
      <div class="column is-2">상태관리</div>
      <div class="column is-2">취소</div>
    </div>
  `;

  ordersContainer.innerHTML = headerHTML;

  orders.forEach((order) => {
    const { id, orderDate, orderStatus, totalAmount } = order;
    const date = new Date(orderDate).toLocaleDateString();

    const orderItemHTML = `
        <div class="columns notification">
          <div class="column is-2">${date}</div>
          <div class="column is-4">${id}</div>
          <div class="column is-2">${addCommas(Number(totalAmount))}원</div>
          <div class="column is-2">
            <div class="select">
              <select id="statusSelectBox-${id}">
                <option ${
                  orderStatus === "상품 준비중" ? "selected" : ""
                } value="상품 준비중">상품 준비중</option>
                <option ${
                  orderStatus === "상품 배송중" ? "selected" : ""
                } value="상품 배송중">상품 배송중</option>
                <option ${
                  orderStatus === "배송완료" ? "selected" : ""
                } value="배송완료">배송완료</option>
              </select>
            </div>
          </div>
          <div class="column is-2">
            <button class="button deleteButton" id=${id} >취소</button>
          </div>
        </div>
      `;

    ordersContainer.innerHTML += orderItemHTML;
  });

  // 삭제 버튼 이벤트 추가
  document.querySelectorAll(`.deleteButton`).forEach((el) => {
    el.addEventListener("click", function (e) {
      const id = e.target.id;
      console.log("id", id);
      orderIdToDelete = id;
      openModal();
    });
  });
}

function updateSummary(orders) {
  const summary = orders.reduce(
    (acc, order) => {
      acc.ordersCount += 1;

      if (order.orderStatus === "상품 준비중") {
        acc.prepareCount += 1;
      } else if (order.orderStatus === "상품 배송중") {
        acc.deliveryCount += 1;
      } else if (order.orderStatus === "배송완료") {
        acc.completeCount += 1;
      }

      return acc;
    },
    { ordersCount: 0, prepareCount: 0, deliveryCount: 0, completeCount: 0 }
  );

  ordersCount.innerText = addCommas(summary.ordersCount);
  prepareCount.innerText = addCommas(summary.prepareCount);
  deliveryCount.innerText = addCommas(summary.deliveryCount);
  completeCount.innerText = addCommas(summary.completeCount);
}

// 페이지 변경 함수
function changePage(page) {
  if (page < 0 || page >= totalPages) return;
  currentPage = page;
  insertOrders();
}

// 페이지 네비게이션 업데이트 함수
function updatePagination(totalItems) {
  const totalPages = Math.ceil(totalItems / pageSize);
  paginationList.innerHTML = "";

  for (let i = 0; i < totalPages; i++) {
    const pageItem = document.createElement("li");
    const pageLink = document.createElement("a");

    pageLink.classList.add("pagination-link");
    if (i === currentPage) {
      pageLink.classList.add("is-current");
    }
    pageLink.textContent = i + 1;
    pageLink.addEventListener("click", () => {
      currentPage = i;
      const startIndex = currentPage * pageSize;
      const endIndex = startIndex + pageSize;
      const ordersToShow = orders.slice(startIndex, endIndex);
      renderOrders(ordersToShow);
    });

    pageItem.appendChild(pageLink);
    paginationList.appendChild(pageItem);
  }

  prevPageButton.disabled = currentPage === 0;
  nextPageButton.disabled = currentPage === totalPages - 1;
}

// db에서 주문정보 삭제
async function deleteOrderData(e) {
  e.preventDefault();

  try {
    const orderId = orderIdToDelete;
    console.log(`Deleting order ${orderId}`);
    const response = await fetch(`/orders/${orderId}`, {
      method: "DELETE",
    });

    if (!response.ok) {
      throw new Error("Network response was not ok");
    }

    // 삭제 성공
    alert("주문 정보가 삭제되었습니다.");

    // 주문 정보 화면에서 제거
    const updatedOrders = orders.filter((order) => order.id !== orderId);
    renderOrders(updatedOrders);
    updateSummary(updatedOrders);

    // 전역변수 초기화
    orderIdToDelete = null;
    closeModal();
  } catch (err) {
    console.error(`Error deleting order ${orderIdToDelete}:`, err);
    alert(`주문정보 삭제 과정에서 오류가 발생하였습니다: ${err.message}`);
  }
}

// Modal 창에서 아니오 클릭할 시, 전역 변수를 다시 초기화함.
function cancelDelete() {
  orderIdToDelete = "";
  closeModal();
}

// Modal 창 열기
function openModal() {
  modal.classList.add("is-active");
}

// Modal 창 닫기
function closeModal() {
  modal.classList.remove("is-active");
}

// 키보드로 Modal 창 닫기
function keyDownCloseModal(e) {
  // Esc 키
  if (e.keyCode === 27) {
    closeModal();
  }
}

// 주문 상태 변경 이벤트 리스너 추가
function addStatusChangeEventListener() {
  ordersContainer.addEventListener("change", async (event) => {
    if (!event.target.matches("select")) return; // 선택박스가 아닌 경우 무시

    const selectBox = event.target;
    const orderId = selectBox.id.split("-")[1]; // 주문 ID 추출
    const newStatus = selectBox.value;

    try {
      const response = await fetch(`/admin/orders/${orderId}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ orderStatus: newStatus }),
      });

      if (!response.ok) {
        throw new Error("Network response was not ok");
      }

      const updatedOrder = await response.json();
      console.log("Updated order:", updatedOrder);

      // 주문 상태 업데이트 후 화면 갱신
      const updatedOrders = orders.map((order) =>
        order.id === orderId ? updatedOrder : order
      );
      renderOrders(updatedOrders);
      updateSummary(updatedOrders);
    } catch (error) {
      console.error(`Error updating status for order ${orderId}:`, error);
      window.location.href = "/admin/orders";
    }
  });
}