// admin-users.js
import { addCommas, checkAdmin, createNavbar } from "../../useful-functions.js";
import * as Api from "../../api.js";

// Date 객체를 특정 형식의 문자열로 변환하는 함수
function formatDate(dateString) {
  const date = new Date(dateString);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  return `${year}-${month}-${day} ${hours}:${minutes}`;
}

// 요소(element), input 혹은 상수
const usersCount = document.querySelector("#usersCount");
const adminCount = document.querySelector("#adminCount");
const usersContainer = document.querySelector("#usersContainer");
const modal = document.querySelector("#modal");
const modalBackground = document.querySelector("#modalBackground");
const modalCloseButton = document.querySelector("#modalCloseButton");
const deleteCompleteButton = document.querySelector("#deleteCompleteButton");
const deleteCancelButton = document.querySelector("#deleteCancelButton");
const searchInput = document.querySelector('#searchInput');
const pagination = document.querySelector('#pagination');

// 비밀번호 변경 Modal 관련 요소들
const passwordModal = document.querySelector("#passwordModal");
const passwordModalBackground = document.querySelector("#passwordModalBackground");
const passwordModalCloseButton = document.querySelector("#passwordModalCloseButton");
const newPasswordInput = document.querySelector("#newPasswordInput");
const newPasswordConfirmInput = document.querySelector("#newPasswordConfirmInput");
const passwordChangeButton = document.querySelector("#passwordChangeButton");

// 페이지 로드 시 실행, 삭제할 회원 id를 전역변수로 관리함
let userEmailToDelete;
const itemsPerPage = 10; // 한 페이지당 보여줄 회원 수
let currentPage = 1; // 현재 페이지 번호
let users = []; // 전체 회원 데이터

checkAdmin();
addAllElements();
addAllEvents();

// 요소 삽입 함수들을 묶어주어서 코드를 깔끔하게 하는 역할임.
function addAllElements() {
  createNavbar();
  loadInitialData();
}

// 여러 개의 addEventListener들을 묶어주어서 코드를 깔끔하게 하는 역할임.
function addAllEvents() {
  modalBackground.addEventListener("click", closeModal);
  modalCloseButton.addEventListener("click", closeModal);
  document.addEventListener("keydown", keyDownCloseModal);
  deleteCompleteButton.addEventListener("click", deleteUserData);
  deleteCancelButton.addEventListener("click", cancelDelete);
  searchInput.addEventListener('input', handleSearch);

  // 비밀번호 변경 Modal 관련 이벤트 리스너 추가
  passwordModalBackground.addEventListener("click", closePasswordModal);
  passwordModalCloseButton.addEventListener("click", closePasswordModal);
  document.addEventListener("keydown", keyDownClosePasswordModal);
  passwordChangeButton.addEventListener("click", changePassword);
}

// 초기 데이터 로드 및 렌더링
async function loadInitialData() {
  users = await Api.get("/admin/users/all");
  renderUsers(users);


// 총회원수 및 관리자수 업데이트
  updateUserCount(users);
  updateAdminCount(users);
}

// 검색 기능 구현
async function handleSearch() {
  const searchTerm = searchInput.value.trim().toLowerCase();
  const filteredUsers = users.filter(user =>
    user.email.toLowerCase().includes(searchTerm)
  );
  renderUsers(filteredUsers);
}

// 회원 목록 렌더링
function renderUsers(usersToRender) {
  const startIndex = (currentPage - 1) * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  const usersToShow = usersToRender.slice(startIndex, endIndex);

  usersContainer.innerHTML = '';

  usersToShow.forEach(user => {
    const { email, username, admin, createdAt, phone_number } = user;
    const sanitizedEmail = email.replace(/[^a-zA-Z0-9]/g, '');

    usersContainer.insertAdjacentHTML(
      "beforeend",
      `
        <div class="columns orders-item" id="user-${sanitizedEmail}">
          <div class="column is-2">${formatDate(createdAt)}</div>
          <div class="column is-2">${email}</div>
          <div class="column is-2">${username}</div>
          <div class="column is-2">
            <div class="select">
              <select id="roleSelectBox-${sanitizedEmail}">
                <option
                  class="has-background-link-light has-text-link"
                  ${!admin ? "selected" : ""}
                  value="USER">
                  일반사용자
                </option>
                <option
                  class="has-background-danger-light has-text-danger"
                  ${admin ? "selected" : ""}
                  value="ADMIN">
                  관리자
                </option>
              </select>
            </div>
          </div>
          <div class="column is-2">
            <button class="button" id="deleteButton-${sanitizedEmail}">회원정보 삭제</button>
          </div>
          <div class="column is-2">${phone_number || '-'}</div>
          <div class="column is-2">
            <button class="button" id="passwordButton-${sanitizedEmail}">비밀번호 변경</button>
          </div>
        </div>
      `
    );

    // 요소 선택
    const roleSelectBox = document.querySelector(`#roleSelectBox-${sanitizedEmail}`);
    const deleteButton = document.querySelector(`#deleteButton-${sanitizedEmail}`);
    const passwordButton = document.querySelector(`#passwordButton-${sanitizedEmail}`);

    // 권한관리 박스에, 선택되어 있는 옵션의 배경색 반영
    const index = roleSelectBox.selectedIndex;
    roleSelectBox.className = roleSelectBox[index].className;

    // 이벤트 - 권한관리 박스 수정 시 바로 db 반영
    roleSelectBox.addEventListener("change", async () => {
      const newRole = roleSelectBox.value === "ADMIN";
      const data = {
        email: email,
        admin: newRole
      };

      try {
        const response = await Api.patch("/admin/users", "", data);
        const index = roleSelectBox.selectedIndex;
        roleSelectBox.className = roleSelectBox[index].className;

        console.log(response);

        if (response.message === '권한이 성공적으로 변경되었습니다.') {
          alert(response.message);
        } else {
          throw new Error(response.message || "권한 변경에 실패했습니다.");
        }
      } catch (err) {
        if (err.message !== '권한이 성공적으로 변경되었습니다.') {
          console.error(err);
          alert(`권한 변경 과정에서 오류가 발생하였습니다: ${err.message}`);
        }
      }
    });

    // 이벤트 - 삭제버튼 클릭 시 Modal 창 띄우고, 동시에, 전역변수에 해당 주문의 id 할당
    deleteButton.addEventListener("click", () => {
      userEmailToDelete = email;
      openModal();
    });

    // 이벤트 - 비밀번호 변경 버튼 클릭 시 Modal 창 열기
    passwordButton.addEventListener("click", () => {
      openPasswordModal(email);
    });
  });

  renderPagination(usersToRender.length);
}

// 페이지네이션 렌더링
function renderPagination(totalItems) {
  const totalPages = Math.ceil(totalItems / itemsPerPage);
  pagination.innerHTML = '';

  for (let i = 1; i <= totalPages; i++) {
    const pageItem = document.createElement('li');
    pageItem.classList.add('pagination-link');
    if (i === currentPage) {
      pageItem.classList.add('is-current');
    }
    pageItem.textContent = i;
    pageItem.addEventListener('click', () => {
      currentPage = i;
      renderUsers(users);
    });
    pagination.appendChild(pageItem);
  }
}

// db에서 회원정보 삭제
async function deleteUserData(e) {
  e.preventDefault();

  try {
    const response = await Api.delete(`/admin/users/${userEmailToDelete}`);

    // 삭제 성공
    alert(response.message);

    // 삭제한 아이템 화면에서 지우기
    const deletedItem = document.querySelector(`#user-${userEmailToDelete.replace(/[^a-zA-Z0-9]/g, '')}`);
    deletedItem.remove();

    // 전역변수 초기화
    userEmailToDelete = "";

    // 삭제 후 카운트 업데이트
    updateUserCount();
    updateAdminCount();
  } catch (err) {
    alert(`회원정보 삭제 과정에서 오류가 발생하였습니다: ${err}`);
  } finally {
    closeModal(); // 삭제 요청 처리 후 모달창 닫기
  }
}

// Modal 창에서 아니오 클릭할 시, 전역 변수를 다시 초기화함.
function cancelDelete() {
  userEmailToDelete = "";
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

// 유저 수 업데이트
function updateUserCount() {
  const userCount = document.querySelectorAll(".orders-item").length;
  usersCount.innerText = addCommas(userCount);
}

// 관리자 수 업데이트
function updateAdminCount() {
  const adminElements = document.querySelectorAll("option[value='ADMIN']:checked");
  const adminCountValue = adminElements.length;
  adminCount.innerText = addCommas(adminCountValue);
}

// 비밀번호 변경 Modal 창 열기
function openPasswordModal(email) {
  passwordModal.classList.add("is-active");
  passwordModal.dataset.email = email;
}

// 비밀번호 변경 Modal 창 닫기
function closePasswordModal() {
  passwordModal.classList.remove("is-active");
  passwordModal.dataset.email = "";
  newPasswordInput.value = "";
  newPasswordConfirmInput.value = "";
}

// 비밀번호 변경
async function changePassword() {
  const email = passwordModal.dataset.email;
  const newPassword = newPasswordInput.value;
  const passwordConfirm = newPasswordConfirmInput.value;

  if (newPassword !== passwordConfirm) {
    alert("새 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
    return;
  }

  if (newPassword.length < 4) {
    alert("비밀번호는 4자리 이상이어야 합니다.");
    return;
  }

  try {
    const data = {
      email: email,
      password: newPassword,
      passwordConfirm: passwordConfirm
    };

    const response = await Api.patch("/admin/users/password", "", data);

    alert("비밀번호가 성공적으로 변경되었습니다.");
    closePasswordModal();
  } catch (err) {
    console.error(err);
    alert("비밀번호가 성공적으로 변경되었습니다.");
  }
}

// 키보드로 비밀번호 변경 Modal 창 닫기
function keyDownClosePasswordModal(e) {
  // Esc 키
  if (e.keyCode === 27) {
    closePasswordModal();
  }
}