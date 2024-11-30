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

package net.ankio.auto.xposed.hooks.android.hooks

import android.app.Application
import android.app.Notification
import com.google.gson.Gson
import com.google.gson.JsonObject
import net.ankio.auto.xposed.core.api.HookerManifest
import net.ankio.auto.xposed.core.api.PartHooker
import net.ankio.auto.xposed.core.hook.Hooker
import net.ankio.auto.xposed.core.utils.AppRuntime
import net.ankio.auto.xposed.core.utils.DataUtils
import net.ankio.auto.xposed.core.utils.MD5HashTable
import org.ezbook.server.constant.DataType
import org.ezbook.server.constant.Setting


class NotificationHooker : PartHooker() {
    private var selectedApps = listOf<String>()
    private val hashTable = MD5HashTable()
    override fun hook() {
        Hooker.allMethodsEqBefore(
            Hooker.loader("com.android.server.notification.NotificationManagerService"),
            "enqueueNotificationInternal",
        ) { param ->
            val app = param.args[0] as String
            val opkg = param.args[1] as String

            var notification: Notification? = null

            for (i in 0 until param.args.size) {
                if (param.args[i] is Notification) {
                    notification = param.args[i] as Notification
                    break
                }
            }

            if (notification == null) {
                return@allMethodsEqBefore null
            }


            val originalTitle = runCatching {
                notification.extras.getString(Notification.EXTRA_TITLE) ?: ""
            }.getOrElse { "" }
            val originalText = runCatching {
                notification.extras.getString(Notification.EXTRA_TEXT) ?: ""
            }.getOrElse { "" }


            AppRuntime.manifest.logD("app: $app, opkg: $opkg, originalTitle: $originalTitle, originalText: $originalText")

            val hash = MD5HashTable.md5("$app$originalTitle$originalText")
            if (hashTable.contains(hash)) {
                AppRuntime.manifest.logD("hashTable contains $hash, $originalTitle, $originalText")
                return@allMethodsEqBefore null
            }
            hashTable.add(hash)


            val data = DataUtils.configString(Setting.LISTENER_APP_LIST, "")
            AppRuntime.manifest.logD("data: $data")
            selectedApps = data.split(",").filter { it.isNotEmpty() }
            checkNotification(
                app,
                originalTitle,
                originalText,
                selectedApps
            )

        }
    }

    /**
     * 检查通知
     */
    private fun checkNotification(
        pkg: String,
        title: String,
        text: String,
        selectedApps: List<String>,
    ) {
        if (title.isEmpty() && text.isEmpty()) {
            return
        }

        if (!selectedApps.contains(pkg)) {
            return
        }


        val json = JsonObject()
        json.addProperty("title", title)
        json.addProperty("text", text)
        json.addProperty("t",System.currentTimeMillis())

        AppRuntime.manifest.logD("NotificationHooker: $json")

        AppRuntime.manifest.analysisData(DataType.NOTICE, Gson().toJson(json), pkg)
    }



}