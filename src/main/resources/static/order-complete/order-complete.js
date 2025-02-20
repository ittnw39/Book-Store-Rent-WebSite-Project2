import * as Api from "../api.js";
import { checkLogin, navigate, createNavbar } from "../../useful-functions.js";

// 요소(element), input 혹은 상수
const orderDetailButton = document.querySelector("#orderDetailButton");
const shoppingButton = document.querySelector("#shoppingButton");

checkLogin();
addAllElements();
addAllEvents();

let globalUserId = null; // 페이지 전역에서 사용할 userId

async function insertUserData() {
  const userData = await Api.get("/users/data");
    const { id } = userData;

    if (id) {
      globalUserId = id;
      orderDetailButton.disabled = false; // 데이터 로드 후 버튼 활성화
      console.log("User ID set to:", globalUserId); // 로그로 ID 확인
    } else {
      console.error("No user ID found");
    }
}

// html에 요소를 추가하는 함수들을 묶어주어서 코드를 깔끔하게 하는 역할임.
function addAllElements() {
  createNavbar();
  insertUserData();
}

// addEventListener들을 묶어주어서 코드를 깔끔하게 하는 역할임.
function addAllEvents() {
  orderDetailButton.addEventListener("click", function() {
      if (!globalUserId) {
          alert('로그인이 필요합니다.');
          navigate('/login')();
          return;
      }
      navigate("/orders/account")();
  });

  shoppingButton.addEventListener("click", navigate("/"));
}
