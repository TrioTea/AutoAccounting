/*
 * Copyright (C) 2024 ankio(ankio@ankio.net)
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

package net.ankio.auto.xposed

import net.ankio.auto.xposed.core.api.HookerManifest
import net.ankio.auto.xposed.core.utils.DataUtils
import net.ankio.auto.xposed.hooks.alipay.AliPayHooker
import net.ankio.auto.xposed.hooks.auto.AutoHooker
import net.ankio.auto.xposed.hooks.common.ServerHooker
import net.ankio.auto.xposed.hooks.qianji.QianjiHooker
import net.ankio.auto.xposed.hooks.wechat.WechatHooker
import org.ezbook.server.constant.DefaultData
import org.ezbook.server.constant.Setting


object Apps {
    /**
     * 虚拟框架无法hook到模块环境
     */
    fun getServerRunInApp():Pair<String,String>{
        // 或者运行于com.tencent.mm
     //  if (BuildConfig.DEBUG) return BuildConfig.APPLICATION_ID
        val wechat = DataUtils.configString(Setting.HOOK_WECHAT,  DefaultData.WECHAT_PACKAGE)
        return  Pair(wechat,"${wechat}:push")
    }


    fun get(): MutableList<HookerManifest> {
        return mutableListOf(
            ServerHooker(), // Server
           // AndroidHooker(), // Android
            AutoHooker(), // Auto
            ////////////////////////////
            // 记账App hook
            ////////////////////////////
            QianjiHooker(),
            ////////////////////////////
            // 哪些App可能发送记账账单？
            ////////////////////////////
            WechatHooker(), // Wechat
            AliPayHooker(), // AliPay
           // SmsHooker() // Sms
            ////////////////////////////
        )
    }
}