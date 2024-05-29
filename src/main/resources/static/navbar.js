export const createNavbar = () => {
  const pathname = window.location.pathname;

  switch (pathname) {
    case "/":
      addNavElements("admin register login account logout");
      break;
    case "/account/orders/":
      addNavElements("admin account logout");
      break;
    case "/account/security/":
      addNavElements("admin account logout");
      break;
    case "/account/signout/":
      addNavElements("admin account logout");
      break;
    case "/account/":
      addNavElements("admin logout");
      break;
    case "/admin/orders/":
      addNavElements("admin account logout");
      break;
    case "/admin/users/":
      addNavElements("admin account logout");
      break;
    case "/admin/":
      addNavElements("account logout");
      break;
    case "/cart/":
      addNavElements("admin register login account logout");
      break;
    case "/category/add/":
      addNavElements("admin account productAdd logout");
      break;
    case "/login/":
      addNavElements("register");
      break;
    case "/order/complete/":
      addNavElements("admin account logout");
      break;
    case "/order/":
      addNavElements("admin account logout");
      break;
    case "/product/add/":
      addNavElements("admin account logout");
      break;
    case "/product/detail/":
      addNavElements("admin register login account logout");
      break;
    case "/product/list/":
      addNavElements("admin register login account logout");
      break;
    case "/register/":
      addNavElements("login");
      break;

    default:
  }
};

// navbar ul 태그에, li 태그들을 삽입함)
const addNavElements = (keyString) => {
  const keys = keyString.split(" ");

  const container = document.querySelector("#navbar");
  const isLogin = sessionStorage.getItem("token") ? true : false;
  const isAdmin = sessionStorage.getItem("admin") ? true : false;

  // 로그인 안 된 상태에서만 보이게 될 navbar 요소들
  const itemsBeforeLogin = {
    register: '<li><a href="/register">회원가입</a></li>',
    login: '<li><a href="/login">로그인</a></li>',
  };

  // 로그인 완료된 상태에서만 보이게 될 navbar 요소들
  const itemsAfterLogin = {
    account: '<li><a href="/account">계정관리</a></li>',
    logout: '<li><a href="#" id="logout">로그아웃</a></li>',
    productAdd: '<li><a href="/product/add">제품 추가</a></li>',
    categoryAdd: '<li><a href="/category/add">카테고리 추가</a></li>',
  };

  const itemsForAdmin = {
    admin: '<li><a href="/admin">페이지관리</a></li>',
  };

  // 로그아웃 요소만 유일하게, 클릭 이벤트를 필요로 함 (나머지는 href로 충분함)
  const logoutScript = document.createElement("script");
  logoutScript.innerText = `
      const logoutElem = document.querySelector('#logout'); 
      
      if (logoutElem) {
        logoutElem.addEventListener('click', () => {
          sessionStorage.removeItem('token');
          sessionStorage.removeItem('admin');

          window.location.href = '/';
        });
      }
  `;

  let items = "";
  for (const key of keys) {
    if (isAdmin) {
      items += itemsForAdmin[key] ?? "";
    }
    if (isLogin) {
      items += itemsAfterLogin[key] ?? "";
    } else {
      items += itemsBeforeLogin[key] ?? "";
    }
  }

  // items에 쌓은 navbar 요소들 문자열을 html에 삽입함.
  container.insertAdjacentHTML("afterbegin", items);

  // insertAdjacentHTML 은 문자열 형태 script를 실행하지는 않음.
  // append, after 등의 함수로 script 객체 요소를 삽입해 주어야 실행함.
  container.after(logoutScript);
};
