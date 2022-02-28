package com.trungpd.biliso.base

import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.math.max
import kotlin.math.min

class WaExecutor {
    companion object {
        var value1: Int = 0
        var value2: Int = 0
        var value3: Int = 0
        var value4: Int = 0

        var executor1 = Executors.newFixedThreadPool(max(2, min(Runtime.getRuntime().availableProcessors() - 1, 4)))
        var executor2 = Executors.newFixedThreadPool(max(2, min(Runtime.getRuntime().availableProcessors() - 1, 4)))


    }


}