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

### RESTFul网络接口设计REST



