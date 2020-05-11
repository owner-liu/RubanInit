package com.lyw.rubaninit.test.depend

import android.os.Looper
import android.util.Log
import com.lyw.ruban.core.ConstantsForCore
import com.lyw.ruban.core.IInitObserver
import com.lyw.ruban.core.InitContext
import com.lyw.ruban.init.lib.LibInit

/**
 * Created on  2020-03-08
 * Created by  lyw
 * Created for
 */
class TestThreadDLib :
    LibInit(3, ConstantsForCore.THREAD_ASYNC) {
    override fun getAliasName(): String {
        return "TestThreadDLib"
    }

    override fun doInit(context: InitContext, observer: IInitObserver) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            context.logger.i(msg = "err-线程异常-alias:${getAliasName()}")
        }
    }
}