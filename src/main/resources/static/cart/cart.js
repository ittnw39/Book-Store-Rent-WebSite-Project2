import { getImageUrl } from "../aws-s3.js";
import {
  addCommas,
  convertToNumber,
  navigate,
  compressString,
  createNavbar,
} from "../useful-functions.js";
import { deleteFromDb, getFromDb, putToDb } from "../indexed-db.js";

// 요소(element), input 혹은 상수
const cartProductsContainer = document.querySelector("#cartProductsContainer");
const allSelectCheckbox = document.querySelector("#allSelectCheckbox");
const partialDeleteLabel = document.querySelector("#partialDeleteLabel");
const productsCountElem = document.querySelector("#productsCount");
const productsTotalElem = document.querySelector("#productsTotal");
const deliveryFeeElem = document.querySelector("#deliveryFee");
const orderTotalElem = document.querySelector("#orderTotal");
const purchaseButton = document.querySelector("#purchaseButton");

addAllElements();
addAllEvents();

// html에 요소를 추가하는 함수들을 묶어주어서 코드를 깔끔하게 하는 역할임.
function addAllElements() {
  createNavbar();
  insertProductsfromCart();
  insertOrderSummary();
  updateAllSelectCheckbox();
}

// addEventListener들을 묶어주어서 코드를 깔끔하게 하는 역할임.
function addAllEvents() {
  allSelectCheckbox.addEventListener("change", toggleAll);
  partialDeleteLabel.addEventListener("click", deleteSelectedItems);
  purchaseButton.addEventListener("click", navigate("/order"));
}

// indexedDB의 cart와 order에서 필요한 정보를 가져온 후
// 요소(컴포넌트)를 만들어 html에 삽입함.
async function insertProductsfromCart() {
  const products = await getFromDb("cart");
  const { selectedIds } = await getFromDb("order", "summary");

  products.forEach(async (product) => {
    // 객체 destructuring
    const { _id, title, quantity, imageKey, price } = product;
    const imageUrl = await getImageUrl(imageKey);

    const isSelected = selectedIds.includes(_id);

    cartProductsContainer.insertAdjacentHTML(
      "beforeend",
      `
        <div class="cart-product-item" id="productItem-${_id}">
          <label class="checkbox">
            <input type="checkbox" id="checkbox-${_id}" ${
        isSelected ? "checked" : ""
      } />
          </label>
          <button class="delete-button" id="delete-${_id}">
            <span class="icon">
              <i class="fas fa-trash-can"></i>
            </span>
          </button>
          <figure class="image is-96x96">
            <img
              id="image-${_id}"
              src="${imageUrl}"
              alt="product-image"
            />
          </figure>
          <div class="content">
            <p id="title-${_id}">${compressString(title)}</p>
            <div class="quantity">
              <button 
                class="button is-rounded" 
                id="minus-${_id}" 
                ${quantity <= 1 ? "disabled" : ""}
                ${isSelected ? "" : "disabled"}
              >
                <span class="icon is-small">
                  <i class="fas fa-thin fa-minus"></i>
                </span>
              </button>
              <input
                class="input"
                id="quantityInput-${_id}"
                type="number"
                min="1"
                max="99"
                value="${quantity}"
                ${isSelected ? "" : "disabled"}
              />
              <button 
                class="button is-rounded" 
                id="plus-${_id}"
                ${quantity >= 99 ? "disabled" : ""}
                ${isSelected ? "" : "disabled"}
              >
                <span class="icon">
                  <i class="fas fa-lg fa-plus"></i>
                </span>
              </button>
            </div>
          </div>
          <div class="calculation">
            <p id="unitPrice-${_id}">${addCommas(price)}원</p>
            <p>
              <span class="icon">
                <i class="fas fa-thin fa-xmark"></i>
              </span>
            </p>
            <p id="quantity-${_id}">${quantity}</p>
            <p>
              <span class="icon">
                <i class="fas fa-thin fa-equals"></i>
              </span>
            </p>
            <p id="total-${_id}">${addCommas(quantity * price)}원</p>
          </div>
        </div>
      `
    );

    // 각종 이벤트 추가
    document
      .querySelector(`#delete-${_id}`)
      .addEventListener("click", () => deleteItem(_id));

    document
      .querySelector(`#checkbox-${_id}`)
      .addEventListener("change", () => toggleItem(_id));

    document
      .querySelector(`#image-${_id}`)
      .addEventListener("click", navigate(`/product/detail?id=${_id}`));

    document
      .querySelector(`#title-${_id}`)
      .addEventListener("click", navigate(`/product/detail?id=${_id}`));

    document
      .querySelector(`#plus-${_id}`)
      .addEventListener("click", () => increaseItemQuantity(_id));

    document
      .querySelector(`#minus-${_id}`)
      .addEventListener("click", () => decreaseItemQuantity(_id));

    document
      .querySelector(`#quantityInput-${_id}`)
      .addEventListener("change", () => handleQuantityInput(_id));
  });
}

async function toggleItem(id) {
  const itemCheckbox = document.querySelector(`#checkbox-${id}`);
  const isChecked = itemCheckbox.checked;

  // 결제정보 업데이트 및, 체크 상태에서는 수량을 수정 가능 (언체크는 불가능)으로 함
  if (isChecked) {
    await updateOrderSummary(id, "add-checkbox");
    setQuantityBox(id, "able");
  } else {
    await updateOrderSummary(id, "removeTemp-checkbox");
    setQuantityBox(id, "disable");
  }
}

async function toggleAll(e) {
  // 전체 체크냐 전체 체크 해제이냐로 true 혹은 false
  const isCheckAll = e.target.checked;
  const { ids } = await getFromDb("order", "summary");

  ids.forEach(async (id) => {
    const itemCheckbox = document.querySelector(`#checkbox-${id}`);
    const isItemCurrentlyChecked = itemCheckbox.checked;

    // 일단 아이템(제품) 체크박스에 전체 체크 혹은 언체크 여부를 반영함.
    itemCheckbox.checked = isCheckAll;

    // 결제정보 업데이트 필요 여부 확인
    const isAddRequired = isCheckAll && !isItemCurrentlyChecked;
    const isRemoveRequired = !isCheckAll && isItemCurrentlyChecked;

    // 결제정보 업데이트 및, 체크 상태에서는 수정 가능으로 함
    if (isAddRequired) {
      updateOrderSummary(id, "add-checkbox");
      setQuantityBox(id, "able");
    }

    // 결제정보 업데이트 및, 언체크 상태에서는 수정 불가능으로 함
    if (isRemoveRequired) {
      updateOrderSummary(id, "removeTemp-checkbox");
      setQuantityBox(id, "disable");
    }
  });
}

async function increaseItemQuantity(id) {
  // 결제정보카드 업데이트
  await updateOrderSummary(id, "add-plusButton");

  // 제품아이템카드 업데이트
  await updateProductItem(id, "increase");

  // indexedDB의 cart 데이터 업데이트
  await putToDb("cart", id, (data) => {
    data.quantity = data.quantity + 1;
  });

  // 수량 변경박스(-버튼, 입력칸, +버튼) 상태 업데이트
  setQuantityBox(id, "plus");
}

async function decreaseItemQuantity(id) {
  // 결제정보카드 업데이트
  await updateOrderSummary(id, "minusButton");

  // 제품아이템카드 업데이트
  await updateProductItem(id, "decrease");

  // indexedDB의 cart 데이터 업데이트
  await putToDb("cart", id, (data) => {
    data.quantity = data.quantity - 1;
  });

  // 수량 변경박스(-버튼, 입력칸, +버튼) 상태 업데이트
  setQuantityBox(id, "minus");
}

async function handleQuantityInput(id) {
  // 우선 입력값이 범위 1~99 인지 확인
  const inputElem = document.querySelector(`#quantityInput-${id}`);
  const quantity = parseInt(inputElem.value);

  if (quantity < 1 || quantity > 99) {
    return alert("수량은 1~99 사이가 가능합니다.");
  }

  // 결제정보카드 업데이트
  await updateOrderSummary(id, "add-input");

  // 제품아이템카드 업데이트
  await updateProductItem(id, "input");

  // indexedDB의 cart 데이터 업데이트
  await putToDb("cart", id, (data) => {
    data.quantity = quantity;
  });

  // 수량 변경박스(-버튼, 입력칸, +버튼) 상태 업데이트
  setQuantityBox(id, "input");
}

// -버튼, 숫자입력칸, +버튼 활성화 여부 및 값을 세팅함.
function setQuantityBox(id, type) {
  // 세팅 방식 결정을 위한 변수들
  const isPlus = type.includes("plus");
  const isMinus = type.includes("minus");
  const isInput = type.includes("input");
  const isDisableAll = type.includes("disable");

  // 세팅을 위한 요소들
  const minusButton = document.querySelector(`#minus-${id}`);
  const quantityInput = document.querySelector(`#quantityInput-${id}`);
  const plusButton = document.querySelector(`#plus-${id}`);

  // 우선 기본적으로 활성화시킴
  minusButton.removeAttribute("disabled");
  quantityInput.removeAttribute("disabled");
  plusButton.removeAttribute("disabled");

  // 전체 비활성화 시키는 타입일 경우 (제품 체크를 해제했을 때 등)
  if (isDisableAll) {
    minusButton.setAttribute("disabled", "");
    quantityInput.setAttribute("disabled", "");
    plusButton.setAttribute("disabled", "");
    return;
  }

  // input칸 값을 업데이트하기 위한 변수 설정
  let quantityUpdate;
  if (isPlus) {
    quantityUpdate = +1;
  } else if (isMinus) {
    quantityUpdate = -1;
  } else if (isInput) {
    quantityUpdate = 0;
  } else {
    quantityUpdate = 0;
  }

  // input칸 값 업데이트
  const currentQuantity = parseInt(quantityInput.value);
  const newQuantity = currentQuantity + quantityUpdate;
  quantityInput.value = newQuantity;

  // 숫자는 1~99만 가능
  const isMin = newQuantity === 1;
  const isMax = newQuantity === 99;

  if (isMin) {
    minusButton.setAttribute("disabled", "");
  }

  if (isMax) {
    plusButton.setAttribute("disabled", "");
  }
}

async function deleteSelectedItems() {
  const { selectedIds } = await getFromDb("order", "summary");

  selectedIds.forEach((id) => deleteItem(id));
}

// 전체선택 체크박스를, 현재 상황에 맞추어
// 체크 또는 언체크 상태로 만듦
async function updateAllSelectCheckbox() {
  const { ids, selectedIds } = await getFromDb("order", "summary");

  const isOrderEmpty = ids.length === 0;
  const isAllItemSelected = ids.length === selectedIds.length;

  // 장바구니 아이템(제품) 수가 0이 아니고,
  // 또 전체 아이템들이 선택된 상태라면 체크함.
  if (!isOrderEmpty && isAllItemSelected) {
    allSelectCheckbox.checked = true;
  } else {
    allSelectCheckbox.checked = false;
  }
}

async function deleteItem(id) {
  // indexedDB의 cart 목록에서 id를 key로 가지는 데이터를 삭제함.
  await deleteFromDb("cart", id);

  // 결제정보를 업데이트함.
  await updateOrderSummary(id, "removePermanent-deleteButton");

  // 제품 요소(컴포넌트)를 페이지에서 제거함
  document.querySelector(`#productItem-${id}`).remove();

  // 전체선택 체크박스를 업데이트함
  updateAllSelectCheckbox();
}

// 결제정보 카드 업데이트 및, indexedDB 업데이트를 진행함.
async function updateOrderSummary(id, type) {
  // 업데이트 방식 결정을 위한 변수들
  const isCheckbox = type.includes("checkbox");
  const isInput = type.includes("input");
  const isDeleteButton = type.includes("deleteButton");
  const isMinusButton = type.includes("minusButton");
  const isPlusButton = type.includes("plusButton");
  const isAdd = type.includes("add");
  const isRemoveTemp = type.includes("removeTemp");
  const isRemovePermanent = type.includes("removePermanent");
  const isRemove = isRemoveTemp || isRemovePermanent;
  const isItemChecked = document.querySelector(`#checkbox-${id}`).checked;
  const isDeleteWithoutChecked = isDeleteButton && !isItemChecked;

  // 업데이트에 사용될 변수
  let price;
  let quantity;

  // 체크박스 혹은 삭제 버튼 클릭으로 인한 업데이트임.
  if (isCheckbox || isDeleteButton) {
    const priceElem = document.querySelector(`#total-${id}`);
    price = convertToNumber(priceElem.innerText);

    quantity = 1;
  }

  // - + 버튼 클릭으로 인한 업데이트임.
  if (isMinusButton || isPlusButton) {
    const unitPriceElem = document.querySelector(`#unitPrice-${id}`);
    price = convertToNumber(unitPriceElem.innerText);

    quantity = 0;
  }

  // input 박스 입력으로 인한 업데이트임
  if (isInput) {
    const unitPriceElem = document.querySelector(`#unitPrice-${id}`);
    const unitPrice = convertToNumber(unitPriceElem.innerText);

    const inputElem = document.querySelector(`#quantityInput-${id}`);
    const inputQuantity = convertToNumber(inputElem.value);

    const quantityElem = document.querySelector(`#quantity-${id}`);
    const currentQuantity = convertToNumber(quantityElem.innerText);

    price = unitPrice * (inputQuantity - currentQuantity);

    quantity = 0;
  }

  // 업데이트 방식
  const priceUpdate = isAdd ? +price : -price;
  const countUpdate = isAdd ? +quantity : -quantity;

  // 현재 결제정보의 값들을 가져오고 숫자로 바꿈.
  const currentCount = convertToNumber(productsCountElem.innerText);
  const currentProductsTotal = convertToNumber(productsTotalElem.innerText);
  const currentFee = convertToNumber(deliveryFeeElem.innerText);
  const currentOrderTotal = convertToNumber(orderTotalElem.innerText);

  // 결제정보 관련 요소들 업데이트
  if (!isDeleteWithoutChecked) {
    productsCountElem.innerText = `${currentCount + countUpdate}개`;
    productsTotalElem.innerText = `${addCommas(
      currentProductsTotal + priceUpdate
    )}원`;
  }

  // 기존 결제정보가 비어있었어서, 배송비 또한 0인 상태였던 경우
  const isFeeAddRequired = isAdd && currentFee === 0;

  if (isFeeAddRequired) {
    deliveryFeeElem.innerText = `3000원`;
    orderTotalElem.innerText = `${addCommas(
      currentOrderTotal + priceUpdate + 3000
    )}원`;
  }

  if (!isFeeAddRequired && !isDeleteWithoutChecked) {
    orderTotalElem.innerText = `${addCommas(
      currentOrderTotal + priceUpdate
    )}원`;
  }

  // 이 업데이트로 인해 결제정보가 비게 되는 경우
  const isCartNowEmpty = currentCount === 1 && isRemove;

  if (!isDeleteWithoutChecked && isCartNowEmpty) {
    deliveryFeeElem.innerText = `0원`;

    // 다시 한 번, 현재 값을 가져와서 3000을 빼 줌
    const currentOrderTotal = convertToNumber(orderTotalElem.innerText);
    orderTotalElem.innerText = `${addCommas(currentOrderTotal - 3000)}원`;

    // 전체선택도 언체크되도록 함.
    updateAllSelectCheckbox();
  }

  // indexedDB의 order.summary 업데이트
  await putToDb("order", "summary", (data) => {
    const hasId = data.selectedIds.includes(id);

    if (isAdd && !hasId) {
      data.selectedIds.push(id);
    }

    if (isRemoveTemp) {
      data.selectedIds = data.selectedIds.filter((_id) => _id !== id);
    }

    if (isRemovePermanent) {
      data.ids = data.ids.filter((_id) => _id !== id);
      data.selectedIds = data.selectedIds.filter((_id) => _id !== id);
    }

    if (!isDeleteWithoutChecked) {
      data.productsCount += countUpdate;
      data.productsTotal += priceUpdate;
    }
  });

  // 전체선택 체크박스 업데이트
  updateAllSelectCheckbox();
}

// 아이템(제품)카드의 수량, 금액 등을 업데이트함
async function updateProductItem(id, type) {
  // 업데이트 방식을 결정하는 변수들
  const isInput = type.includes("input");
  const isIncrease = type.includes("increase");

  // 업데이트에 필요한 요소 및 값들을 가져오고 숫자로 바꿈.
  const unitPriceElem = document.querySelector(`#unitPrice-${id}`);
  const unitPrice = convertToNumber(unitPriceElem.innerText);

  const quantityElem = document.querySelector(`#quantity-${id}`);
  const currentQuantity = convertToNumber(quantityElem.innerText);

  const totalElem = document.querySelector(`#total-${id}`);
  const currentTotal = convertToNumber(totalElem.innerText);

  const inputElem = document.querySelector(`#quantityInput-${id}`);
  const inputQuantity = convertToNumber(inputElem.value);

  // 업데이트 진행
  if (isInput) {
    quantityElem.innerText = `${inputQuantity}개`;
    totalElem.innerText = `${addCommas(unitPrice * inputQuantity)}원`;
    return;
  }

  const quantityUpdate = isIncrease ? +1 : -1;
  const priceUpdate = isIncrease ? +unitPrice : -unitPrice;

  quantityElem.innerText = `${currentQuantity + quantityUpdate}개`;
  totalElem.innerText = `${addCommas(currentTotal + priceUpdate)}원`;
}

// 페이지 로드 시 실행되며, 결제정보 카드에 값을 삽입함.
async function insertOrderSummary() {
  const { productsCount, productsTotal } = await getFromDb("order", "summary");

  const hasItems = productsCount !== 0;

  productsCountElem.innerText = `${productsCount}개`;
  productsTotalElem.innerText = `${addCommas(productsTotal)}원`;

  if (hasItems) {
    deliveryFeeElem.innerText = `3,000원`;
    orderTotalElem.innerText = `${addCommas(productsTotal + 3000)}원`;
  } else {
    deliveryFeeElem.innerText = `0원`;
    orderTotalElem.innerText = `0원`;
  }
}
