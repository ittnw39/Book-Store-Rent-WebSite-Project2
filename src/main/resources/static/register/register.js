import * as Api from "../api.js";
import { validateEmail, createNavbar } from "../useful-functions.js";

// 요소(element), input 혹은 상수
const fullNameInput = document.querySelector("#fullNameInput");
const emailInput = document.querySelector("#emailInput");
const passwordInput = document.querySelector("#passwordInput");
const passwordConfirmInput = document.querySelector("#passwordConfirmInput");
const phone_numberInput = document.querySelector("#phone_numberInput");
const submitButton = document.querySelector("#submitButton");

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

// 회원가입 진행
async function handleSubmit(e) {
  e.preventDefault();

  const username = fullNameInput.value;
  const email = emailInput.value;
  const password = passwordInput.value;
  const passwordConfirm = passwordConfirmInput.value;
  const phone_number = phone_numberInput.value;

  // 잘 입력했는지 확인
  const isFullNameValid = username.length >= 2;
  const isEmailValid = validateEmail(email);
  const isPasswordValid = password.length >= 6;
  const isPasswordSame = password === passwordConfirm;
  const isPhone_numberValid = phone_number.length >= 9;

  if (!isFullNameValid || !isPasswordValid) {
    return alert("이름은 2글자 이상, 비밀번호는 6글자 이상이어야 합니다.");
  }

  if (!isEmailValid) {
    return alert("이메일 형식이 맞지 않습니다.");
  }

  if (!isPasswordSame) {
    return alert("비밀번호가 일치하지 않습니다.");
  }

  // 비밀번호 추가 유효성 검사
  if (!isPasswordSecure(password)) {
    alert('비밀번호는 최소 8자 이상, 영문자/숫자/특수문자 중 2종류 이상을 포함해야 합니다.');
    return;
  }

  // 회원가입 api 요청

try {
    const data = { username, email, password,passwordConfirm ,phone_number};

    await Api.post("/users/register", data);

    alert(`정상적으로 회원가입되었습니다.`);
    // 로그인 페이지 이동
    window.location.href = "/users/login";
  } catch (err) {
    console.error(err.stack);
    alert(`문제가 발생하였습니다. 확인 후 다시 시도해 주세요: ${err.message}`);
  }

  // 비밀번호 보안 강도 검사
  function isPasswordSecure(password) {
    // 최소 길이 8자 이상
    if (password.length < 8) {
      return false;
    }

    let hasUppercase = false;
    let hasLowercase = false;
    let hasNumber = false;
    let hasSpecialChar = false;

    for (let i = 0; i < password.length; i++) {
      const char = password[i];

      if (/[A-Z]/.test(char)) {
        hasUppercase = true;
      } else if (/[a-z]/.test(char)) {
        hasLowercase = true;
      } else if (/\d/.test(char)) {
        hasNumber = true;
      } else if (/[!@#$%^&*(),.?":{}|<>]/i.test(char)) {
        hasSpecialChar = true;
      }
    }

    // 영문자/숫자/특수문자 중 2종류 이상 포함 여부
    return (hasUppercase || hasLowercase) && (hasNumber || hasSpecialChar);
  }

}
