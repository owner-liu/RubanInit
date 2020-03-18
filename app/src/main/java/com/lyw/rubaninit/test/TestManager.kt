package com.lyw.rubaninit.test

import android.app.Application
import android.util.Log
import com.lyw.ruban.AppInitManager
import com.lyw.ruban.core.IDependInitObserver
import com.lyw.ruban.core.IInitObserver
import com.lyw.ruban.core.InitContext
import com.lyw.ruban.core.depend.AbsDependInit
import com.lyw.ruban.ICompleteListener
import com.lyw.ruban.init.app.LazyAppDependInit
import com.lyw.ruban.init.module.comm.ModuleInit
import com.lyw.ruban.init.module.depend.ThreadListExternalDependModuleInit
import com.lyw.ruban.init.widgets.depend.DependThreadLibInit
import com.lyw.rubaninit.test.comm.TestLib
import com.lyw.rubaninit.test.comm.TestLibCopy
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
//        testAppLazyDependInit()
        testAppLazyDependThreadInit(application, isDebug)
    }

    private val mDependObserver = object : IDependInitObserver {
        override fun onCompleted(context: InitContext, aliasName: String) {
            Log.i("ruban_test", "onCompleted-$aliasName-${System.currentTimeMillis()}")
        }

        override fun onWaitToInit(
            context: InitContext,
            init: AbsDependInit<IDependInitObserver>,
            dependAliasName: String
        ) {
            Log.i("ruban_test", "onWaitToInit-$dependAliasName-${System.currentTimeMillis()}")
        }
    }


    private val mObserver = object : IInitObserver {
        override fun onCompleted(context: InitContext, aliasName: String) {
            Log.i("ruban_test", "onCompleted-$aliasName-${System.currentTimeMillis()}")
        }

    }

    private val mCompleteObserver = object : ICompleteListener {
        override fun onCompleted() {
            Log.i("ruban_test", "全部初始化完毕！")
        }
    }


    /**
     * 无任何依赖关系～
     */
    private fun testWithoutDepend(application: Application, isDebug: Boolean) {
        var initContext = InitContext(null, true)
        ModuleInit(1).apply {
            addInit(TestLibCopy())
            addInit(TestLib())
        }.initialize(initContext, mObserver)
    }

    /**
     * module内，线程集合间存在依赖～
     */
    private fun testModuleDependWithAlias(application: Application, isDebug: Boolean) {
        var initContext = InitContext(null, true)
        ThreadListExternalDependModuleInit(2).apply {
            addInit(
                DependThreadLibInit(
                    TestExternalDependLibCopy()
                )
            )
            addInit(DependThreadLibInit(TestExternalDependLib()))
        }.initialize(initContext, mDependObserver)
    }

    private fun testAppDependInit(application: Application, isDebug: Boolean) {
        AppInitManager.apply {
            addLibInit(TestModuleADependLibCopy())
            addLibInit(TestModuleBDependLibCopy())
            addLibInit(TestModuleCDependLibCopy())
            addModuleDependAlias(1, arrayListOf("2"))
            addModuleDependAlias(2, arrayListOf("3"))
            addModuleCompletedListener(
                hashSetOf(3),
                object : ICompleteListener {
                    override fun onCompleted() {
                        Log.i("ruban_test", "监听到相关module 已完成～")
                    }

                })
            addAppCompletedListener(object :
                ICompleteListener {
                override fun onCompleted() {
                    Log.i("ruban_test", "全部初始化完毕！")
                }
            })
            initialize(application, isDebug)

            addAppCompletedListener(object :
                ICompleteListener {
                override fun onCompleted() {
                    Log.i("ruban_test", "全部初始化完毕～")
                }
            })
        }
    }

    private fun testAppLazyDependInit(application: Application, isDebug: Boolean) {
        AppInitManager.apply {
            addLibInit(TestModuleADependLibCopy())
            addLibInit(TestModuleBDependLibCopy())
            addLibInit(TestModuleCDependLibCopy())
            addModuleDependAlias(1, arrayListOf("2"))
            addModuleDependAlias(2, arrayListOf("3"))
            setModuleLazy(2)
            addModuleCompletedListener(
                hashSetOf(3),
                object : ICompleteListener {
                    override fun onCompleted() {
                        Log.i("ruban_test", "监听到相关module 已完成～")

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
            addModuleDependAlias(1, arrayListOf("2"))
            addModuleCompletedListener(
                hashSetOf(1),
                object : ICompleteListener {
                    override fun onCompleted() {
                        Log.i("ruban_test", "监听到相关module 已完成～")

                        initializeLazyAll()
                    }
                })

            addAppCompletedListener(mCompleteObserver)

            initialize(application, isDebug);
        }
    }
}