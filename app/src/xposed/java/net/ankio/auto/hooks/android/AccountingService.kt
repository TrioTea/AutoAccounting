/*
 * Copyright (C) 2023 ankio(ankio@ankio.net)
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

package net.ankio.auto.hooks.android

import android.content.Context
import android.util.Log
import net.ankio.auto.IAccountingService
import net.ankio.auto.database.table.BillInfo
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AccountingService(val mContext:Context?) : IAccountingService.Stub() {

    private val SERVICE_NAME = "accounting.service"

    private val TAG = "AutoAccountingService"

    fun getServiceName(): String {
        return "user.$SERVICE_NAME"
    }


    fun systemReady() {
        Log.e(TAG,"Welcome to AutoAccounting.")
        // 删除日志
        val logFile = File(logFileName)
        if (logFile.exists()) {
            logFile.delete()
        }
        Log.e(TAG,"Removed old logs：$logFile")
        //写入启动日志
        log("Android","------- AutoAccounting Start  ")



    }

    companion object{
        private var mService: IAccountingService? = null
        fun get( mContext:Context?): IAccountingService? {
            try {
                 val SERVICE_NAME = "accounting.service"
                if (mService == null) {
                 mService = mContext?.getSystemService("user.$SERVICE_NAME") as IAccountingService
                }
                return mService
            } catch (e: Exception) {
                println(e.message)
                e.printStackTrace()
            }
            return null
        }
        const val dataDir = "/data/system/net.ankio.auto.xposed"
        const val logFileName = "$dataDir/auto-log.txt"

    }



    /**
     * 初始化创建文件夹子
     */

    private fun initFile(file: File){
        if (!file.exists()) {
            val parentDirectory = file.parentFile
            if (parentDirectory != null && !parentDirectory.exists()) {
                // 递归创建目录
                parentDirectory.mkdirs()
            }
            file.createNewFile()
        }
    }

    override fun log(prefix: String?, log: String?) {
        try {
            val logFile = File(logFileName)
           initFile(logFile)

            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
            val currentTime = Date()
            val logMessage = "[${dateFormat.format(currentTime)}][$prefix]$log"

            val fileWriter = FileWriter(logFile, true) // 设置为 true，以便追加到现有文件
            val bufferedWriter = BufferedWriter(fileWriter)

            bufferedWriter.write(logMessage)
            bufferedWriter.newLine() // 换行

            bufferedWriter.close()
            fileWriter.close()
        } catch (e: IOException) {
           Log.e(TAG,"Error writing to the log file: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * 读配置文件
     */
    override fun get(key: String): String {
        val dataFileName = "$dataDir/auto-$key.txt"
        val dataFile = File(dataFileName)

        return if (dataFile.exists()) {
            dataFile.readText()
        } else {
            ""
        }
    }

    /**
     * 写配置文件
     */
    override fun put(key: String, value: String) {
        val dataFileName = "$dataDir/auto-$key.txt"
        val dataFile = File(dataFileName)
        initFile(dataFile)
        dataFile.writeText(value)
    }

    override fun launchApp(billInfo: String?) {
        TODO("Not yet implemented")
    }


    override fun analyzeData(data: String?): String? {
        TODO("Not yet implemented")
    }

    override fun getDataInfo() {
        TODO("Not yet implemented")
    }
}