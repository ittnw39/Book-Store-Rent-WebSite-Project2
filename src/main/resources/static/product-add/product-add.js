import { addImageToS3 } from "../../aws-s3.js";
import * as Api from "../../api.js";
import { checkLogin, randomId, createNavbar } from "../../useful-functions.js";

// 요소(element)들과 상수들
const titleInput = document.querySelector("#titleInput");
const categorySelectBox = document.querySelector("#categorySelectBox");
const authorInput = document.querySelector("#authorInput");
const publisherInput = document.querySelector("#publisherInput");
const publishedDateInput = document.querySelector("#publishedDateInput");
const descriptionInput = document.querySelector(
  "#descriptionInput"
);
//const imageInput = document.querySelector("#imageInput");
const totalStockQuantityInput = document.querySelector("#totalStockQuantityInput");
const priceInput = document.querySelector("#priceInput");
const pageInput = document.querySelector("#pageInput");

const submitButton = document.querySelector("#submitButton");
const registerProductForm = document.querySelector("#registerProductForm");

/*추가수정*/
const params = new URLSearchParams(window.location.search);
const bookId = params.get('id');

//checkLogin();
addAllElements();
addAllEvents();

// html에 요소를 추가하는 함수들을 묶어주어서 코드를 깔끔하게 하는 역할임.
function addAllElements() {
  createNavbar();
  addOptionsToSelectBox();
  fillBookInfo();
}

// addEventListener들을 묶어주어서 코드를 깔끔하게 하는 역할임.
function addAllEvents() {
//  imageInput.addEventListener("change", handleImageUpload);
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
//  const image = imageInput.files[0];
  const totalStockQuantity = parseInt(totalStockQuantityInput.value);
  const price = parseInt(priceInput.value);
  const page = parseInt(pageInput.value);

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

  /*if (image.size > 3e6) {
    return alert("사진은 최대 2.5MB 크기까지 가능합니다.");
  }*/

  // S3 에 이미지가 속할 폴더 이름은 카테고리명으로 함.
  const index = categorySelectBox.selectedIndex;
  const categoryName = categorySelectBox[index].text;
  const category = {name : categoryName};
  const author = {name : authorName};

  try {
//    const imageKey = await addImageToS3(imageInput, categoryName);
    const data = {
      title,
      category,
      author,
      publisher,
      publishedDate,
      description,
//      imageKey,
      totalStockQuantity,
      price,
      page
    };

    if(bookId) {
      await Api.put("/admin/api/book", data, bookId);
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
    console.log(err.stack);

    alert(`문제가 발생하였습니다. 확인 후 다시 시도해 주세요: ${err.message}`);
  }
}

// 사용자가 사진을 업로드했을 때, 파일 이름이 화면에 나타나도록 함.
/*function handleImageUpload() {
  const file = imageInput.files[0];
  if (file) {
    fileNameSpan.innerText = file.name;
  } else {
    fileNameSpan.innerText = "";
  }
}*/

// 선택할 수 있는 카테고리 종류를 api로 가져와서, 옵션 태그를 만들어 삽입함.
async function addOptionsToSelectBox() {
  const categories = await Api.get("/admin/category/all");
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

//관리자 페이지에서 수정 버튼으로 접근 시 실행
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
}