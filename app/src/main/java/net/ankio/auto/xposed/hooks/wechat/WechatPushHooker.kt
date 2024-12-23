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

package net.ankio.auto.xposed.hooks.wechat

import net.ankio.auto.xposed.core.api.HookerManifest
import net.ankio.auto.xposed.core.api.PartHooker
import net.ankio.auto.xposed.core.utils.DataUtils
import net.ankio.auto.xposed.hooks.common.CommonHooker
import net.ankio.dex.model.Clazz
import org.ezbook.server.constant.DefaultData
import org.ezbook.server.constant.Setting

class WechatPushHooker:HookerManifest() {
    val wechat = DataUtils.configString(Setting.HOOK_WECHAT,  DefaultData.WECHAT_PACKAGE)
    override val packageName: String
        get() = wechat

    override val appName: String
        get() = "微信 Push"

    override var processName: String
        get() = "$wechat:push"
        set(value) {}

    override val systemApp: Boolean
        get() = false

    override fun hookLoadPackage() {
        CommonHooker.init()
    }

    override var partHookers: MutableList<PartHooker> = mutableListOf()

    override var rules: MutableList<Clazz> = mutableListOf()
}