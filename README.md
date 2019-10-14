# Android PushKit
目前仅集成了[华为推送](https://developer.huawei.com/consumer/cn/service/hms/catalog/huaweipush_agent.html)和[小米推送](https://dev.mi.com/console/doc/detail?pId=68)
<br>Just support huawei and xiaomi

## 使用
* **添加JitPack仓库到你项目的根build.gradle**
<br>Add JitPack repository in your root build.gradle:
```
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
```

* **添加依赖到你的app的build.gradle**
<br>Add the dependency:
```
    dependencies {
        implementation 'com.github.famik:pushkit:1.0.6'
    }
```

* **配置AppId / AppKey**
<br>在你的app的build.gradle里添加 manifestPlaceholders:
```
    defaultConfig {
        // ...此处省略其它已有代码

        manifestPlaceholders = [
            // 华为推送的AppId
            "huaweiPushAppId": "101195551",

            // 小米推送的AppId
            "xiaomiPushAppId": "2882303761518198184",
            "xiaomiPushAppKey": "5841819888184"
        ]
    }
```
<br>


* **初始化**
<br>在Application的onCreate里面调用 PushKit 的 init 方法
```
    PushKit.init(context)
```
初始化时会根据手机厂商选择推送平台，如果不是华为手机则使用小米推送<br>
<br>


* **设置委托**
```
    PushKit.setDelegate(delegate)
```

<span id="delegate">PushKit.Delegate 接口如下:</span>

```
    public interface Delegate {
        // token获取成功后回调
        void onPushTokenUpdated(Context context, String token);

        // 收到透传消息后回调
        void onPushMessageReceived(Context context, String msg);
    }
```
<br>


* **获取Token**
```
    PushKit.updateToken()
```
Token 获取成功后会通过 [PushKit.Delegate](#delegate) 接口回调<br>
由于华为推送平台的Token会过期，建议定时调用一下 updateToken

