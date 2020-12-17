package com.example.android.marsrealestate.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.android.marsrealestate.domain.Alco

/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

@Entity
data class DatabaseAlco constructor(
        @PrimaryKey
        val url: String,
        val alco_name: String,
        val affect: String)

fun List<DatabaseAlco>.asDomainModel(): List<Alco> {
    return map {
        Alco(
                url = it.url,
                alco_name = it.alco_name,
                affect = it.affect
        )
    }
}
