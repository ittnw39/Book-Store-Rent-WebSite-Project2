import { checkAdmin, createNavbar } from "../useful-functions.js";
import * as Api from "../api.js";

async function fetchAdminData() {
  const token = sessionStorage.getItem("token");
  const adminData = await Api.get("/admin/data", "", token);
  // 가져온 관리자 데이터를 사용하여 페이지를 렌더링하는 로직 작성
}

checkAdmin();
createNavbar();
fetchAdminData();