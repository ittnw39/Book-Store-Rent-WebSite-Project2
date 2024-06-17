// 문자열+숫자로 이루어진 랜덤 5글자 반환
export const randomId = () => {
  return Math.random().toString(36).substring(2, 7);
};

// 이메일 형식인지 확인 (true 혹은 false 반환)
export const validateEmail = (email) => {
  return String(email)
    .toLowerCase()
    .match(
      /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
    );
};

// 주소창의 url로부터 params를 얻어 객체로 만듦
export const getUrlParams = () => {
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);

  const result = {};

  for (const [key, value] of urlParams) {
    result[key] = value;
  }

  return result;
};

// 숫자에 쉼표를 추가함. (10000 -> 10,000)
export const addCommas = (n) => {
  return n.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
};



// 로그인 성공 후 토큰을 sessionStorage에 저장하는 함수 추가
export const saveToken = (token) => {
  sessionStorage.setItem("token", token);
};

// 로그인 여부(토큰 존재 여부) 확인
export const checkLogin = () => {
  const token = sessionStorage.getItem("token");
  if (!token) {
    // 현재 페이지의 url 주소 추출하기
    const pathname = window.location.pathname;
    const search = window.location.search;

    // 로그인 후 다시 지금 페이지로 자동으로 돌아가도록 하기 위한 준비작업임.
//    window.location.replace(`/login?previouspage=${pathname + search}`);
  window.location.replace("/users/login");
  }
  };

  // 토큰의 유효성 검사
  export async function checkToken() {
   const token = sessionStorage.getItem("token");

   if (!token) {
     return false;
   }

   try {
     const res = await fetch("/users/check", {
       method: "POST",
       headers: {
         "Content-Type": "application/json",
         Authorization: `Bearer ${token}`,
       },
     });

     if (res.ok) {
       return true;
     } else {
       sessionStorage.removeItem("token");
       return false;
     }
   } catch (error) {
     console.error(error);
     return false;
   }
  }


  // 관리자 토큰 여부 확인
  export const checkAdmin = async () => {
      const token = sessionStorage.getItem("token");
      if (!token || token === "") {
          const pathname = window.location.pathname;
          const search = window.location.search;
          //window.location.replace(`/login?previouspage=${pathname + search}`);
          window.location.replace("/users/login");
          return;
      }

      try {
          const res = await fetch("/users/admin-check", {
              headers: {
                  Authorization: `Bearer ${token}`,
              },
          });

          if (res.status === 401) {
              // 토큰이 만료된 경우 로그아웃 처리 후 로그인 페이지로 이동
              sessionStorage.removeItem("token");
              sessionStorage.removeItem("isAdmin");
              alert("세션이 만료되었습니다. 다시 로그인해주세요.");
              window.location.replace("/users/login");
              return;
          }

          if (res.ok) {
              const { result } = await res.json();
              if (result === "success") {
                  //renderAdminPage();  //주석처리가 하니까 해결/
                  window.document.body.style.display = "block";
              } else {
                  alert("관리자 전용 페이지입니다.");
                  window.location.replace("/users/login");
              }
          } else {
              throw new Error("관리자 인증 실패");
          }
      } catch (error) {
          console.error(error);
          alert("관리자 인증 과정에서 오류가 발생했습니다.");
      }
  };


// 로그인 상태일 때에는 접근 불가한 페이지로 만듦. (회원가입 페이지 등)
export const blockIfLogin = () => {
  const token = sessionStorage.getItem("token");

  if (token) {
    alert("로그인 상태에서는 접근할 수 없는 페이지입니다.");
    window.location.replace("/");
  }
};

// 해당 주소로 이동하는 콜백함수를 반환함.
// 이벤트 핸들 함수로 쓰면 유용함
export const navigate = (pathname) => {
  return function () {
    window.location.href = pathname;
  };
};

// 13,000원, 2개 등의 문자열에서 쉼표, 글자 등 제외 후 숫자만 뺴냄
// 예시: 13,000원 -> 13000, 20,000개 -> 20000
export const convertToNumber = (string) => {
  return parseInt(string.replace(/(,|개|원)/g, ""));
};

// ms만큼 기다리게 함.
export const wait = (ms) => {
  return new Promise((r) => setTimeout(r, ms));
};

// 긴 문자열에서 뒷부분을 ..으로 바꿈
export const compressString = (string) => {
  if (string.length > 10) {
    return string.substring(0, 9) + "..";
  }
  return string;
};

// 주소에 특정 params가 없다면 잘못된 접근으로 하고 싶은 경우 사용.
export const checkUrlParams = (key) => {
  const { [key]: params } = getUrlParams();

  if (!params) {
    window.location.replace("/page-not-found");
  }
};

// 배열 혹은 객체에서 랜덤으로 1개 고름
export const randomPick = (items) => {
  const isArray = Array.isArray(items);

  // 배열인 경우
  if (isArray) {
    const randomIndex = [Math.floor(Math.random() * items.length)];

    return items[randomIndex];
  }

  // 객체인 경우
  const keys = Object.keys(items);
  const randomIndex = [Math.floor(Math.random() * keys.length)];
  const randomKey = keys[randomIndex];

  return items[randomKey];
};

// 주변 다른 파일 것도 여기서 일괄 export 함
export { createNavbar } from "./navbar.js";
