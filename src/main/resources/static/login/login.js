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


// 로그인 된 상태라면 페이지 접근을 막는 함수
blockIfLogin();
addAllElements();
addAllEvents();

// HTML에 요소를 추가하는 함수들을 묶어주어서 코드를 깔끔하게 하는 역할
async function addAllElements() {
  createNavbar();
}

// 여러 개의 addEventListener들을 묶어주어서 코드를 깔끔하게 하는 역할
function addAllEvents() {
  submitButton.addEventListener("click", handleSubmit);
}


// 네이버 OAuth2 콜백을 처리하는 함수
async function handleNaverCallback() {
  const urlParams = new URLSearchParams(window.location.search);
  const code = urlParams.get('code');
  const state = urlParams.get('state');

  if (code && state) {
    try {
      const response = await fetch(`/oauth2/callback/naver?code=${code}&state=${state}`);
      if (response.ok) {
        const data = await response.json();
        const token = data.token;

        if (token) {
          sessionStorage.setItem("token", token);
          window.opener.postMessage({ token }, '*');
          window.close();
        } else {
          console.error('No token received:', data);
        }
      } else {
        console.error('Naver login failed:', response.status);
      }
    } catch (error) {
      console.error('Error during OAuth2 callback handling:', error);
    }
  }
}

// 이메일과 비밀번호를 사용한 로그인 요청을 처리하는 함수
async function handleSubmit(event) {
  event.preventDefault();

  const email = emailInput.value;
  const password = passwordInput.value;

  if (!validateEmail(email)) {
    alert('이메일 형식이 맞지 않습니다.');
    return;
  }

  if (password.length < 6) {
    alert('비밀번호는 최소 6자 이상이어야 합니다.');
    return;
  }

  try {
    const data = { email, password };
    const result = await Api.post("/users/login", data);
    const { message, roles, token } = result;

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
