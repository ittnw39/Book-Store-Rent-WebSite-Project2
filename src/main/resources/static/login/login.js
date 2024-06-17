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

//// 네이버 로그인 버튼 클릭 시 동작
async function handleNaverCallback() {
  try {
    const response = await fetch('/oauth2/callback', {
      credentials: 'include' // 쿠키 전송을 위해 필요
    });

    const cookies = response.headers.get('Set-Cookie');
    if (cookies) {
      const tokenCookie = cookies.split(';').find(cookie => cookie.startsWith('token='));
      if (tokenCookie) {
        const token = tokenCookie.split('=')[1];
        sessionStorage.setItem('token', token);
      }
    }

    // 로그인 완료 후 처리
    // ...
  } catch (error) {
    console.error('Naver login failed:', error);
  }
}


// 로그인 진행
async function handleSubmit(e) {
  e.preventDefault();

  const email = emailInput.value;
  const password = passwordInput.value;

  // 잘 입력했는지 확인
  const isEmailValid = validateEmail(email);
  const isPasswordValid = password.length >= 4;

  if (!isEmailValid || !isPasswordValid) {
    return alert(
      "비밀번호가 4글자 이상인지, 이메일 형태가 맞는지 확인해 주세요."
    );
  }

  // 로그인 api 요청
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

