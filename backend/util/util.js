
const axios = require('axios');

const qs = require('querystring');

const access_token = '24.4db8bb7f160d411a4ebd6c685489bf6b.2592000.1574934923.282335-17603369';

const baiduFaceAddUrl = 'https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add';

const baiduFaceSearchUrl = 'https://aip.baidubce.com/rest/2.0/face/v3/search';

const baiduFaceMultiSearchUrl = 'https://aip.baidubce.com/rest/2.0/face/v3/multi-search';

const baiduFaceDeleteUrl = 'https://aip.baidubce.com/rest/2.0/face/v3/faceset/face/delete';

const baiduFaceDetectUrl = 'https://aip.baidubce.com/rest/2.0/face/v3/detect';

const baiduFaceCompareUrl = 'https://aip.baidubce.com/rest/2.0/face/v3/match';

// 获取到百度大脑的access_token
const param = qs.stringify({
  'grant_type': 'client_credentials',
  'client_id': 'EVPajzLLHFHp2FZtwHyOZdxI',
  'client_secret': 'gkiwQAF30KtUN2j80UKbqEqCNwFlKzxU'
});

const getAccessToken = axios.get('https://aip.baidubce.com/oauth/2.0/token?' + param)
.then(response => {
  console.log(response.data.access_token);
})
.catch(error => {
  console.log(error);
});

const apiAddFace = (addParams, successCallback, failCallback) =>{
  axios.post(baiduFaceAddUrl + '?access_token=' + access_token, addParams)
    .then(response => {
      console.log(response.data, response.data.result, "添加结果在这");
      if (response.data.error_msg === 'SUCCESS') {
        successCallback(response.data);
      } else {
        failCallback(response.data.error_msg);
      }
    })
    .catch(error => {
      failCallback(error);
    })
}

const apiDetectFace = (detectParams, successCallback, failCallback) => {
  axios.post(baiduFaceDetectUrl + '?access_token=' + access_token, detectParams)
    .then(response => {
      console.log(response.data, response.data.result, "检测结果在这！");
      successCallback(response.data);
    })
    .catch(error => {
      console.log(error);
      failCallback(error);
    })
}

const apiSearchFacee = (searchParams, successCallback, failCallback ) => {
  axios.post(baiduFaceMultiSearchUrl + '?access_token=' + access_token, searchParams)
    .then(response => {
      console.log(response.data, response.data.result, "查找结果在这！");
      successCallback(response.data);
    })
    .catch(error => {
      failCallback(error);
    })
}

const apiDeleteFace = (deleteParams, successCallback, failCallback) => {
  axios.post(baiduFaceDeleteUrl + '?access_token=' + access_token, deleteParams)
    .then(response => {
      console.log(response.data);
      successCallback(response.data);
    })
    .catch(error => {
      failCallback(error);
    })
}

const apiCompareFace = (compareParams, successCallback, failCallback) => {
  axios.post(baiduFaceCompareUrl + '?access_token=' + access_token, compareParams)
  .then(response => {
    console.log(response.data);
    successCallback(response.data);
  })
  .catch(error => {
    failCallback(error);
  })
}

// 添加人脸成功后的回调函数
const successHandle = (addResponse, post, res, code, message ) => {
  post.face_token = addResponse.result.face_token;
  post.save().then(createPost => {
    res.status(code).json({
      message: message,
      post: {
        ...createPost,
        id: createPost._id
      }
    })
  });
}

// 添加人脸失败后的回调函数
const errorHandle = (error, res) => {
  console.log(error, "错误日志在这！");
  res.status(400).json({
    message: error
  })
}

module.exports = {
  apiAddFace: apiAddFace,
  apiCompareFace: apiCompareFace,
  apiDetectFace: apiDetectFace,
  apiSearchFacee: apiSearchFacee,
  apiDeleteFace: apiDeleteFace,
  successHandle: successHandle,
  errorHandle: errorHandle
}
