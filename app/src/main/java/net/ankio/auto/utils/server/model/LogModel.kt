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

package net.ankio.auto.utils.server.model

import com.google.gson.Gson
import com.google.gson.JsonArray
import kotlinx.coroutines.launch
import net.ankio.auto.utils.AppUtils

class LogModel:BaseModel(){
    var date = ""
    var app = ""
    var hook = 0
    var thread = ""
    var line = ""
    var level = 0
    var log = ""

    companion object {
        const val LOG_LEVEL_DEBUG = 0
        const val LOG_LEVEL_INFO = 1
        const val LOG_LEVEL_WARN = 2
        const val LOG_LEVEL_ERROR = 3

        fun put(logModel: LogModel) {
            AppUtils.getScope().launch {
                AppUtils.getService().sendMsg("log/put", logModel)
            }
        }


        fun deleteAll() {
            AppUtils.getScope().launch {
                AppUtils.getService().sendMsg("log/delete/all", null)
            }
        }
    }
}
