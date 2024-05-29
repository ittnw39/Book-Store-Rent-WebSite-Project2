import { addCommas, checkAdmin, createNavbar } from "../../useful-functions.js";
import * as Api from "../../api.js";

// 요소(element), input 혹은 상수
const usersCount = document.querySelector("#usersCount");
const adminCount = document.querySelector("#adminCount");
const usersContainer = document.querySelector("#usersContainer");
const modal = document.querySelector("#modal");
const modalBackground = document.querySelector("#modalBackground");
const modalCloseButton = document.querySelector("#modalCloseButton");
const deleteCompleteButton = document.querySelector("#deleteCompleteButton");
const deleteCancelButton = document.querySelector("#deleteCancelButton");

checkAdmin();
addAllElements();
addAllEvents();

// 요소 삽입 함수들을 묶어주어서 코드를 깔끔하게 하는 역할임.
function addAllElements() {
  createNavbar();
  insertUsers();
}

// 여러 개의 addEventListener들을 묶어주어서 코드를 깔끔하게 하는 역할임.
function addAllEvents() {
  modalBackground.addEventListener("click", closeModal);
  modalCloseButton.addEventListener("click", closeModal);
  document.addEventListener("keydown", keyDownCloseModal);
  deleteCompleteButton.addEventListener("click", deleteUserData);
  deleteCancelButton.addEventListener("click", cancelDelete);
}

// 페이지 로드 시 실행, 삭제할 회원 id를 전역변수로 관리함
let userIdToDelete;
async function insertUsers() {
  const users = await Api.get("/users/all");

  // 총 요약에 활용
  const summary = {
    usersCount: 0,
    adminCount: 0,
  };

  for (const user of users) {
    const { id, email, fullName, roles, createdAt } = user;
    const date = createdAt;

    summary.usersCount += 1;

    if (roles.includes('ADMIN')) {
      summary.adminCount += 1;
    }

    usersContainer.insertAdjacentHTML(
      "beforeend",
      `
        <div class="columns orders-item" id="user-${id}">
          <div class="column is-2">${date}</div>
          <div class="column is-2">${email}</div>
          <div class="column is-2">${fullName}</div>
          <div class="column is-2">
            <div class="select" >
              <select id="roleSelectBox-${id}">
                <option 
                  class="has-background-link-light has-text-link"
                  ${roles.includes('ADMIN') === false ? "selected" : ""} 
                  value="USER">
                  일반사용자
                </option>
                <option 
                  class="has-background-danger-light has-text-danger"
                  ${roles.includes('ADMIN') === true ? "selected" : ""} 
                  value="ADMIN">
                  관리자
                </option>
              </select>
            </div>
          </div>
          <div class="column is-2">
            <button class="button" id="deleteButton-${id}" >회원정보 삭제</button>
          </div>
        </div>
      `
    );

    // 요소 선택
    const roleSelectBox = document.querySelector(`#roleSelectBox-${id}`);
    const deleteButton = document.querySelector(`#deleteButton-${id}`);

    // 권한관리 박스에, 선택되어 있는 옵션의 배경색 반영
    const index = roleSelectBox.selectedIndex;
    roleSelectBox.className = roleSelectBox[index].className;

    // 이벤트 - 권한관리 박스 수정 시 바로 db 반영
    roleSelectBox.addEventListener("change", async () => {
      const newRole = roleSelectBox.value;
      const data = { roles: newRole };

      // 선택한 옵션의 배경색 반영
      const index = roleSelectBox.selectedIndex;
      roleSelectBox.className = roleSelectBox[index].className;

      // api 요청
      await Api.patch("/users", id, data);
    });

    // 이벤트 - 삭제버튼 클릭 시 Modal 창 띄우고, 동시에, 전역변수에 해당 주문의 id 할당
    deleteButton.addEventListener("click", () => {
      userIdToDelete = id;
      openModal();
    });
  }

  // 총 요약에 값 삽입
  usersCount.innerText = addCommas(summary.usersCount);
  adminCount.innerText = addCommas(summary.adminCount);
}

// db에서 회원정보 삭제
async function deleteUserData(e) {
  e.preventDefault();

  try {
    await Api.delete("/users", userIdToDelete);

    // 삭제 성공
    alert("회원 정보가 삭제되었습니다.");

    // 삭제한 아이템 화면에서 지우기
    const deletedItem = document.querySelector(`#user-${userIdToDelete}`);
    deletedItem.remove();

    // 전역변수 초기화
    userIdToDelete = "";

    closeModal();
  } catch (err) {
    alert(`회원정보 삭제 과정에서 오류가 발생하였습니다: ${err}`);
  }
}

// Modal 창에서 아니오 클릭할 시, 전역 변수를 다시 초기화함.
function cancelDelete() {
  userIdToDelete = "";
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
