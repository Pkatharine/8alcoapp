package com.example.android.marsrealestate.network

import com.example.android.marsrealestate.database.DatabaseAlco
import com.example.android.marsrealestate.domain.Alco
import com.squareup.moshi.JsonClass
@JsonClass(generateAdapter = true)
data class NetworkAlcoContainer(val alcos: List<NetworkAlco>)

@JsonClass(generateAdapter = true)
data class NetworkAlco(
        val alco_name: String,
        val affect: String,
        val url: String)

fun NetworkAlcoContainer.asDomainModel(): List<Alco> {
    return alcos.map {
        Alco(
                alco_name = it.alco_name,
                affect = it.affect,
                url = it.url)
    }
}

fun NetworkAlcoContainer.asDatabaseModel(): Array<DatabaseAlco> {
    return alcos.map {
        DatabaseAlco(
                alco_name = it.alco_name,
                affect = it.affect,
                url = it.url)
    }.toTypedArray()
}
