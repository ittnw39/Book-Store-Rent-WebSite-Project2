import * as Api from "../api.js";
import {
  blockIfLogin,
  getUrlParams,
  validateEmail,
  createNavbar,
} from "../useful-functions.js";

// 요소(element), input 혹은 상수
const emailInput = document.querySelector("#emailInput");
const passwordInput = document.querySelector("#passwordInput");
const submitButton = document.querySelector("#submitButton");

blockIfLogin();
addAllElements();
addAllEvents();

// html에 요소를 추가하는 함수들을 묶어주어서 코드를 깔끔하게 하는 역할임.
async function addAllElements() {
  createNavbar();
}

// 여러 개의 addEventListener들을 묶어주어서 코드를 깔끔하게 하는 역할임.
function addAllEvents() {
  submitButton.addEventListener("click", handleSubmit);
}

// 로그인 진행 //여기서부터 시작입니당 post 요청 출발
async function handleSubmit(e) {
  e.preventDefault();

  const email = emailInput.value;     //input 안에 email 값을 이메일이라는 변수 안에 넣으세요~~    html 에 선언되있음 똑같이 쓰면 됨 ㅇㅋ?
  const password = passwordInput.value;

  // 잘 입력했는지 확인
  const isEmailValid = validateEmail(email);     //validate라는 함수를 찾으세요 ~~ 찾고 그안에다가 이메일을 넣으세용 이메일 객체 검증?
  const isPasswordValid = password.length >= 4;  // 얘도 똑같음

  if (!isEmailValid || !isPasswordValid) {      //검증하고 주르륵 내려가서
    return alert(
      "비밀번호가 4글자 이상인지, 이메일 형태가 맞는지 확인해 주세요."
    );
  }

  // 로그인 api 요청
  try {
    const data = { email, password };   //아시 까 이메일이랑 ㅠㅐ스워드안에는 입력된 값이 있지? 그걸 데이터라는 변수 안에 다시 넣으세연~

    const result = await Api.post("/users/login", data); //폼안에서 쏴지는 데이터들을 가지고 post요청을 하겠다 postman 이랑 동일한 원리
 //await 은 변태다 묻지마라 / 일단은 무시
    const { message, roles, token } = result;     //던질 주소가 저 경로고 던지는 데이터가 이메일이랑 패스워드를 갖고있는 data란 친구


        if (token) {
          const expirationTime = Date.now() + 10 * 60 * 60 * 1000; // 10시간 후 만료
          sessionStorage.setItem("token", token);
          sessionStorage.setItem("expirationTime", expirationTime);
        }
 alert(message);

    // 로그인 성공
    // 역할 정보에 따라 admin 여부 판단
    const isAdmin = roles.includes("ADMIN");
        if (isAdmin) {
          sessionStorage.setItem("isAdmin", "true");
        } else {
        sessionStorage.removeItem("isAdmin");
        }

    // 기존 다른 페이지에서 이 로그인 페이지로 온 경우, 다시 돌아가도록 해 줌.
    const { previouspage } = getUrlParams();

    if (previouspage) {
      window.location.href = previouspage;

      return;
    }

    // 기존 다른 페이지가 없었던 경우, 그냥 기본 페이지로 이동
    window.location.href = "/";
  } catch (err) {
    console.error(err.stack);
    alert(`문제가 발생하였습니다. 확인 후 다시 시도해 주세요: ${err.message}`);
  }
  }

