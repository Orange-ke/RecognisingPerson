# RecognisingPerson

## 组员名单

> 王泽 柯阳 邵佳鑫 段乘风 娄斌.

## 项目亮点
### 原生Android

    1：实现了建库/查询/对比等基础功能
    
    2：实现了侧滑栏形式导航功能
    
    3：解决了图片预览方向与拍摄方向不一致问题
    
    4：拓展了人脸选取并查询详细信息（如年龄，颜值，表情等）的功能
    
### NodeJs服务端

    5：后台实现了更好管理人脸图片库的功能，可更方便的定位需要删除的人脸数据，删除单张人脸信息

### 技术扩展

    6：实现了web端单页应用，在原生app功能的基础上扩展了 找出颜值最高人脸功能 和 用户注册登陆功能，实现了图片管理与用户信息结合
    
    7：实现了混合式App开发<HybirdApp>
    
    8: 使用云服务器管理后台应用和web App
    
    9: 使用spring boot 实现批量上传功能
    
网页地址：[WebApp](http://www.wangzze.com)

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

1: 用户注册

    接口地址：
    
        http://122.51.169.168/api/user/signup

    接口能力：
    
        注册新用户，保证注册用emial唯一性， 加密用户密码

    请求说明：
    
        格式：form-data,
        请求参数：{ emial: String, password: String }

    返回结果：
    
        成功status(201)：
            { 
                message: "用户已创建" 
            }, 
        失败status(500)：
            {
                message: error.message 
            }

2: 用户登陆

    接口地址：
    
        http://122.51.169.168/api/user/login

    接口能力：
    
        已注册用户登陆，使用Web JWT生成token，并设置过期时间

    请求说明：
    
        格式：form-data，
        请求参数：{ emial: String, password: String }

    返回结果：
    
        成功status(200)：
            { 
                message: "登陆成功"，
                result: { token：token, userId: userId, expiresIn: 3600 } 
            }, 
        失败status(401)：
            { 
                message: "账户不存在" 
            } || { 
            message: "密码错误" 
            }， 
            失败status(500): 
            { 
                message: error.message 
            }

> 人脸识别接口

1: 人脸对比接口

    接口地址：
    
        http://122.51.169.168/api/posts/compare

    接口能力: 
    
        对接baidu人脸对比接口，将用户上传的两张人脸file类型图片文件转换为base64格式并添加必要的参数，调用百度人脸对比接口，返回人脸对比结果，筛选必要信息

    请求说明：
    
        格式：form-data, 需要设置http 请求头部信息 Authorization：登陆后获取的token, 请求body 参数: { image1: File, image2: File }

    返回结果：
    
        成功status(200): 
            { 
                message: "对比成功", 
                result: { score: 78.42330933 } 
            }, 
        失败status(401)：
            { 
                message： "无权限" 
            }, 
        status(500)： 
            { 
                message: error.message 
            }

2: 人脸检测接口

    接口地址：
    
        http://122.51.169.168/api/posts/detect

    接口能力: 
    
        对接baidu人脸检测接口，将用户上传的单张带人脸图片从file类型文件转换为base64格式并添加必要的参数，调用百度人脸检测接口，返回人脸检测结果，删选必要信息

    请求说明：
    
        格式：form-data, 需要设置http 请求头部信息 Authorization：登陆后获取的token, 请求body 参数: { image: File }

    返回结果：
    
        成功status(200): 
            { 
                message: "人脸检测成功", 
                result: { location, user_list } 
            }, 
        失败status(401)：
            { 
                message： "无权限" 
            }, 
        status(500)：
            { 
                message: error.message 
            }

3: 人脸查询接口

    接口地址：
    
        http://122.51.169.168/api/posts/search

    接口能力: 
    
        对接baidu多张人脸搜索接口，将用户上传单张带人脸图片从file类型文件转换为base64格式并添加必要的参数，调用百度人脸搜索接口，返回人脸搜索结果，删选必要信息

    请求说明：
        
        格式：form-data, 
        需要设置http 请求头部信息 Authorization：登陆后获取的token, 
        请求body 参数: { image: File }

    返回结果：
        
        成功status(200): 
            {
                message: "查询成功", 
                result: { location, faces_info } 
            }, 
        失败status(401)：
            { 
                message： "无权限" 
            }, 
        status(500)：
            {
                message: error.message 
            }

4：人脸上传接口

    接口地址：
    
        http://122.51.169.168/api/posts

    接口能力: 
    
        对接baidu人脸添加接口，将用户上传图片从file类型文件转换为base64格式并添加必要的参数，调用百度人脸搜索接口，返回人脸搜索结果，若人脸相似分大于80分则判定存在该人脸，将该人脸保存到同一user_id下， 若分数小于80分，则判定无该人脸则新建user_id, 调用baidu添加人脸接口，成功分会后将用户上传单张带人脸照片和信息保存到服务器中， 返回相应的结果，中间流程出错都返回错误信息。

    请求说明：
    
        格式：form-data, 
        需要设置http 请求头部信息 Authorization：登陆后获取的token, 
        请求body 参数: { title: String, content: String, image: File }

    返回结果：
    
        成功status(201): 
            { 
                message: "该人脸用户已存在，已将图片插入该用户列表下", 
                result: { location, faces_info } 
            }, 
        成功status(201): 
            { 
                message: "该人脸用户不存在，已新建用户并插入图片", 
                result: { location, faces_info } 
            }
        失败status(401)：
            { 
                message： "无权限" 
            }, 
        失败status(400)：
            { 
                message： "图片存在问题" 
            }, 
        失败status(500)：
            { 
                message: error.message 
            }
    
5：人脸删除接口

    接口地址：
    
        http://122.51.169.168/api/posts/:id

    接口能力: 
        
        对接baidu人脸删除接口， 根据用户发送的faca_token调用百度人脸删除接口，成功返回后同时删除服务器本地信息，返回删除结果

    请求说明：
    
        格式：application/json, 
        需要设置http 请求头部信息 Authorization：登陆后获取的token, 
        请求body 参数: { post_id, {user_id: String, face_token: String, imagePath: String } }

    返回结果：
    
        成功status(201): 
            { 
                message: "人脸删除成功" 
            }, 
        失败status(401)：
            { 
                message： "无权限" 
            }, 
        失败status(400)：
            { 
                message："face_token无效" 
            }, 
        失败status(500)：
            { 
                message: error.message
            }
    
5：获取人脸数据接口

    接口地址：
    
        http://122.51.169.168/api/posts

    接口能力: 
    
        从服务器返回分页人脸数据

    请求说明：
    
        格式：application/json, 
        请求body 参数: { postsPerPage: Number, currentPage: Number }

    返回结果：
    
        成功status(201): 
            { 
                message: '获取人脸数据成功',
                posts: fetchedPosts,
                maxPosts: count 
            }, 
        失败status(500):
            { 
                message: error.message
            }


### 特别鸣谢

感谢@johnyu666 老师的悉心指导






