package com.lyw.rubaninit.test

import android.app.Application
import android.util.Log
import com.lyw.ruban.AppInitManager
import com.lyw.ruban.core.IDependInitObserver
import com.lyw.ruban.core.IInitObserver
import com.lyw.ruban.core.InitContext
import com.lyw.ruban.core.depend.AbsDependInit
import com.lyw.ruban.ICompleteListener
import com.lyw.ruban.init.module.ModuleConfig
import com.lyw.ruban.init.widgets.depend.DependThreadLibInit
import com.lyw.rubaninit.test.depend.*

/**
 * Created on  2020-03-08
 * Created by  lyw
 * Created for test manager~
 */
object TestManager {
    fun test(application: Application, isDebug: Boolean) {
//        testWithoutDepend() //LibCopy->Lib
//        testModuleDependWithAlias() //Lib->LibCopy
//        testAppDependInit()
//        testAppLazyDependInit(application,isDebug)
        testAppLazyDependThreadInit(application, isDebug)
    }

    private val mDependObserver = object : IDependInitObserver {
        override fun onCompleted(context: InitContext, aliasName: String) {
        }

        override fun onWaitToInit(
            context: InitContext,
            init: AbsDependInit<IDependInitObserver>,
            dependAliasName: String
        ) {
        }
    }


    private val mObserver = object : IInitObserver {
        override fun onCompleted(context: InitContext, aliasName: String) {
        }

    }

    private val mCompleteObserver = object : ICompleteListener {
        override fun onCompleted() {
            Log.i("ruban", "监听到全部初始化完成～")
        }
    }

    private fun testAppDependInit(application: Application, isDebug: Boolean) {
        AppInitManager.apply {
            addLibInit(TestModuleADependLibCopy())
            addLibInit(TestModuleBDependLibCopy())
            addLibInit(TestModuleCDependLibCopy())

            configModule(ModuleConfig(1, false, arrayListOf("2")))
            configModule(ModuleConfig(2, false, arrayListOf("3")))

            addModuleCompletedListener(
                hashSetOf(3),
                object : ICompleteListener {
                    override fun onCompleted() {
                        Log.i("ruban", "监听到相关module 已完成～")
                    }

                })
            addAppCompletedListener(object :
                ICompleteListener {
                override fun onCompleted() {
                    Log.i("ruban", "全部初始化完毕！")
                }
            })
            initialize(application, isDebug)

            addAppCompletedListener(object :
                ICompleteListener {
                override fun onCompleted() {
                    Log.i("ruban", "全部初始化完毕～")
                }
            })
        }
    }

    private fun testAppLazyDependInit(application: Application, isDebug: Boolean) {
        AppInitManager.apply {
            addLibInit(TestModuleADependLibCopy())
            addLibInit(TestModuleBDependLibCopy())
            addLibInit(TestModuleCDependLibCopy())

            configModule(ModuleConfig(1, false, arrayListOf("2")))
            configModule(ModuleConfig(2, true, arrayListOf("3")))

            addModuleCompletedListener(
                hashSetOf(3),
                object : ICompleteListener {
                    override fun onCompleted() {
                        Log.i("ruban", "监听到相关module 已完成～")

                        initializeLazyAll()
                    }
                })

            addAppCompletedListener(mCompleteObserver)
            initialize(application, isDebug);
            addAppCompletedListener(mCompleteObserver)
        }
    }

    private fun testAppLazyDependThreadInit(application: Application, isDebug: Boolean) {
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

            addAppCompletedListener(mCompleteObserver)

            initialize(application, isDebug);
        }
    }
}