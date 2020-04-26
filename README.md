# RubanInit
Android App 相关库的初始化管理

[![](https://jitpack.io/v/owner-liu/rubaninit.svg)](https://jitpack.io/#owner-liu/rubaninit)

Add it in your root build.gradle at the end of repositories:
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

Add the dependency
```
	dependencies {
	        implementation 'com.github.owner-liu:rubaninit:version'
	}
```

#### 示例代码：
```
 AppInitManager.apply {
            addLibInit(TestThreadALib())
            addLibInit(TestThreadBLib())
            addLibInit(TestThreadCLib())

            configModule(ModuleConfig(1, false, arrayListOf("2")))

            addModuleCompletedListener(
                hashSetOf(1),
                object : ICompleteListener {
                    override fun onCompleted() {
                        Log.i("ruban", "监听到相关module 已完成～")

                        initializeLazyAll()
                    }
                })

            addInitCompletedListener(1, "TestThreadALib", object : ICompleteListener {
                override fun onCompleted() {
                    Log.i("ruban", "监听到指定lib初始化完成～")
                }
            })

            addAppCompletedListener(object : ICompleteListener {
                override fun onCompleted() {
                    Log.i("ruban", "监听到全部初始化完成～")
                }

            })

            setLogger(CustomizationLogger())

            initialize(application, isDebug);
        }
```
```
class TestThreadALib : LibInit(1, ConstantsForCore.THREAD_ASYNC, arrayListOf()) {
    //当前init别名，如果其他的库有依赖于当前库，需要填写，防止混淆时，无法找到对应的init～
    override fun getAliasName(): String {
        return "TestThreadALib"
    }
    //初始化逻辑编写地方
    override fun doInit(context: InitContext, observer: IInitObserver) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Log.i("ruban", "err-线程异常-init:${getAliasName()}")
        }
    }
}
```
```
class CustomizationLogger : ILogger {
    override fun d(tag: String, msg: String) {

    }

    override fun e(tag: String, msg: String) {
    }

    override fun i(tag: String, msg: String) {
    }

    override fun v(tag: String, msg: String) {
    }

    override fun w(tag: String, msg: String) {
    }

}
```

#### 说明：
##### 1。初始化
      1。module～
              1。库的初始化以module为界
              2。module 可以设置延迟加载～
              3。module 之间可以存在依赖关系～
      2。lib
              1。lib 可以设置指定线程执行～
              2。lib 之间可以存在 module内的依赖关系

 ##### 2。监听
      1。可以设置单个或者多个module的初始化完成监听～
      2. 可以设置对应module内的指定Init的初始化完成监听～
      3。可以设置所有的module初始化完成监听（包含延迟初始化的module）～

 ##### 3。针对于延迟初始化～
      1。延迟初始化的库之间禁止存在依赖关系～

 ##### 4.优化：
      1。插队策略
      2。异步并发
      3. 初始化（依赖结果的，通过addInitCompletedListener，初始化需要的，直接添加depend～）

![](https://github.com/owner-liu/pic/blob/master/ruban_uml.jpg)
