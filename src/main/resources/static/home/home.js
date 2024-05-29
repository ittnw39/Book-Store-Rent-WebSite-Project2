import * as Api from "../api.js";
import { getImageUrl } from "../aws-s3.js";
import { navigate, createNavbar } from "../useful-functions.js";
// import {attach} from "bulma-carousel/src/js";


// 요소(element), input 혹은 상수
const sliderDiv = document.querySelector("#slider");
const sliderArrowLeft = document.querySelector("#sliderArrowLeft");
const sliderArrowRight = document.querySelector("#sliderArrowRight");

addAllElements();
addAllEvents();

// html에 요소를 추가하는 함수들을 묶어주어서 코드를 깔끔하게 하는 역할임.
async function addAllElements() {
  createNavbar();
  await addImageCardsToSlider();
  attachSlider();
}

// 여러 개의 addEventListener들을 묶어주어서 코드를 깔끔하게 하는 역할임.
function addAllEvents() {}

// api에서 카테고리 정보 및 사진 가져와서 슬라이드 카드로 사용
async function addImageCardsToSlider() {
  const categories = await Api.get("/categories");
  console.log(categories)

  for (const category of categories) {
    // 객체 destructuring
    const { id, title, description, themeClass, imageKey } = category;

    sliderDiv.insertAdjacentHTML(
      "beforeend",
      `
      <div class="card" id="category-${id}">
        <div class="notification ${themeClass}">
          <p class="title is-3 is-spaced">${title}</p>
          <p class="subtitle is-6">${description}</p>
        </div>
        <div class="card-image">
          <figure class="image is-3by2">
            <img
              src="${imageKey}"
              alt="카테고리 이미지"
            />
          </figure>
        </div>
      </div>
    `
    );

    const card = document.querySelector(`#category-${id}`);

    card.addEventListener("click", navigate(`/product/list?category=${title}`));
  }
}

function attachSlider() {
  // 페이지 로드 완료 후 bulmaCarousel 라이브러리의 attach 함수를 사용합니다.
  document.addEventListener('DOMContentLoaded', () => {
    const imageSlider = bulmaCarousel.attach("#slider", {
      autoplay: true,
      autoplaySpeed: 6000,
      infinite: true,
      duration: 500,
      pauseOnHover: false,
      navigation: false,
    });

    sliderArrowLeft.addEventListener("click", () => {
      imageSlider[0].previous();
    });

    sliderArrowRight.addEventListener("click", () => {
      imageSlider[0].next();
    });
  });
}

