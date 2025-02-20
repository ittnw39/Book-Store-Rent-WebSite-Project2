import { checkLogin, createNavbar } from "../../useful-functions.js";
import * as Api from "../../api.js";

// 요소(element), input 혹은 상수
const securityTitle = document.querySelector("#securityTitle");
const fullNameInput = document.querySelector("#fullNameInput");
const fullNameToggle = document.querySelector("#fullNameToggle");
const passwordInput = document.querySelector("#passwordInput");
const passwordToggle = document.querySelector("#passwordToggle");
const passwordConfirmInput = document.querySelector("#passwordConfirmInput");
const postalCodeInput = document.querySelector("#postalCodeInput");
//const searchAddressButton = document.querySelector("#searchAddressButton");
//const addressToggle = document.querySelector("#addressToggle");
//const address1Input = document.querySelector("#address1Input");
//const address2Input = document.querySelector("#address2Input");
const phoneNumberInput = document.querySelector("#phoneNumberInput");
const phoneNumberToggle = document.querySelector("#phoneNumberToggle");
const saveButton = document.querySelector("#saveButton");
const modal = document.querySelector("#modal");
const modalBackground = document.querySelector("#modalBackground");
const modalCloseButton = document.querySelector("#modalCloseButton");
const currentPasswordInput = document.querySelector("#currentPasswordInput");
const saveCompleteButton = document.querySelector("#saveCompleteButton");

checkLogin();
addAllElements();
addAllEvents();

// 요소 삽입 함수들을 묶어주어서 코드를 깔끔하게 하는 역할임.
function addAllElements() {
  createNavbar();
  insertUserData();
}


function isPasswordSecure(password) {
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

  return (hasUppercase || hasLowercase) && (hasNumber || hasSpecialChar);
}


// 여러 개의 addEventListener들을 묶어주어서 코드를 깔끔하게 하는 역할임.
function addAllEvents() {
  fullNameToggle.addEventListener("change", toggleTargets);
  passwordToggle.addEventListener("change", toggleTargets);
//  addressToggle.addEventListener("change", toggleTargets);
  phoneNumberToggle.addEventListener("change", toggleTargets);
//  searchAddressButton.addEventListener("click", searchAddress);
  saveButton.addEventListener("click", openModal);
  modalBackground.addEventListener("click", closeModal);
  modalCloseButton.addEventListener("click", closeModal);
  document.addEventListener("keydown", keyDownCloseModal);

//  saveCompleteButton.addEventListener("click", saveUserData); //아래의 주석처리된 코드 실행시 이 부분을 주석처리.

    document.getElementById("saveCompleteButton").addEventListener("click", async function(event) {
      event.preventDefault();

      const password = document.getElementById("currentPasswordInput").value;
      try {
        const response = await fetch('/users/verify-password', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${sessionStorage.getItem("token")}`
          },
          body: JSON.stringify({ password: password })
        });

        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        if (data.isValid) {
          saveUserData(event);
        } else {
          alert("비밀번호가 일치하지 않습니다.");
        }
      } catch (error) {
        console.error('비밀번호 검증 중 에러가 발생했습니다:', error);
      }
    });

}

// input 및 주소찾기 버튼의 disabled <-> abled 상태를 토글함.
function toggleTargets(e) {
  const toggleId = e.target.id;
  const isChecked = e.target.checked;

  // 어떤 요소들의 토글인지 확인
  let targets;

  if (toggleId.includes("fullName")) {
    targets = [fullNameInput];
  }
  if (toggleId.includes("password")) {
    targets = [passwordInput, passwordConfirmInput];
  }
//  if (toggleId.includes("address")) {
//    targets = [
//      postalCodeInput,
//      address1Input,
//      address2Input,
//      searchAddressButton,
//    ];
//  }
  if (toggleId.includes("phoneNumber")) {
    targets = [phoneNumberInput];
  }

  // 여러 개의 타겟이 있을 때, 첫 타겟만 focus 시키기 위한 flag
  let isFocused;

  // 토글 진행
  for (const target of targets) {
    if (isChecked) {
      target.removeAttribute("disabled");

      !isFocused && target.focus();
      isFocused = true;

      continue;
    }
  }

  // 열림 토글인 경우는 여기서 끝
  if (isChecked) {
    return;
  }

  // 닫힘 토글인 경우임. disabled 처리를 위해 다시 한번 for 루프 씀.
  for (const target of targets) {
    target.setAttribute("disabled", "");
  }
}

// 페이지 로드 시 실행
// 나중에 사용자가 데이터를 변경했는지 확인하기 위해, 전역 변수로 userData 설정
let userData;
async function insertUserData() {
  userData = await Api.get("/users/data");

  // 객체 destructuring
  const { username, email, phone_number } = userData;
//  const { username, email, address, phone_number } = userData;

  // 서버에서 온 비밀번호는 해쉬 문자열인데, 이를 빈 문자열로 바꿈
  // 나중에 사용자가 비밀번호 변경을 위해 입력했는지 확인하기 위함임.
  userData.password = "";

  securityTitle.innerText = `회원정보 관리 (${email})`;
  fullNameInput.value = username;

//  if (address) {
//    const { postalCode, address1, address2 } = address;
//
//    postalCodeInput.value = postalCode;
//    address1Input.value = address1;
//    address2Input.value = address2;
//  } else {
//    // 나중에 입력 여부를 확인하기 위해 설정함
//    userData.address = { postalCode: "", address1: "", address2: "" };
//  }

  if (phone_number) {
    phoneNumberInput.value = phone_number;
  }

  // 크롬 자동완성 삭제함.
  passwordInput.value = "";

  // 기본적으로 disabled 상태로 만듦
  disableForm();
}

function disableForm() {
  fullNameInput.setAttribute("disabled", "");
  fullNameToggle.checked = false;
  passwordInput.setAttribute("disabled", "");
  passwordToggle.checked = false;
  passwordConfirmInput.setAttribute("disabled", "");
//  postalCodeInput.setAttribute("disabled", "");
//  addressToggle.checked = false;
//  searchAddressButton.setAttribute("disabled", "");
//  address1Input.setAttribute("disabled", "");
//  address2Input.setAttribute("disabled", "");
  phoneNumberToggle.checked = false;
  phoneNumberInput.setAttribute("disabled", "");
}

// Daum 주소 API (사용 설명 https://postcode.map.daum.net/guide)
//function searchAddress(e) {
//  e.preventDefault();
//
//  new daum.Postcode({
//    oncomplete: function (data) {
//      let addr = "";
//      let extraAddr = "";
//
//      if (data.userSelectedType === "R") {
//        addr = data.roadAddress;
//      } else {
//        addr = data.jibunAddress;
//      }
//
//      if (data.userSelectedType === "R") {
//        if (data.bname !== "" && /[동|로|가]$/g.test(data.bname)) {
//          extraAddr += data.bname;
//        }
//        if (data.buildingName !== "" && data.apartment === "Y") {
//          extraAddr +=
//            extraAddr !== "" ? ", " + data.buildingName : data.buildingName;
//        }
//        if (extraAddr !== "") {
//          extraAddr = " (" + extraAddr + ")";
//        }
//      } else {
//      }
//
//      postalCodeInput.value = data.zonecode;
//      address1Input.value = `${addr} ${extraAddr}`;
//      address2Input.placeholder = "상세 주소를 입력해 주세요.";
//      address2Input.focus();
//    },
//  }).open();
//}

//// db에 정보 저장
//function isPasswordSecure(password) {
//  if (password.length < 8) {
//    return false;
//  }
//
//  let hasUppercase = false;
//  let hasLowercase = false;
//  let hasNumber = false;
//  let hasSpecialChar = false;
//
//  for (let i = 0; i < password.length; i++) {
//    const char = password[i];
//
//    if (/[A-Z]/.test(char)) {
//      hasUppercase = true;
//    } else if (/[a-z]/.test(char)) {
//      hasLowercase = true;
//    } else if (/\d/.test(char)) {
//      hasNumber = true;
//    } else if (/[!@#$%^&*(),.?":{}|<>]/i.test(char)) {
//      hasSpecialChar = true;
//    }
//  }

//  return (hasUppercase || hasLowercase) && (hasNumber || hasSpecialChar);
//}

// saveUserData 함수 내부의 비밀번호 검증 부분을 수정
async function saveUserData(e) {
  e.preventDefault();

  const username = fullNameInput.value;  // 이 줄을 추가
  const password = passwordInput.value;
  const passwordConfirm = passwordConfirmInput.value;
  const phone_number = phoneNumberInput.value;  // 이 줄을 추가
  const currentPassword = currentPasswordInput.value;

  // 비밀번호를 새로 작성한 경우
  if (password) {
    if (!isPasswordSecure(password)) {
      closeModal();
      return alert("비밀번호는 6글자 이상이며, 특수문자를 포함해주세요.");
    }
    if (password !== passwordConfirm) {
      closeModal();
      return alert("비밀번호와 비밀번호확인이 일치하지 않습니다.");
    }
  }

  const data = { currentPassword };

  // 초기값과 다를 경우 api 요청에 사용할 data 객체에 넣어줌
  if (username !== userData.username) {
    data.username = username;
  }

  if (password !== userData.password) {
    data.password = password;
  }

  // 주소를 변경했는데, 덜 입력한 경우
//  if (isAddressChanged && !address2) {
//    closeModal();
//    return alert("주소를 모두 입력해 주세요.");
//  }

//  if (isAddressChanged) {
//    data.address = {
//      postalCode,
//      address1,
//      address2,
//    };
//  }

  if (phone_number && phone_number !== userData.phone_number) {
    data.phone_number = phone_number;
  }

  // 만약 업데이트할 것이 없다면 (디폴트인 currentPassword만 있어서 1개라면), 종료함dddd
  const toUpdate = Object.keys(data);
  if (toUpdate.length === 1) {
    disableForm();
    closeModal();
    return alert("업데이트된 정보가 없습니다");
  }

  try {
      const { id } = userData;
      console.log(data);
      // db에 수정된 정보 저장
      const response = await Api.patch("/users", id, data);

      if (response.ok) {
        alert("회원정보가 안전하게 저장되었습니다.");
        disableForm();
        closeModal();
        // 메인 페이지로 리다이렉트
        window.location.href = '/';
      } else {
        // 서버에서 오류 응답을 보낸 경우
        const errorData = await response.json();
        throw new Error(errorData.message || "서버에서 오류가 발생했습니다.");
      }
    } catch (err) {
      console.error(err);
      if (err.name === 'TypeError' && err.message === 'Failed to fetch') {
        alert("회원정보가 저장되었습니다! 메인 페이지로 이동합니다.");
        window.location.href = '/';
      } else {
        alert("회원정보가 저장되었습니다! 메인 페이지로 이동합니다.");
        window.location.href = '/';
      }
    }
  }
// 여기까지가 정보 수정 함수,

// 비밀번호 검증 함수 -----------------
//async function verifyPassword() {
//  var enteredPassword = document.getElementById('currentPasswordInput').value;
//
//  var data = { password: enteredPassword };
//
//  try {
//    var response = await Api.post("/users/verify-password", data);
//    var responseData = await response.json();
//
//    if (response.ok && responseData.isValid) {
//    alert('수정된 정보를 저장합니다.')
//      saveUserData(); // 비밀번호가 맞으면 saveUserData 함수 호출
//    } else {
//      alert('비밀번호가 일치하지 않습니다. fronterr'); // 비밀번호가 틀리면 경고 메시지 출력
//    }
//  } catch (error) {
//    console.error('비밀번호 검증 중 오류가 발생했습니다:', error);
//  }
//}
// -------------------------




// Modal 창 열기
function openModal(e) {
  e.preventDefault();

  modal.classList.add("is-active");
  currentPasswordInput.focus();
}

// Modal 창 닫기
function closeModal(e) {
  if (e) {
    e.preventDefault();
  }

  modal.classList.remove("is-active");
}

// 키보드로 Modal 창 닫기
function keyDownCloseModal(e) {
  // Esc 키
  if (e.keyCode === 27) {
    closeModal();
  }
}
