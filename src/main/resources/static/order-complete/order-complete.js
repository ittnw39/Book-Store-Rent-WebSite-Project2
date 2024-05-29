import { checkLogin, navigate, createNavbar } from "../../useful-functions.js";

// 요소(element), input 혹은 상수
const orderDetailButton = document.querySelector("#orderDetailButton");
const shoppingButton = document.querySelector("#shoppingButton");

checkLogin();
addAllElements();
addAllEvents();

// html에 요소를 추가하는 함수들을 묶어주어서 코드를 깔끔하게 하는 역할임.
function addAllElements() {
  createNavbar();
}

// addEventListener들을 묶어주어서 코드를 깔끔하게 하는 역할임.
function addAllEvents() {
  orderDetailButton.addEventListener("click", navigate("/account/orders"));
  shoppingButton.addEventListener("click", navigate("/"));
}
