import { randomId } from "./useful-functions.js";

// aws-s3 사이트에서의 설정값들
const s3BucketName = "elice-shoppingmall";
const bucketRegion = "ap-northeast-2"; // 한국은 항상 ap-northeast-2임.
const IdentityPoolId = "ap-northeast-2:5e7a5d7d-e6ed-462c-b302-f5663eb5fb0d";

// aws 공식문서 그대로 가져옴
AWS.config.update({
  region: bucketRegion,
  credentials: new AWS.CognitoIdentityCredentials({
    IdentityPoolId: IdentityPoolId,
  }),
});

// aws 공식문서 그대로 가져옴
const s3 = new AWS.S3({
  apiVersion: "2006-03-01",
  params: { Bucket: s3BucketName },
});

// 아마존 S3에 사진파일 올리는 함수
// fileInputElement: input 요소
// album: S3에서 업로드된 사진파일이 속할 폴더 이름.
async function addImageToS3(fileInputElement, album) {
  // 파일 input 요소에, 사용자가 올린 파일이 있는지 여부 확인
  const files = fileInputElement.files;
  if (!files.length) {
    throw new Error("사진 파일을 업로드해 주세요.");
  }

  // 파일 input 요소에서 사진파일 추출 등 AWS S3로의 업로드 준비
  const file = files[0];
  // 유니크한 사진파일 주소를 만들 수 있게 함.
  const fileName = randomId() + "_" + file.name;
  const albumPhotosKey = encodeURIComponent(album) + "/";
  const photoKey = albumPhotosKey + fileName;

  const upload = new AWS.S3.ManagedUpload({
    params: {
      Bucket: s3BucketName,
      Key: photoKey,
      Body: file,
    },
  });

  // AWS S3에 업로드 진행 -> 성공 시, 업로드된 파일 주소
  // (정확히는, 주소의 뒷부분만 - 폴더/파일이름 만)를 반환
  try {
    const uploadedFile = await upload.promise();

    const fileKey = uploadedFile.Key;
    console.log(uploadedFile);
    console.log(
      `AWS S3에 정상적으로 사진이 업로드되었습니다.\n파일 위치: ${fileKey}`
    );

    return fileKey;
  } catch (err) {
    throw new Error(
      `S3에 업로드하는 과정에서 에러가 발생하였습니다.\n${err.message}`
    );
  }
}

// 업로드한 사진을 바로 링크를 통해 보고 싶어도, 그냥 url로 접근하면,
// 권한 인증이 없어서 사진 열람이 불가함.
// 아래 함수로, 인증코드가 추가된 특별한 url을 만든 후, img 요소의 src로 삽입해야 함.
function getImageUrl(imageKey) {
  const imageUrl = new Promise((resolve) => {
    const params = {
      Bucket: s3BucketName,
      Key: imageKey,
      Expires: 60,
    };

    s3.getSignedUrl("getObject", params, (_, url) => {
      resolve(url);
    });
  });

  return imageUrl;
}

export { addImageToS3, getImageUrl };
