/*
 * Copyright (C) 2025 ankio(ankio@ankio.net)
 * Licensed under the Apache License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-3.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package net.ankio.auto.xposed.hooks.qianji.models

import de.robv.android.xposed.XposedHelpers
import net.ankio.auto.xposed.core.utils.AppRuntime

object UserModel {
    private val userManagerClazz by lazy {
        AppRuntime.clazz("UserManager")
    }

    private fun getInstance(): Any {
        return XposedHelpers.callStaticMethod(userManagerClazz, "getInstance")
    }

    fun isLogin(): Boolean {
        return XposedHelpers.callMethod(getInstance(), "isLogin") as Boolean
    }

    fun getLoginUserID(): String {
        return XposedHelpers.callMethod(getInstance(), "getLoginUserID") as String
    }

    fun isVip(): Boolean {
        return XposedHelpers.callMethod(getInstance(), "isVip") as Boolean
    }
}