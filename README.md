# RecognisingPerson

## 第一部分 Native AndroidApp
### 类设计

![类图](https://orange-ke.github.io/RecognisingPerson/class.jpg)

### 单元测试

主要用于测试网络连接和读取文件测试

> ExampleUnitTest 

### 本地接口设计

安卓通过类`AccessBaiduManager`类里的静态函数与百度进行交互发送请求和处理返回结果

三类发送请求接口

> sendPhotoToMultiRecognize

> sendFaceForMoreInformation

> uploadFaceInform

两类处理返回结果接口

> handleResponseOfDetect

> handleResponseOfSearch

详细内容，见 [详细技术文档](https://orange-ke.github.io/RecognisingPerson/doc/index.html)

## 第二部分 NodeJS 后台

## 综述 使用MEAN(MongoDB, Express, Angular, NodeJS)全栈技术搭建后台RESTFul接口和相应的Web SPA。

### 数据库设计 （使用Mongoose 设计存储模式）

1：用户信息

    const userSchema = mongoose.Schema({

      email: { type: String, require: true, unique: true },

      password: { type: String, require: true }

    })


2：人脸信息存储

        const faceSchema = mongoose.Schema({
          user_id: {type: String, require: true},
          face_token: {type: String, require: true},
          title: { type: String, require: true },
          content: { type: String, require: true },
          imagePath: { type: String, require: true },
          creator: { type: mongoose.Schema.Types.ObjectId, ref: 'User', require: true }
        })


### RESTFul网络接口设计

#### 用户注册和登陆





