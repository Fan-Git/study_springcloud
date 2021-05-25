
```text
启动:
服务注册中心: eureka
网关: zuul
小程序后台数据服务：naicha_app
管理后台数据服务: naicha_system
```

- 首页
- 点餐（到店自取和外卖配送两种方式，有基本的点餐逻辑处理）
- 取餐
- 我的资料
- 收货地址
- 我的订单
- 订单详情

### 数据库
用户
商品类别
商品信息
收货地址
订单
订单评价

#### 订单的状态
```text
处理状态:
未付款: 已下单但是付款失败
制作中: 成功下单且已付款了
请取餐: 到店自取方式时已经制作完等用户自己取餐
配送中: 外卖配送方式时的配送
已送达: 已取餐或外卖已经送达
退款中: 没有商品或其他原因时的退款处理，管理员操作
订单完成状态:
已完成: 用户自己确认收货，或者管理员确认收货
已取消: 未付款时用户可以直接取消(用户取消订单则直接删除该订单)，
    管理员取消订单时不能删除订单(同时填入取消原因)
已退款: 后台成功退款

```


```text
商品列表信息从本地缓存(ConcurrentHashMap实现)里直接获取，定时任务定时刷新
商品列表信息获取时用redis做乐观锁限制一个用户去请求到数据库
对部分接口进行限流
使用java模拟简单的消息队列实现订单的提醒
利用ConcurrentHashMap写了一个本地二级缓存
```
###线上部署
```text
重新启动redis时 config set requiredPass 密码
设置docker的端口号和挂载的静态图片目录  /image  /image
// Linux部署
-XX:+PrintGCDetails  
-Xloggc:/log/naicha/gc.log  // gc的日志文件
-Duser.timezone=GMT+08 // 时区，不然时间不对 
使用命令: nohup java -jar XXXXXX.jar &  
查看进程: ps -ef|grep java
停止进程: kill  进程id
 nohup java  -Xms2500m  -Xmx2500m -XX:+PrintGCDetails -Xloggc:/log/naicha/gc.log -Duser.timezone=GMT+08 -jar lifei_naicha-1.0-SNAPSHOT.jar &
eureka服务:
 nohup java  -Xms256m  -Xmx256m  -Duser.timezone=GMT+08 -jar eureka-server-1.0-SNAPSHOT.jar &
zuul网关: 
 nohup java  -Xms1024m  -Xmx1024m  -Duser.timezone=GMT+08 -jar zuul-1.0-SNAPSHOT.jar &
```

```text
微信小程序支付流程:
1 用户提交订单，服务器先生成订单(在生成订单的时候加锁防止用户重复下单)，后台生成订单号返回
3 微信预下单，后台调微信的统一下单接口(填商家号，openid等信息)返回微信支付所需参数对象给小程序调起支付用
4 微信小程序支付成功后更新订单状态与支付时间(在支付后通过微信支付的回调来再次更新订单状态和支付时间以及微信交易号，
每天检查没有填入交易号的订单再次主动查询订单支付情况)
5 小程序根据返回的支付所需的参数对象去调用微信原生支付接口，根据用户支付结果去修改订单状态

```

```text

数据库的字段名取名不要取 order,option等,mybatis不会加``反单引号会导致SQL执行异常
js计算浮点数不准确，后端用BigDecimal表示小数时，前端的小数计算极其不准确，
简单使用redis的乐观锁防止用户重复下单。
同一个mapper里的方法不支持重载


update goods_category set show_status= !show_status; // 不报错
update goods_category set show_status=!show_status; // 报错？？


商品静态图片的问题
图片的上传到服务器，docker部署的时候挂载容器里的目录到Linux本地服务器文件夹，从而实现静态图片资源的上传和获取.


```

```text
  商品静态图片的问题，docker文件的挂载
  项目部署问题(2台服务器，小程序只能访问那个备案了的url，就通过微服务多一层转发)
  订单号的生成(时间戳+AtomicInteger)
  订单业务相对(订单的流程，状态的改变)
  数据库表的设计(索引加少部分冗余(比如之前的商品表的goods_category_name))，数据的合法性
  用户的授权认证(利用jwt无状态的特性)
  获取和生成商品菜单数据时慢(用本地缓存，通过Redis去判断缓存的商品数据是否失效，在获取商品菜单时用到分布式锁)
  服务器上Java的时区问题和MySQL服务的时区问题
  前台和后台分离后，服务间的调用
  后台权限表的配置
  对一些比较铭感和反应慢的接口限流
  异步消息回调(通知用户取餐，暂时可以用定时任务轮询redis的消息；新订单提醒，Ajax轮询)

```
