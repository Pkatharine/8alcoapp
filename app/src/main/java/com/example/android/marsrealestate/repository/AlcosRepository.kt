package com.example.android.marsrealestate.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.android.marsrealestate.database.AlcosDatabase
import com.example.android.marsrealestate.database.DatabaseAlco
import com.example.android.marsrealestate.database.asDomainModel
import com.example.android.marsrealestate.domain.Alco
import com.example.android.marsrealestate.network.AlcoApi
import com.example.android.marsrealestate.network.AlcoApiFilter
import com.example.android.marsrealestate.network.AlcoProperty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AlcosRepository(private val database: AlcosDatabase) {

    /**
     * A playlist of videos that can be shown on the screen.
     */
    val alcos: LiveData<List<Alco>> =
            Transformations.map(database.alcoDao.getAlcos()) {
                it.asDomainModel()
            }

    fun AlcoProperty.toRestDB() = DatabaseAlco(
            url = imgSrcUrl,
            alco_name = alco_name,
            affect = affect
    )


    /**
     * Refresh the videos stored in the offline cache.
     *
     * This function uses the IO dispatcher to ensure the database insert database operation
     * happens on the IO dispatcher. By switching to the IO dispatcher using `withContext` this
     * function is now safe to call from any thread including the Main thread.
     *
     * To actually load the videos for use, observe [videos]
     */
    suspend fun refreshAlcos() {
        withContext(Dispatchers.IO) {
            val playlist = AlcoApi.retrofitService.getProperties(AlcoApiFilter.SHOW_ALL.value)
            playlist.forEach({
                database.alcoDao.insertAll(it.toRestDB())
            })
        }
    }
}
