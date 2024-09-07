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

package net.ankio.auto.hooks.qianji.sync

import com.google.gson.Gson
import de.robv.android.xposed.XposedHelpers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.ankio.auto.core.App
import net.ankio.auto.core.api.HookerManifest
import net.ankio.auto.core.xposed.Hooker
import org.ezbook.server.constant.BillType
import org.ezbook.server.db.model.BillInfoModel
import org.ezbook.server.db.model.BookBillModel
import org.ezbook.server.db.model.SettingModel
import java.lang.reflect.Proxy
import java.util.Calendar
import java.util.HashSet
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class BaoXiaoUtils(
    private val manifest: HookerManifest,
    private val classLoader: ClassLoader
) {

    val baoXiaoImpl by lazy {
        XposedHelpers.findClass("com.mutangtech.qianji.bill.baoxiao.BxPresenterImpl", classLoader)
    }


    private suspend fun getBaoXiaoList(all: Boolean = false): List<*> =
        suspendCoroutine { continuation ->
            var resumed = false
            val constructor = baoXiaoImpl.constructors.first()!!
            // public BxPresenterImpl(t8.b bVar) {
            val param1Clazz = constructor.parameterTypes.first()!!
            val param1Object = Proxy.newProxyInstance(
                classLoader,
                arrayOf(param1Clazz)
            ) { proxy, method, args ->
                if (method.name == "onGetList") {
                    if (!resumed) {
                        resumed = true
                        val billList = args[0]
                        continuation.resume(billList as List<*>)
                    }
                }
            }
            // public void refresh(t8.c cVar, BookFilter bookFilter, KeywordFilter keywordFilter) {
            val refreshMethod = baoXiaoImpl.declaredMethods.find { it.name == "refresh" }!!


            val clazzEnum = refreshMethod.parameters[0].type

            val enumValue =
                clazzEnum?.declaredFields?.firstOrNull { it.name == if (all) "ALL" else "NOT" }!!
                    .get(null)

            //BookFilter
            val bookFilter = XposedHelpers.newInstance(refreshMethod.parameters[1].type)
            //KeywordFilter
            val keywordFilter = XposedHelpers.newInstance(refreshMethod.parameters[2].type, "")


            XposedHelpers.callMethod(
                XposedHelpers.newInstance(baoXiaoImpl, param1Object),
                "refresh",
                enumValue,
                bookFilter,
                keywordFilter
            )

        }


    suspend fun syncBaoXiao() = withContext(Dispatchers.IO) {
        // 报销账单
        val bxList =
            withContext(Dispatchers.Main) {
                getBaoXiaoList()
            }

        /**
         * {
         *     "_id": 11002,
         *     "assetid": 1613058959055,
         *     "billid": 1718199912441166031,
         *     "bookId": -1,
         *     "category": {
         *         "bookId": -1,
         *         "editable": 0,
         *         "icon": "http://qianjires.xxoojoke.com/cateic_other.png",
         *         "id": 6691047,
         *         "level": 1,
         *         "name": "其它",
         *         "parentId": -1,
         *         "sort": 10,
         *         "type": 0,
         *         "userId": "200104405e109647c18e9"
         *     },
         *     "categoryId": 6691047,
         *     "createtimeInSec": 1718199912,
         *     "descinfo": "支付宝-余额宝",
         *     "fromact": "支付宝-余额宝",
         *     "fromid": -1,
         *     "importPackId": 0,
         *     "money": 0.01,
         *     "paytype": 0,
         *     "platform": 0,
         *     "remark": "长城基金管理有限公司 -222222",
         *     "status": 1,
         *     "targetid": -1,
         *     "timeInSec": 1715020286,
         *     "type": 5,
         *     "updateTimeInSec": 0,
         *     "userid": "200104405e109647c18e9"
         * }*/

        /**
         * {
         *     "_id": 11002,
         *     "assetid": 1613058959055,
         *     "billid": 1718199912441166031,
         *     "bookId": -1,
         *     "category": {
         *         "bookId": -1,
         *         "editable": 0,
         *         "icon": "http://qianjires.xxoojoke.com/cateic_other.png",
         *         "id": 6691047,
         *         "level": 1,
         *         "name": "其它",
         *         "parentId": -1,
         *         "sort": 10,
         *         "type": 0,
         *         "userId": "200104405e109647c18e9"
         *     },
         *     "categoryId": 6691047,
         *     "createtimeInSec": 1718199912,
         *     "descinfo": "支付宝-余额宝",
         *     "fromact": "支付宝-余额宝",
         *     "fromid": -1,
         *     "importPackId": 0,
         *     "money": 0.01,
         *     "paytype": 0,
         *     "platform": 0,
         *     "remark": "长城基金管理有限公司 -222222",
         *     "status": 1,
         *     "targetid": -1,
         *     "timeInSec": 1715020286,
         *     "type": 5,
         *     "updateTimeInSec": 0,
         *     "userid": "200104405e109647c18e9"
         * }*/

        manifest.logD("报销账单:${Gson().toJson(bxList)},数据总数：${bxList.size}")
        val bills = convert2Bill(bxList, BillType.ExpendRepayment)
        val sync = Gson().toJson(bills)
        val md5 = App.md5(sync)
        val server = SettingModel.get("sync_bill_md5", "")
        if (server == md5) {
            manifest.log("报销列表信息未发生变化，无需同步")
            return@withContext
        }

        BookBillModel.put(bills, md5, BillType.ExpendRepayment)

    }

    suspend fun doBaoXiao(billModel: BillInfoModel) = withContext(Dispatchers.IO) {

        val list = billModel.extendData.split(", ")

        val billList =
            withContext(Dispatchers.Main) {
                getBaoXiaoList(true)
            }

        val selectBills =
            billList.filter {
                val billIdField =
                    it!!.javaClass.declaredFields.first { item -> item.name == "billid" }
                val billId = (billIdField.get(it) as Long).toString()
                // 判断billId是否在list中
                list.contains(billId)
            }

        if (selectBills.isEmpty()) {
            throw RuntimeException("没有找到需要报销的账单")
        }

        val constructor = baoXiaoImpl.constructors.first()!!
        // public BxPresenterImpl(t8.b bVar) {
        val param1Clazz = constructor.parameterTypes.first()!!
        val param1Object = Proxy.newProxyInstance(
            classLoader,
            arrayOf(param1Clazz)
        ) { _, _, _ ->

        }
        val baoXiaoInstance = XposedHelpers.newInstance(baoXiaoImpl, param1Object)

        val doBaoXiaoMethod = baoXiaoImpl.declaredMethods.find { it.name == "doBaoXiao" }!!
        //    public void doBaoXiao(
        //    java.util.Set<? extends com.mutangtech.qianji.data.model.Bill> r36,
        //    com.mutangtech.qianji.data.model.AssetAccount r37,
        //    double r38,
        //    java.util.Calendar r40,
        //    com.mutangtech.qianji.data.model.CurrencyExtra r41,
        //    java.lang.String r42,
        //    java.util.List<java.lang.String> r43,
        //    java.util.List<? extends com.mutangtech.qianji.data.model.Tag> r44) {


        // java.util.Set<? extends com.mutangtech.qianji.data.model.Bill> r36,
        val set = HashSet<Any>(selectBills)

        // com.mutangtech.qianji.data.model.AssetAccount r37,
        val asset = withContext(Dispatchers.Main) {
            AssetsUtils(manifest, classLoader).getAssetsList()
        }.filter {

            XposedHelpers.getObjectField(it, "name") as String == billModel.accountNameTo }

            .getOrNull(0) ?: throw RuntimeException("没有找到资产")


        // double r38,
        val money = billModel.money

        // java.util.Calendar r40,
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = billModel.time

        //com.mutangtech.qianji.data.model.CurrencyExtra r41
        val currencyExtraInstance =
            XposedHelpers.callMethod(selectBills.first(), "getCurrencyExtra")

        //java.lang.String r42,
        val str = ""

        // java.util.List<java.lang.String> r43, 这是图片链接
        val listStr = arrayListOf<String>()


        // java.util.List<? extends com.mutangtech.qianji.data.model.Tag> r44
        val listTag = arrayListOf<Any>()

        XposedHelpers.callMethod(
            baoXiaoInstance,
            "doBaoXiao",
            set,
            asset,
            money,
            calendar,
            currencyExtraInstance,
            str,
            listStr,
            listTag
        )

    }

    companion object {
        fun convert2Bill(anyBills: List<*>, billType: BillType): ArrayList<BookBillModel> {
            val bills = arrayListOf<BookBillModel>()
            anyBills.forEach {
                val bill = BookBillModel()
                bill.type = billType
                val fields = it!!.javaClass.declaredFields
                for (field in fields) {
                    field.isAccessible = true
                    val value = field.get(it)
                    when (field.name) {
                        "money" -> bill.money = value as Double
                        "billid" -> bill.remoteId = (value as Long).toString()
                        "remark" -> bill.remark = (value as String?) ?: ""
                        "createtimeInSec" -> bill.time = (value as Long) * 1000
                        // "fromact" -> bill.accountFrom = (value as String?) ?: ""
                        //  "descinfo" -> bill.accountTo = (value as String?) ?: ""
                        "bookId" -> bill.remoteBookId = (value as Long).toString()

                        "category" -> {
                            val category = XposedHelpers.getObjectField(value, "name") as String

                            bill.category = category
                        }
                    }
                }
                bills.add(bill)

                // 债务账单
            }
            return bills
        }
    }
}