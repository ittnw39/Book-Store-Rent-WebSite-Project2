import * as Api from "../api.js";
import {
  checkLogin,
  addCommas,
  convertToNumber,
  navigate,
  randomPick,
  createNavbar,
} from "../useful-functions.js";
import { deleteFromDb, getFromDb, putToDb } from "../indexed-db.js";

const subtitleCart = document.querySelector("#subtitleCart");
// 요소(element), input 혹은 상수
const customRequestContainer = document.querySelector(
  "#customRequestContainer"
);

checkLogin();
addAllElements();

// html에 요소를 추가하는 함수들을 묶어주어서 코드를 깔끔하게 하는 역할임.
function addAllElements() {
  createNavbar();
}