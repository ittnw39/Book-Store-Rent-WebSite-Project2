import * as Api from "../api.js";
import {
  checkLogin,
  addCommas,
  convertToNumber,
  navigate,
  randomPick,
  createNavbar,
} from "../useful-functions.js";

// 요소(element), input 혹은 상수
const subtitleCart = document.querySelector("#subtitleCart");
const receiverNameInput = document.querySelector("#receiverName");
const receiverPhoneNumberInput = document.querySelector("#receiverPhoneNumber");
const postalCodeInput = document.querySelector("#postalCode");
const searchAddressButton = document.querySelector("#searchAddressButton");
const address1Input = document.querySelector("#address1");
const address2Input = document.querySelector("#address2");
const requestSelectBox = document.querySelector("#requestSelectBox");
const customRequestContainer = document.querySelector(
  "#customRequestContainer"
);
const customRequestInput = document.querySelector("#customRequest");
const productsTitleElem = document.querySelector("#productsTitle");
const productsTotalElem = document.querySelector("#productsTotal");
const deliveryFeeElem = document.querySelector("#deliveryFee");
const discountRate = document.querySelector("#discountRate");
const orderTotalElem = document.querySelector("#orderTotal");
const checkoutButton = document.querySelector("#checkoutButton");

const requestOption = {
  1: "직접 수령하겠습니다.",
  2: "배송 전 연락바랍니다.",
  3: "부재 시 경비실에 맡겨주세요.",
  4: "부재 시 문 앞에 놓아주세요.",
  5: "부재 시 택배함에 넣어주세요.",
  6: "직접 입력",
};

checkLogin();
addAllElements();
addAllEvents();
insertUserData();

// html에 요소를 추가하는 함수들을 묶어주어서 코드를 깔끔하게 하는 역할임.
function addAllElements() {
  createNavbar();
  insertOrderSummary();
}

// addEventListener들을 묶어주어서 코드를 깔끔하게 하는 역할임.
function addAllEvents() {
  subtitleCart.addEventListener("click", navigate("/cart"));
  searchAddressButton.addEventListener("click", searchAddress);
  requestSelectBox.addEventListener("change", handleRequestChange);
  checkoutButton.addEventListener("click", doCheckout);
}

// Daum 주소 API (사용 설명 https://postcode.map.daum.net/guide)
function searchAddress() {
  new daum.Postcode({
    oncomplete: function (data) {
      let addr = "";
      let extraAddr = "";

      if (data.userSelectedType === "R") {
        addr = data.roadAddress;
      } else {
        addr = data.jibunAddress;
      }

      if (data.userSelectedType === "R") {
        if (data.bname !== "" && /[동|로|가]$/g.test(data.bname)) {
          extraAddr += data.bname;
        }
        if (data.buildingName !== "" && data.apartment === "Y") {
          extraAddr +=
            extraAddr !== "" ? ", " + data.buildingName : data.buildingName;
        }
        if (extraAddr !== "") {
          extraAddr = " (" + extraAddr + ")";
        }
      } else {
      }

      postalCodeInput.value = data.zonecode;
      address1Input.value = `${addr} ${extraAddr}`;
      address2Input.placeholder = "상세 주소를 입력해 주세요.";
      address2Input.focus();
    },
  }).open();
}

let globalUserId; // 페이지 전역에서 사용할 userId
let userData;
async function insertUserData() {
  try {
    //토큰 검증 - 클라이언트에서 요청
    const token = sessionStorage.getItem("token");

    if (!token) {
      throw new Error("토큰이 없습니다. 로그인이 필요합니다.");
    }

    const userData = await Api.get("/users/data");
    console.log("API Response:", userData);
    const { id, username, phone_number } = userData;


    if (!id) {
          throw new Error("API 응답에 id 값이 없습니다.");
    }

    globalUserId = id;


    // 만약 db에 데이터 값이 있었다면, 배송지정보에 삽입
    if (username) {
      receiverNameInput.value = username;
    }

    if (phone_number) {
      receiverPhoneNumberInput.value = phone_number;
    }
  } catch (error) {
    console.error('Error fetching user data:', error);
  }
}

// 주문 요약 정보를 불러와 화면에 표시하는 함수
async function insertOrderSummary() {
    try {
        const response = await Api.get("/api/cart");
        const cartDetails = response;

        console.log(cartDetails);

        if (!cartDetails.length) {
            alert("장바구니에 상품이 없습니다. 상품을 먼저 추가해 주세요.");
            return;
        }

        let productsTitle = "";
        let productsTotal = 0;

        cartDetails.forEach((item, index) => {
            productsTitle += `${index + 1}. ${item.title} - ${item.quantity}개\n`;
            productsTotal += item.price * item.quantity;
        });

        productsTitleElem.innerText = productsTitle;

        const totalPrice = productsTotal;
        orderTotalElem.innerText = `${addCommas(totalPrice)}원`;

        const shippingFee = 3000;
        deliveryFeeElem.innerText = `${addCommas(shippingFee)}원`;

        const discountRateValue = 0.1;
        discountRate.innerText = `${(discountRateValue * 100).toFixed(0)}%`;

        const totalWithDeliveryAndDiscount = (totalPrice + shippingFee) * (1 - discountRateValue);
        orderTotalElem.innerText = `${addCommas(totalWithDeliveryAndDiscount)}원`;

        receiverNameInput.focus();
    } catch (error) {
        console.error("장바구니 정보를 가져오는 중 오류가 발생했습니다.", error);
        alert("장바구니 정보를 불러오는 중 문제가 발생했습니다.");
    }
}

// "직접 입력" 선택 시 input칸 보이게 함
// default값(배송 시 요청사항을 선택해 주세여) 이외를 선택 시 글자가 진해지도록 함
function handleRequestChange(e) {
  const type = e.target.value;

  if (type === "6") {
    customRequestContainer.style.display = "flex";
    customRequestInput.focus();
  } else {
    customRequestContainer.style.display = "none";
  }

  if (type === "0") {
    requestSelectBox.style.color = "rgba(0, 0, 0, 0.3)";
  } else {
    requestSelectBox.style.color = "rgba(0, 0, 0, 1)";
  }
}


// 결제 진행
async function doCheckout() {
if (!globalUserId) {
        alert('로그인이 필요합니다.');
        return navigate('/login');  // 로그인 페이지로 이동
    }
  const receiverName = receiverNameInput.value;
  const receiverPhoneNumber = receiverPhoneNumberInput.value;
  const postalCode = postalCodeInput.value;
  const address1 = address1Input.value;
  const address2 = address2Input.value;
  const requestType = requestSelectBox.value;
  const customRequest = customRequestInput.value;
  const summaryTitle = productsTitleElem.innerText;
  const totalPrice = convertToNumber(orderTotalElem.innerText); // 백엔드가 BigDecimal을 요구한다면 문자열 형태로 전송

  if (!receiverName || !receiverPhoneNumber || !postalCode || !address2) {
    return alert("배송지 정보를 모두 입력해 주세요.");
  }

  // 요청사항의 종류에 따라 request 문구가 달라짐
  let request;
  if (requestType === "0") {
    request = "요청사항 없음.";
  } else if (requestType === "6") {
    if (!customRequest) {
      return alert("요청사항을 작성해 주세요.");
    }
    request = customRequest;
  } else {
    request = requestOption[requestType];
  }

  const address = {
    postalCode,
    address1,
    address2,
    receiverName,
    receiverPhoneNumber
  };


try {
    // 전체 주문을 등록함
    const discountRate = 0.1;
    const orderData = await Api.post("/orders/create", {
      userId: globalUserId,
      orderDate: new Date().toISOString(),
      orderStatus: "주문 완료",
      discountRate: discountRate,
      totalPrice: totalPrice,
      userAddress: `${address.postalCode} ${address.address1} ${address.address2}`,
      orderOption: 'AVAILABLE',
      request,
    });

    // orderId를 가져오기 위해 새로 생성된 주문을 GET 요청으로 가져옴
    const orders = await Api.get(`/orders/user/${globalUserId}/all`);
    const orderId = orders[orders.length - 1].id;  // 마지막으로 생성된 주문의 ID 추출

    if (!orderId) {
      throw new Error("Order ID를 추출할 수 없습니다.");
    }

    const cartItems = await Api.get("/api/cart");
    for (const item of cartItems) {
          const orderLine = {
            quantity: item.quantity,
            price: item.price,
            discountRate: 0.1, // 예시 할인율
            orderId: orderId,
          };
          console.log(cartItems);

          const createdOrderLine = await Api.post('/orderLine/create', orderLine);
            // 각 orderLine에 대해 여러 orderLineBook을 생성
                const orderLineBookDTO = {
                    quantity: item.quantity,
                    bookId: item.bookDetailId,
                    orderLineId: createdOrderLine.id // 서버에서 반환된 ID 사용
                };

                await Api.post("/orderLineBook/create", orderLineBookDTO);
        }
    alert("결제 및 주문이 정상적으로 완료되었습니다.\n감사합니다.");
    window.location.href = "/order-complete/order-complete.html";
  } catch (err) {
    console.log(err);
    alert(`결제 중 문제가 발생하였습니다: ${err.message}`);
  }
}