import { addCommas, checkLogin, createNavbar } from "../../useful-functions.js";
import * as Api from "../../api.js";


// 요소(element), input 혹은 상수
const ordersContainer = document.querySelector("#ordersContainer");
const modal = document.querySelector("#modal");
const modalBackground = document.querySelector("#modalBackground");
const modalCloseButton = document.querySelector("#modalCloseButton");
const deleteCompleteButton = document.querySelector("#deleteCompleteButton");
const deleteCancelButton = document.querySelector("#deleteCancelButton");

const paginationList = document.querySelector("#paginationList");
const prevPageButton = document.querySelector("#prevPage");
const nextPageButton = document.querySelector("#nextPage");

let currentPage = 0;
const pageSize = 10; // 한 페이지에 표시할 주문 수
let totalPages = 1;

checkLogin();
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
}

// 페이지 로드 시 실행, 삭제할 주문 id를 전역변수로 관리함
let userId;
let orderIdToDelete;
async function insertOrders() {
  try {
    const userData = await Api.get("/users/data");
    console.log("API Response:", userData);
    const { id } = userData;

    if (!id) {
              throw new Error("API 응답에 id 값이 없습니다.");
        }

    userId = id;

    const response = await fetch(`/orders/${userId}?page=${currentPage}&size=${pageSize}`);

      console.log('Response status:', response.status);
      console.log('Response headers:', response.headers);

      const responseText = await response.text();
      console.log('Response text:', responseText);

      if (!response.ok) {
        throw new Error('Network response was not ok');
      }

      // JSON으로 변환
      const data = JSON.parse(responseText);
      const { content: orders, totalPages: newTotalPages } = data;

      totalPages = newTotalPages;
      renderOrders(orders);
      updatePagination();

    } catch (err) {
        console.error("Error fetching orders:", err);
        alert("주문 데이터를 가져오는 중 오류가 발생했습니다.");
      }
    }

    // 주문 데이터를 HTML 요소로 변환하는 함수
    function renderOrders(orders) {
      ordersContainer.innerHTML = `
        <div class="columns notification is-info is-light is-mobile orders-top">
          <div class="column is-2">날짜</div>
          <div class="column is-4">주문정보</div>
          <div class="column is-2">주문총액</div>
          <div class="column is-2">상태</div>
          <div class="column is-2">신청</div>
        </div>
      `;

      orders.forEach(order => {
        const { id, orderDate, orderStatus, totalAmount } = order;
        const date = new Date(orderDate).toLocaleDateString();

        const orderItemHTML = `
          <div class="columns notification" id="order-${id}">
            <div class="column is-2">${date}</div>
            <div class="column is-4">
                      <a href="/orders/detail?orderId=${id}" class="order-link">주문 번호 : ${id}</a>
            </div>
            <div class="column is-2">${addCommas(Number(totalAmount))}원</div>
            <div class="column is-2">${orderStatus}</div>
            <div class="column is-2">
              <button class="button is-danger deleteButton" id=${id} ${orderStatus !== "상품 준비중" ? "disabled" : ""}>취소</button>
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


// 페이지 변경 함수
function changePage(page) {
  if (page < 0 || page >= totalPages) return;
  currentPage = page;
  insertOrders();
}

// 페이지 네비게이션 업데이트 함수
function updatePagination() {
  paginationList.innerHTML = '';

  for (let i = 0; i < totalPages; i++) {
    const pageItem = document.createElement('li');
    const pageLink = document.createElement('a');

    pageLink.classList.add('pagination-link');
    if (i === currentPage) {
      pageLink.classList.add('is-current');
    }
    pageLink.textContent = i + 1;
    pageLink.addEventListener('click', () => changePage(i));

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
      console.log(`Deleting order ${orderIdToDelete}`);
      const response = await fetch(`/orders/${orderIdToDelete}`, {
        method: 'DELETE',
      });
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }

    // 삭제 성공
    alert("주문 정보가 삭제되었습니다.");

    // 삭제한 아이템 화면에서 지우기
    const deletedItem = document.querySelector(`#order-${orderIdToDelete}`);
    deletedItem.remove();

    // 전역변수 초기화
    orderIdToDelete = "";

    closeModal();
  } catch (err) {
    alert(`주문정보 삭제 과정에서 오류가 발생하였습니다: ${err}`);
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
