
const express = require('express');
const router = express.Router();

const multer = require('multer');
const checkAuth = require('../middleware/check-auth');

const postsController = require('../controllers/posts');

const MIME_TYPE_MAP = {
  'image/png': 'png',
  'image/jpeg': 'jpg',
  'image/jpg': 'jpg'
}

const storage = multer.diskStorage({
  destination: (req, file, cb) => {
    const isValid = MIME_TYPE_MAP[file.mimetype];
    let error = new Error('Invalid mime type');
    if (isValid) {
      error = null;
    }
    cb(error, 'images');
  },
  filename: (req, file, cb) => {
    const name = file.originalname.toLowerCase().split(' ').join('-');
    const extension = MIME_TYPE_MAP[file.mimetype];
    cb(null, name + '-' + Date.now() + '.' + extension);
  }
})

// 新建人脸 --需要先搜索是否存在该人脸，再决定插入已有用户还是新建用户组
router.post('', checkAuth, multer({storage: storage}).single('image'), postsController.addPost);

// 获取所有人脸数据
router.get('', postsController.getPosts);

// 删除数据库中的信息，存储的图片文件，并删除百度人脸库中的数据
router.delete("/:id", checkAuth, postsController.deletePost);

// 查询人脸信息，有则返回人脸信息，无则返回没有此人脸，通过在人脸库中找到得分最高的人脸（score > 80）确认
router.post("/search", checkAuth, multer({storage: storage}).single('image'), postsController.searchPost);

// 检测人脸
router.post("/detect", checkAuth, multer({storage: storage}).single('image'), postsController.detectPost);

// 对比两张人脸
router.post("/compare", checkAuth, multer({storage: storage}).array('images'), postsController.comparePost);

module.exports = router;
