
const fs = require('fs');

const Post = require('../models/post');

const baiduApi = require('../util/util');

const removeFile = (path) => {
  fs.unlink(path, (err) => {
    if (err) throw err;
    console.log(path + 'was deleted');
  });
}


exports.addPost = (req, res, next) => {
  const url = req.protocol + '://' + req.get('host');
  const post = new Post({
    user_id: '',
    face_token: '',
    title: req.body.title,
    content: req.body.content,
    imagePath: url + '/images/' + req.file.filename,
    creator: req.userData.userId
  });

  // 先读出图片转换为bese64格式
  let img = fs.readFileSync(req.file.path);
  let encode_image = img.toString('base64');

  const searchParams = {
    image: encode_image,
    image_type: "BASE64",
    group_id_list: "group1",
    quality_control: "NORMAL"
  };

  const addParams = {
    image: encode_image,
    image_type: "BASE64",
    group_id: "group1",
    user_id: 'user' + Date.now(),
    user_info: req.body.title + '////' + req.body.content,
    quality_control: "NORMAL"
  };
  // 已获得 access_token
  baiduApi.apiSearchFacee(searchParams, function (searchResponse) {
    // 成功----search
    switch (searchResponse.error_msg) {
      case 'SUCCESS':
        const user = searchResponse.result.face_list[0].user_list[0];
        if (user.score >= 80) {
          // 认为存在同一张人脸， 可以将图片上传到对应的user_group中
          addParams.user_id = user.user_id;
          post.user_id = addParams.user_id;
          baiduApi.apiAddFace(addParams, function (addResponse) {
            // 成功----add
            baiduApi.successHandle(addResponse, post, res, 201, '该人脸用户已存在，已将图片插入该用户列表下');
          }, function (error) {
            // 失败----add
            baiduApi.errorHandle(error, res);
            removeFile(req.file.path);
          })
        } else {
          // 认为该人脸对应用户不存在，则新建user组并插入图片
          post.user_id = addParams.user_id;
          baiduApi.apiAddFace(addParams, function (addResponse) {
            // 成功----add
            baiduApi.successHandle(addResponse, post, res, 201, '该人脸用户不存在，已新建用户并插入图片');
          }, function (error) {
            // 失败----add
            baiduApi.errorHandle(error, res);
            removeFile(req.file.path);
          })
        }
        break;
      case 'match user is not found':
        // 人脸库为空
        post.user_id = addParams.user_id;
        baiduApi.apiAddFace(addParams, function (addResponse) {
          // 成功----add
          baiduApi.successHandle(addResponse, post, res, 201, '该人脸用户不存在，已新建用户并插入图片');
        }, function (error) {
          // 失败----add
          baiduApi.errorHandle(error, res);
          removeFile(req.file.path);
        })
        break;
      default:
        // 上传图片存在问题
        baiduApi.errorHandle(searchResponse.error_msg, res);
        removeFile(req.file.path);
        break;
    }
  }, function (error) {
    // 失败----search
    res.status(500).json({
      message: '服务器出错\n' + error.message
    })
    removeFile(req.file.path);
  })
}

exports.getPosts = (req, res, next) => {
  const pageSize = +req.query.pageSize;
  const currentPage = +req.query.page;
  const postQuery = Post.find();
  let fetchedPosts;
  if (pageSize && currentPage) {
    postQuery
      .skip(pageSize * (currentPage - 1))
      .limit(pageSize);
  }
  postQuery
    .then(documents => {
      fetchedPosts = documents;
      return Post.count();
    })
    .then(count => {
      res.status(200).json({
        message: '获取人脸数据成功！',
        posts: fetchedPosts,
        maxPosts: count
      });
    })
    .catch(error => {
      res.status(500).json({
        message: '服务器出错\n' + error.message
      })
    })
};

exports.deletePost = (req, res, next) => {
  const baseUrl = req.protocol + '://' + req.get('host');
  const imagePath = req.body.imagePath.replace(baseUrl + '/', '');
  const deleteParams = {
    group_id: 'group1',
    user_id: req.body.user_id,
    face_token: req.body.face_token
  }
  baiduApi.apiDeleteFace(deleteParams, function (deleteRes) {
    if (deleteRes.error_code === 0) {
      Post.deleteOne({ _id: req.params.id, creator: req.userData.userId })
        .then(result => {
          if (result.n > 0) {
            res.status(200).json({ message: 'Face deleted!' });
            removeFile(imagePath);
          } else {
            res.status(401).json({ message: '没有删除权限！' });
          }
        }).catch((error) => {
          res.status(500).json({ message: error.message });
        })
    } else {
      res.status(400).json({ message: deleteRes.error_msg });
    }
  }, function(error) {
    res.status(500).json({ message: '服务器出错\n' + error.message });
  })
};

exports.searchPost = (req, res, next) => {
  // 先读出图片转换为bese64格式
  let img = fs.readFileSync(req.file.path);
  let encode_image = img.toString('base64');
  const searchParams = {
    image: encode_image,
    image_type: 'BASE64',
    group_id_list: 'group1',
    match_threshold: 10
  }

  // 删除查询图片
  removeFile(req.file.path);
  baiduApi.apiSearchFacee(searchParams, function (searchResponse) {
    switch (searchResponse.error_msg) {
      case 'SUCCESS':
        // 人脸查询成功
        const SCORE_LINE = 10;
        const user_list = searchResponse.result.face_list
        const faces_info = user_list.map(
          item => {
            let itemMap = item.user_list[0];
            return {
              location: item.location,
              face_info: {
                score: itemMap.score,
                name: itemMap.score > SCORE_LINE ?  itemMap.user_info.substring(0, itemMap.user_info.indexOf("////")) : '无该人脸姓名数据',
                info: itemMap.score > SCORE_LINE ?  itemMap.user_info.substring(itemMap.user_info.indexOf("////") + 4) : '无该人脸信息数据'
              }
            }
          }
        )
        res.status(200).json({
          message: "查询成功！",
          result: faces_info
        })
        break;
      case 'match user is not found':
        // 人脸库为空
        res.status(400).json({
          message: "查询失败， 人脸库为空！"
        })
        break;
      default:
        // 上传图片有错误
        res.status(400).json({
          message: searchResponse.error_msg
        })
        break;
    }
  }, function (error) {
    res.status(500).json({
      message: '服务器出错\n' + error.message
    })
  })
};

exports.detectPost = (req, res, next) => {
  // 先读出图片转换为bese64格式
  let img = fs.readFileSync(req.file.path);
  let encode_image = img.toString('base64');
  const detectParams = {
    image: encode_image,
    image_type: 'BASE64',
    face_field: 'age,beauty,expression,face_shape,gender,race,emotion,face_type',
    max_face_num: 10
  };
  // 删除查询图片
  removeFile(req.file.path);
  baiduApi.apiDetectFace(detectParams, function(detectRes) {
    switch (detectRes.error_msg) {
      case 'SUCCESS':
        // 人脸检索成功
        res.status(200).json({
          result: detectRes.result,
          message: '人脸检测成功！'
        })
        break;
      default:
        // 人脸检索失败
        res.status(400).json({
          message: detectRes.error_msg
        })
        break;
    }
  }, function(error) {
    res.status(500).json({
      message: '服务器出错\n' + error.message
    })
  })
};

exports.comparePost = (req, res, next) => {
  let img1 = fs.readFileSync(req.files[0].path);
  let encode_image_1 = img1.toString('base64');
  let img2 = fs.readFileSync(req.files[1].path);
  let encode_image_2 = img2.toString('base64');
  for (const file of req.files) {
    removeFile(file.path);
  }
  const compareParams = [
    {
        "image": encode_image_1,
        "image_type": "BASE64",
        "quality_control": "LOW"
    },
    {
        "image": encode_image_2,
        "image_type": "BASE64",
        "quality_control": "LOW"
    }
  ];
  baiduApi.apiCompareFace(compareParams, function(compareRes) {
    switch (compareRes.error_msg) {
      case 'SUCCESS':
        res.status(200).json({
          message: '对比成功',
          result: {
            score: compareRes.result.score
          }
        })
        break;
      default:
        res.status(400).json({
          message: compareRes.error_msg
        })
        break;
    }
  }, function(error) {
    res.status(500).json({
      message: '服务器出错\n' + error.message
    })
  })
};

exports.updatePost = (req, res, next) => {
  let imagePath = req.body.imagePath;
  if (req.file) {
    const url = req.protocol + '://' + req.get('host');
    imagePath = url + '/images/' + req.file.filename;
  }
  const post = new Post({
    _id: req.body.id,
    title: req.body.title,
    content: req.body.content,
    imagePath: imagePath
  })
  Post.updateOne({ _id: req.params.id, creator: req.userData.userId }, post).then(result => {
    if (result.nModified > 0) {
      res.status(200).json({ message: 'updated successfully!' });
    } else {
      res.status(401).json({ message: '没有更新权限！' });
    }
  })
};

exports.getPost = (req, res, next) => {
  Post.findById(req.params.id).then(post => {
    if (post) {
      res.status(200).json(post);
    } else {
      res.status(404).json({ message: 'face not found!' });
    }
  })
};
