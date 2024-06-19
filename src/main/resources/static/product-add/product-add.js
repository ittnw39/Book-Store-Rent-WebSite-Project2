import * as Api from "../../api.js";
import { checkLogin, randomId, createNavbar, checkAdmin } from "../../useful-functions.js";

// 요소(element)들과 상수들
const titleInput = document.querySelector("#titleInput");
const categorySelectBox = document.querySelector("#categorySelectBox");
const authorInput = document.querySelector("#authorInput");
const publisherInput = document.querySelector("#publisherInput");
const publishedDateInput = document.querySelector("#publishedDateInput");
const descriptionInput = document.querySelector(
  "#descriptionInput"
);
const imageInput = document.querySelector("#imageInput");
const totalStockQuantityInput = document.querySelector("#totalStockQuantityInput");
const priceInput = document.querySelector("#priceInput");
const pageInput = document.querySelector("#pageInput");
const submitButton = document.querySelector("#submitButton");
const registerProductForm = document.querySelector("#registerProductForm");

let bookImageURL;

//수정을 통해 제품 등록페이지로 넘어올 경우 id 값을 받는 역할
const params = new URLSearchParams(window.location.search);
const bookId = params.get('id');

checkLogin();
addAllElements();
addAllEvents();
checkAdmin();

// html에 요소를 추가하는 함수들을 묶어주어서 코드를 깔끔하게 하는 역할임.
function addAllElements() {
  createNavbar();
  addOptionsToSelectBox().then(() => {
    fillBookInfo();
  });
}

// addEventListener들을 묶어주어서 코드를 깔끔하게 하는 역할임.
function addAllEvents() {
  submitButton.addEventListener("click", handleSubmit);
  categorySelectBox.addEventListener("change", handleCategoryChange);
}

// 제품 추가 - 사진은 AWS S3에 저장, 이후 제품 정보를 백엔드 db에 저장.
async function handleSubmit(e) {
  e.preventDefault();

  const title = titleInput.value;
  const categoryId = categorySelectBox.value;
  const authorName = authorInput.value;
  const publisher = publisherInput.value;
  const publishedDate = publishedDateInput.value;
  const description = descriptionInput.value;
  const totalStockQuantity = parseInt(totalStockQuantityInput.value);
  const price = parseInt(priceInput.value);
  const page = parseInt(pageInput.value);

  const fileInput = document.getElementById('imageInput');
  const file = fileInput.files[0];

  // 입력 칸이 비어 있으면 진행 불가
  if (
    !title ||
    !categoryId ||
    !authorName ||
    !publisher ||
    !publishedDate ||
    !description ||
    !totalStockQuantity ||
    !price ||
    !page
  ) {
    return alert("빈 칸 및 0이 없어야 합니다.");
  }

  const imageURL = bookImageURL;

  try {
    // 이미지 업로드 및 데이터 처리
    if(file) {
        const formData = new FormData();
        formData.append('file', file);
        formData.append('fileName', file.name);
        const url = await uploadImage(formData);
        await processData(title, authorName, publisher, publishedDate, description, totalStockQuantity, price, page, url);
    } else {
        await processData(title, authorName, publisher, publishedDate, description, totalStockQuantity, price, page, imageURL);
    }
  } catch (error) {
    console.error("에러 발생:", error);
    alert("문제가 발생하였습니다. 확인 후 다시 시도해 주세요.");
  }
}

//이미지 업로드 함수
async function uploadImage(formData) {
  const response = await fetch('/image/upload', {
    method: 'POST',
    body: formData
  });
  const url = await response.text();
  return url;
}

//data를 전달하여 post, put 실행
async function processData(title, authorName, publisher, publishedDate, description, totalStockQuantity, price, page, imageURL) {
  const index = categorySelectBox.selectedIndex;
  const categoryName = categorySelectBox[index].text;
  const category = {name : categoryName};
  const author = {name : authorName};

  const data = {
    title,
    category,
    author,
    publisher,
    publishedDate,
    description,
    imageURL,
    totalStockQuantity,
    price,
    page
  };

  try {
    if(bookId) {
      await Api.push("/admin/api/book", data, bookId);
    } else {
      await Api.post("/admin/api/book", data);
    }

    alert(`정상적으로 ${title} 제품이 ${bookId ? '업데이트' : '등록'} 되었습니다.`);

    // 폼 초기화
    registerProductForm.reset();
    fileNameSpan.innerText = "";
    categorySelectBox.style.color = "black";
    categorySelectBox.style.backgroundColor = "white";
  } catch (err) {
    console.error("에러 발생:", err);
    alert(`문제가 발생하였습니다. 확인 후 다시 시도해 주세요: ${err.message}`);
  }
}

// 선택할 수 있는 카테고리 종류를 api로 가져와서, 옵션 태그를 만들어 삽입함.
async function addOptionsToSelectBox() {
  const categories = await Api.get("/categories");
  categories.forEach((category) => {
    // 객체 destructuring
    const { id, name } = category;

    categorySelectBox.insertAdjacentHTML(
      "beforeend",
      `
      <option value=${id}> ${name} </option>`
    );
  });
}

// 카테고리 선택 시, 선택박스에 해당 카테고리 테마가 반영되게 함.
function handleCategoryChange() {
  const index = categorySelectBox.selectedIndex;

  categorySelectBox.className = categorySelectBox[index].className;
}

//관리자 페이지에서 수정 버튼으로 접근 시 데이터 불러오기
async function fillBookInfo() {
  const bookInfo = await Api.get(`/api/book/${bookId}`);

  // 가져온 책 정보를 각 필드에 채움
  titleInput.value = bookInfo.title;
  categorySelectBox.value = bookInfo.category.id;
  authorInput.value = bookInfo.author.name;
  publisherInput.value = bookInfo.publisher;
  publishedDateInput.value = bookInfo.publishedDate.split("T")[0];
  descriptionInput.value = bookInfo.description;
  totalStockQuantityInput.value = bookInfo.totalStockQuantity;
  priceInput.value = bookInfo.price;
  pageInput.value = bookInfo.page;

  bookImageURL = bookInfo.imageURL;
}
