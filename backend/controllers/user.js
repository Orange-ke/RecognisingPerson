
const bcrypt = require('bcryptjs');

const jwt = require('jsonwebtoken');

const User = require('../models/user');

exports.createUser = (req, res, next) => {
  bcrypt.hash(req.body.password, 10)
    .then(hash => {
      const user = new User({
        email: req.body.email,
        password: hash
      })
      user.save()
      .then(result => {
        res.status(201).json({
          message: '用户已创建'
        })
      })
      .catch(error => {
        res.status(500).json({
          message: error.message
        })
      })
    })
}

exports.loginUser = (req, res, next) => {
  User.findOne({ email: req.body.email })
  .then(user => {
    if (!user) {
      return res.status(401).json({
        message: "用户名不存在"
      });
    }
    fetchedUser = user;
    return bcrypt.compare(req.body.password, user.password);
  })
  .then(result => {
    if (!result) {
      return res.status(401).json({
        message: "密码错误"
      })
    }
    const token = jwt.sign({ email: fetchedUser.email, userId: fetchedUser._id}, 'secret_this_should_be_longer', {expiresIn: '1h'});
    res.status(200).json({
      token: token,
      userId: fetchedUser._id,
      expiresIn: 3600
    });
  })
  .catch(err => {
    console
    return res.status(401).json({
      message: "未知错误"
    })
  })
}
