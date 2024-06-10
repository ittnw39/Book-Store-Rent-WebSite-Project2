async function get(endpoint, params = "") {
  const apiUrl = params ? `${endpoint}/${params}` : endpoint;
  console.log(`%cGET 요청: ${apiUrl} `, "color: #a25cd1;");

  // 토큰이 있으면 Authorization 헤더를 포함, 없으면 포함하지 않음
  const token = sessionStorage.getItem("token");
  const headers = token ? { Authorization: `Bearer ${token}` } : {};



  const res = await fetch(apiUrl, { headers });

  // 응답 코드가 4XX 계열일 때 (400, 403 등)
  if (!res.ok) {
    const errorContent = await res.json();
    const { reason } = errorContent;

    throw new Error(reason);
  }

  const result = await res.json();
  return result;
}

async function post(endpoint, data) {
  const apiUrl = endpoint;
  const bodyData = JSON.stringify(data);
  console.log(`%cPOST 요청: ${apiUrl}`, "color: #296aba;");
  console.log(`%cPOST 요청 데이터: ${bodyData}`, "color: #296aba;");

  //토큰이 있으면 Authorization 헤더를 포함, 없으면 포함하지 않음
  const token = sessionStorage.getItem("token");
  console.log("patch 함수 내의 token 값:", token);
  const headers = {
    "Content-Type": "application/json",
    ...(token && { Authorization: `Bearer ${token}` }),
  };

  const res = await fetch(apiUrl, {
    method: "POST",
    headers,
    body: bodyData,
  });

  // 응답 코드가 4XX 계열일 때 (400, 403 등)
  if (!res.ok) {
    const errorContent = await res.json();
    const { reason } = errorContent;

    throw new Error(reason);
  }

  const result = await res.json();
  return result;
}

async function patch(endpoint, params = "", data) {
  const apiUrl = params ? `${endpoint}/${params}` : endpoint;

  const bodyData = JSON.stringify(data);
  console.log(`%cPATCH 요청: ${apiUrl}`, "color: #059c4b;");
  console.log(`%cPATCH 요청 데이터: ${bodyData}`, "color: #059c4b;");

  const token = sessionStorage.getItem("token");
    const headers = {
      "Content-Type": "application/json",
      ...(token && { Authorization: `Bearer ${token}` }),
    };

  const res = await fetch(apiUrl, {
    method: "PATCH",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${sessionStorage.getItem("token")}`,
    },
    body: bodyData,
  });

//const res = await fetch(apiUrl, {
//  method: "PATCH",
//  headers,
//  body: bodyData,
//});


  // 응답 코드가 4XX 계열일 때 (400, 403 등)
  if (!res.ok) {
    const errorContent = await res.json();
    const { reason } = errorContent;

    throw new Error(reason);
  }

  const result = await res.json();

  return result;
}

async function del(endpoint, params = "", data = {}) {
  const apiUrl = `${endpoint}/${params}`;
  const bodyData = JSON.stringify(data);

  console.log(`DELETE 요청 ${apiUrl}`, "color: #059c4b;");
  console.log(`DELETE 요청 데이터: ${bodyData}`, "color: #059c4b;");


  // 토큰이 있으면 Authorization 헤더를 포함, 없으면 포함하지 않음
    const token = sessionStorage.getItem("token");
    const headers = {
      "Content-Type": "application/json",
      ...(token && { Authorization: `Bearer ${token}` }),
    };

    const res = await fetch(apiUrl, {
      method: "DELETE",
      headers,
      body: bodyData,
    });


  // 응답 코드가 4XX 계열일 때 (400, 403 등)
  if (!res.ok) {
    const errorContent = await res.json();
    const { reason } = errorContent;

    throw new Error(reason);
  }

  const result = await res.json();

  return result;
}

// 아래처럼 export하면, import * as Api 로 할 시 Api.get, Api.post 등으로 쓸 수 있음.
export { get, post, patch, del as delete };
