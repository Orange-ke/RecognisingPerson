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

      email: { type: String, require: true, unique: true }, // 用户注册用邮箱

      password: { type: String, require: true }  // 用户账户密码

    })


2：人脸信息存储

    const faceSchema = mongoose.Schema({

      face_token: {type: String, require: true},  // 上传人脸后从百度获取的face_token 用于删除人脸时使用

      title: { type: String, require: true },  // 人脸姓名信息

      content: { type: String, require: true }, // 人脸身份信息

      imagePath: { type: String, require: true }, // 人脸照片存入服务器路径信息（由于百度接口不返回照片路径）

      creator: { type: mongoose.Schema.Types.ObjectId, ref: 'User', require: true } // 用于照片与上传者一一对应

    })



### RESTFul网络接口设计

> 用户注册和登陆

1： 用户注册

接口地址：http://122.51.169.168/api/user/login

接口能力：注册新用户，保证注册用emial唯一性

请求说明：格式：form-data 请求参数：{ emial: String, password: String }

返回结果：成功status(201)：{ message: "用户已创建" }, 失败status(500)：{ message: error.message }

> 人脸识别接口

> 人脸对比接口

接口地址：http://122.51.169.168/api/posts/compare

接口能力: 对接baidu人脸对比接口，将用户上传的两张人脸file类型文件转换为base64格式并添加必要的参数，访问百度对比接口，返回人脸对比结果，删选必要信息

请求说明：需要设置http 请求头部信息 Authorization：登陆后获取的token, 请求body 参数: { image1: File, image2: File }

返回结果：{ message: "对比成功", result:{ score: 78.42330933} }





