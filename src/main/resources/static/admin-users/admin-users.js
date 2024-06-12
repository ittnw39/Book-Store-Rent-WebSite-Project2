// admin-users.js
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
let userEmailToDelete;
async function insertUsers() {
 const users = await Api.get("/admin/users/all");

 // 총 요약에 활용
 const summary = {
   usersCount: users.length,
   adminCount: users.filter(user => user.admin).length,
 };

 usersContainer.innerHTML = ''; // 기존 사용자 목록 초기화

 users.forEach(user => {
   const { email, username, admin, createdAt, phNum } = user;
   const date = createdAt;
   const sanitizedEmail = email.replace(/[^a-zA-Z0-9]/g, '');

   usersContainer.insertAdjacentHTML(
     "beforeend",
     `
       <div class="columns orders-item" id="user-${sanitizedEmail}">
         <div class="column is-2">${date}</div>
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
         <div class="column is-2">${phNum}</div>
       </div>
     `
   );

   // 요소 선택
   const roleSelectBox = document.querySelector(`#roleSelectBox-${sanitizedEmail}`);
   const deleteButton = document.querySelector(`#deleteButton-${sanitizedEmail}`);

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
    // api 요청
    const response = await Api.patch("/admin/users", data);

    // 선택한 옵션의 배경색 반영
    const index = roleSelectBox.selectedIndex;
    roleSelectBox.className = roleSelectBox[index].className;

    console.log(response); // 서버 응답 확인용 로그

    if (response.success) {
      alert(response.message);
    } else {
      throw new Error("권한 변경에 실패했습니다.");
    }
  } catch (err) {
    console.error(err);
    alert(`권한 변경 과정에서 오류가 발생하였습니다: ${err.message}`);

    // 오류 발생 시 이전 선택 옵션으로 되돌리기
    roleSelectBox.value = user.admin ? "ADMIN" : "USER";
    const index = roleSelectBox.selectedIndex;
    roleSelectBox.className = roleSelectBox[index].className;
  }
});

 //이벤트 - 삭제버튼 클릭 시 Modal 창 띄우고, 동시에, 전역변수에 해당 주문의 id 할당
   deleteButton.addEventListener("click", () => {
     userEmailToDelete = email;
     openModal();
   });
 });

 // 총 요약에 값 삽입
 usersCount.innerText = addCommas(summary.usersCount);
 adminCount.innerText = addCommas(summary.adminCount);
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