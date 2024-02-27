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
package net.ankio.auto.database

import androidx.room.Database
import androidx.room.RoomDatabase
import net.ankio.auto.database.dao.AssetsDao
import net.ankio.auto.database.dao.AssetsMapDao
import net.ankio.auto.database.dao.BillInfoDao
import net.ankio.auto.database.dao.BookNameDao
import net.ankio.auto.database.dao.CategoryDao
import net.ankio.auto.database.dao.RegularDao
import net.ankio.auto.database.table.Assets
import net.ankio.auto.database.table.AssetsMap
import net.ankio.auto.database.table.BillInfo
import net.ankio.auto.database.table.BookName
import net.ankio.auto.database.table.Category
import net.ankio.auto.database.table.Regular

@Database(
    entities = [Assets::class,AssetsMap::class,BillInfo::class,BookName::class,Category::class,Regular::class],
    version = 1,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun RegularDao(): RegularDao
    abstract fun BookNameDao(): BookNameDao
    abstract fun AssetsMapDao(): AssetsMapDao
    abstract fun BillInfoDao(): BillInfoDao
    abstract fun CategoryDao(): CategoryDao
    abstract fun AssetsDao(): AssetsDao
}
