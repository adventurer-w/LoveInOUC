# 海缘——OUCer的表白交友APP

## 1 项目介绍

在表白墙等地方常常听看到同学们的交友、求助的声音。但由于QQ空间的限制，很难保证消息的实时性，并且只能查看近期的消息。针对这一痛点，「海缘」为广大校友提供了一个实时发表并查看动态的Android APP平台。

「海缘」这个名字有「在海大的缘分」的意思，希望大家能在海大认识那个ta~~~~

**后端使用 [*召](https://github.com/ruaabbit) 同学曾经写的一个新闻资讯平台的后台 ~~~**

[项目地址(github.com)](https://github.com/adventurer-w/LoveInOUC)

## 2 当前完成情况

功能基本实现，已上线运行



## 3 模块功能与技术

下面分用户模块和动态模块来介绍项目的功能



### 3.1 用户模块

#### 登录注册

1. **包含欢迎页、登录页、注册页、修改密码页等四个页面**
2. 若用户此前未登陆账号，则跳转至登陆界面。若无账号，则点击注册，注册账号
3. 使用邮箱注册账号，支持邮箱验证码
4. 支持通过邮箱修改密码
5. 对账号、密码的长度和字符有一定限制

#### 用户信息

1. **包含个人信息、我的关注、我的粉丝、编辑信息等四个页面**
2. 用户信息包括：昵称、性别、个性签名、头像
3. 支持修改用户信息，可自定义头像
4. 支持关注，查看自己的粉丝

#### 颜值评分

1. **包含一个页面**
2. 用户上传人像照片，使用深度学习方法对人像进行检查，输出性别、年龄、颜值评分。




### 3.2  动态模块

1. **包括推荐动态、关注用户动态、我发布的动态、动态详情、发表动态、历史收藏、历史点赞等七个页面**
2. 用户可以发布动态，并为之添加图片和文本
3. 支持为动态添加标签，有「表白」、「交友」、「约饭」、「吐槽」可选。
4. 可以选择查看全校同学的动态 或 自己关注用户的动态
5. 上拉加载，下拉刷新新闻
6. 支持删除动态
7. 支持评论
8. 支持收藏，点赞，并查看点赞数。



### 3.3 技术说明

#### 3.3.1 使用RecyclerView 并搭配 Adapter设计模式

##### RecyclerView

RecyclerView 是一个类似ListView一样的列表控件，但是功能更加强大，也支持横向滚动、瀑布滚动等，而且在子项点击事件上的处理更出色。

**使用RecyclerView实现列表的动态加载，项目实现不同item动态切换、并实现了Adapter的嵌套。首页动态列表，轮播图，粉丝列表，收藏列表，评论区等都使用到了RecyclerView。**



##### Adapter 设计模式

Adapter（适配器）模式把一个类的接口变换成客户端所期待的另一种接口，**从而使原本因接口不匹配 而无法在一起工作的两个类 能够在一起工作**。

适配器模式有以下角色：

1. Target（目标），这就是所期待得到的接口
2. Adaptee（源），现有需要适配的接口
3. Adapter（适配器），适配器类是本模式的核心。适配器把Adaptee转换成Target

<img src="https://s3.bmp.ovh/imgs/2022/08/31/c1e54e814ae6234a.png" style="zoom:40%;" />



下面**以首页的列表为例**，介绍具体实现方式。

##### XML文件

##### fragment_recommend.xml

<img src="https://s3.bmp.ovh/imgs/2022/08/31/68bfb9a0a1d1c845.png" style="zoom:23%;" />

##### item_news.xml (新闻item)

<img src="https://s3.bmp.ovh/imgs/2022/08/31/7a026fd49fe0e4fb.png" style="zoom:23%;" />

##### item_vp_item.xml (轮播图item)

<img src="https://s3.bmp.ovh/imgs/2022/08/31/5ec8a176cb4de444.png" style="zoom:23%;" />

##### JAVA文件

##### NewsAdapter.java（竖向，第一层）

这是新闻页的第一层列表，传入List\<Data\>后，该Adapter会进行解析并更新UI界面。详细逻辑见代码文件。

<img src="https://s3.bmp.ovh/imgs/2022/08/31/c57f00153d75cd39.png" style="zoom:23%;" />

##### ViewPagerAdapter.java（轮播图，横向，第二层）

这是新闻页的第二层列表，当NewsAdapter发现需要使用轮播图的item后，会在这个item里面再套一个RecyclerView，该RecyclerView的适配器就是ViewPagerAdapter。详细逻辑见代码文件。

<img src="https://s3.bmp.ovh/imgs/2022/08/31/9628fae13754cdfe.png" style="zoom:23%;" />



##### RecommendFragment.java

Fragment类在绑定UI控件后，向Adapter传入、更新、删除 List\<Data\>数据即可实现列表的动态加载。下面展示了在HTTP请求后，得到新闻列表数据，更新list，再传入Adapter的代码。

<img src="https://s3.bmp.ovh/imgs/2022/08/31/c29f2dbd02eee8fc.png" style="zoom:33%;" />

##### 最终效果

<img src="https://s3.bmp.ovh/imgs/2022/08/31/5f14d8f5c2447d54.png" style="zoom:30%;" />



#### 3.3.2  使用Fragment，并实现Fragment的两层嵌套

fragment（碎片）可以理解为一个小的activity，点击不同的item ,会显示不同的界面 , 这个界面就是fragment。可以在多个activity中重复使用一个fragment，所以可以把fragment视为activity中的模块化的组成部分。使用Fragment可以让程序更加高效，使页面更动态灵活，减少代码耦合度。

嵌套Fragments (Nested Fragments), 是在Fragment内部又添加Fragment。

在「海缘」中，主界面有「动态」、「发布」、「我」三个Fragment。

在「动态」中，又支持在「关注」和「推荐」两个界面进行滑动切换。

**值得一提的是，在Fragment的两层嵌套中，同样使用了Adapter设计模式**

<img src="https://s3.bmp.ovh/imgs/2022/08/31/c353bbb5a246d933.png" style="zoom:50%;" />

逻辑请见代码文件。



#### 3.3.3 下拉刷新，上拉加载

贴吧类APP显然是需要支持下拉刷新页面 和 上拉加载更多的。我使用了智能刷新框架 — smartRefreshLayout，我在onViewCreated这个生命周期实现了对上拉和下拉的监听，本项目几乎所有的列表都支持刷新与加载。

如下图，onLoadMore是上拉加载，onRefresh是下拉刷新。

<img src="https://s3.bmp.ovh/imgs/2022/08/31/9f137006a6808a6c.png" style="zoom:33%;" />



#### 3.3.4  轮播图

以下代码实现了轮播图滑动时的近大远小的效果

<img src="https://s3.bmp.ovh/imgs/2022/08/31/07e47f248d2999e4.png" style="zoom:40%;" />



#### 3.3.5 网络请求使用 OKHttp，并使用多线程

OKHttp是 Android 常用的网络请求框架，需要使用**多线程**。下面以Post为例展示代码

##### OKHttp 请求

<img src="https://i.bmp.ovh/imgs/2022/08/31/7dba8ce0b79e9d18.png" style="zoom:30%;" />

##### 获取返回数据，更新界面

<img src="https://i.bmp.ovh/imgs/2022/08/31/f82f47c0fb74042b.png" style="zoom:36%;" />



#### 3.3.6 使用SharedPreferences，支持数据的本地存储

APP运行过程中会产生状态和属性数据（如：是否登录，用户token，用户名等），由于其配置信息并不多，采用数据库的等耗时会影响程序的效率，因此我们使用键值对来存放这些配置信息。SharedPreferences正是Android中用于实现这中存储方式的技术。

**相比于数据库，SharedPreferences的实现更加简洁**。代码如下：

##### SharedPreferences Class：MyData.java

<img src="https://i.bmp.ovh/imgs/2022/08/31/e9157c7feb205c8b.png" style="zoom:40%;" />

##### 储存数据

<img src="https://i.bmp.ovh/imgs/2022/08/31/b1c39c61a6c00242.png" style="zoom:48%;" />

##### 获取数据

<img src="https://s3.bmp.ovh/imgs/2022/08/31/19c2621d1410b869.jpg" style="zoom:50%;" />



#### 3.3.8 人脸分析

使用旷视Face++ 人工智能开放平台提供的 人脸分析 API，[Face⁺⁺ - 文档中心 (faceplusplus.com.cn)](https://console.faceplusplus.com.cn/documents/4888383)。



#### 3.3.8 正则表达式

在注册的过程中，用户输入的账号、密码、邮箱等需要满足一下要求：

- 账号：6~12位字母或数字
- 密码：6~12位字母和数字
- 邮箱：合法的邮箱格式

因此，对它们格式的限制使用了正则表达式：

<img src="https://i.bmp.ovh/imgs/2022/08/31/bacaa614ceebc3a0.png" style="zoom:50%;" />



#### 3.3.9 部分矢量图标使用XML实现

<img src="https://s3.bmp.ovh/imgs/2022/08/31/6d74d3b66bfd9e1e.png" style="zoom:20%;" />

<img src="https://s3.bmp.ovh/imgs/2022/08/31/dbf7d64028014bd8.png" style="zoom:45%;" />



### 3.4 项目特色

- 完成度高：拥有较完善的用户系统 和 发布系统，基本实现市面上同类APP的功能
- 界面美观：对界面UI和动效进行了美化，观感较好，色彩氛围突出了「浪漫」这一主题
- 可玩性：测颜值的功能增加了趣味性。



## 4 测试及上线运行情况

### [演示视频（Bilibili）](https://www.bilibili.com/video/BV1DP4y1f7X4)

由于Android 特性，没有对不同Android系统和不同品牌的手机进行适配，在其他设备上运行可能会出现Bug



## 5 遇到的问题

### 5.1 写代码过程中出现各种各样的BUG

一定要掌握这三种DeBug方法！！

#### 5.1.2  掌握Debug模式

设置断点，监视变量。

![v5PFxJ.jpg](https://s1.ax1x.com/2022/08/31/v5PFxJ.jpg)

#### 5.1.3  使用Log输出日志

[![v5PoLR.jpg](https://s1.ax1x.com/2022/08/31/v5PoLR.jpg)](https://imgse.com/i/v5PoLR)

#### 5.1.4 学会查看异常信息

[![v5iCwt.jpg](https://s1.ax1x.com/2022/08/31/v5iCwt.jpg)](https://imgse.com/i/v5iCwt)



### 5.2  相机和相册使用时报错

原因：需要申请储存、读取、访问相机等限权，并且不同版本的安卓系统获取限权的写法不同

解决方法：上网查找相应代码与方法

#### 5.2.1 在Manifest中添加权限

- READ_EXTERNAL_STORAGE 读取外部存储空间
- WRITE_EXTERNAL_STORAGE 写入外部存储空间
- CAMERA 相机权限

```xml
<uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"
        tools:ignore="ProtectedPermissions"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.CAMERA"/>
```

#### 5.2.2 在代码中动态获取限权

```java
private void askPermission(){
        boolean sSRPR=ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)|
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)|
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA);
        Log.e("msg",Boolean.toString(sSRPR));
        if(sSRPR){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
            },0);
        }

    }

```

## 6 心得体会

大一的时候学过一段时间安卓APP，这次「海缘」算是重温了一遍安卓开发。大一学习的时候，对HTTP请求、try catch机制、继承与重写等知识都不甚了解，只会依葫芦画瓢。如今掌握了更多的面向对象编程知识后，再进行安卓APP的编写，思路更加清晰，也更注重代码的低耦合性。

「海缘」这个表白交友平台，基本实现了 市面上同类APP的基础功能，但如果真的所上线给所有同学运行，那也是非常困难的。且不论iOS用户无法使用，安卓的生态是十分混乱的，在不同版本的系统、不同品牌的手机上运行都可能会出现各种各样的问题，此外还要考虑服务器费用等问题。

 