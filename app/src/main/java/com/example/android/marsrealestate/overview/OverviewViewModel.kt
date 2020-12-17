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

package com.example.android.marsrealestate.overview

import android.app.Application
import androidx.lifecycle.*
import com.example.android.marsrealestate.network.AlcoApi
import com.example.android.marsrealestate.network.AlcoApiFilter
import com.example.android.marsrealestate.network.AlcoProperty
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
import com.example.android.marsrealestate.database.getDatabase
import com.example.android.marsrealestate.repository.AlcosRepository
import kotlinx.coroutines.launch

enum class AlcoApiStatus { LOADING, ERROR, DONE }
/**
 * The [ViewModel] that is attached to the [OverviewFragment].
 */
class OverviewViewModel (application: Application): AndroidViewModel(application) {

    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<AlcoApiStatus>()

    // The external immutable LiveData for the request status
    val status: LiveData<AlcoApiStatus>
        get() = _status


    // Internally, we use a MutableLiveData, because we will be updating the List of MarsProperty
    // with new values
    private val _properties = MutableLiveData<List<AlcoProperty>>()

    // The external LiveData interface to the property is immutable, so only this class can modify
    val properties: LiveData<List<AlcoProperty>>
        get() = _properties

    // Internally, we use a MutableLiveData to handle navigation to the selected property
    private val _navigateToSelectedProperty = MutableLiveData<AlcoProperty>()

    // The external immutable LiveData for the navigation property
    val navigateToSelectedProperty: LiveData<AlcoProperty>
        get() = _navigateToSelectedProperty



    /**
     * Call getMarsRealEstateProperties() on init so we can display status immediately.
     */

    private val database = getDatabase(application.applicationContext)
    private val alcoRepository = AlcosRepository(database)
    init {
        viewModelScope.launch {
            alcoRepository.refreshAlcos()
        }

    }

//    init {
//        getAlcoProperties(AlcoApiFilter.SHOW_ALL)
//    }

    val playlist = alcoRepository.alcos


    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(OverviewViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return OverviewViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

    /**
     * Gets filtered Mars real estate property information from the Mars API Retrofit service and
     * updates the [AlcoProperty] [List] and [MarsApiStatus] [LiveData]. The Retrofit service
     * returns a coroutine Deferred, which we await to get the result of the transaction.
     * @param filter the [AlcoApiFilter] that is sent as part of the web server request
     */
     private fun getAlcoProperties(filter: AlcoApiFilter) {
        viewModelScope.launch {
            _status.value = AlcoApiStatus.LOADING
            try {
                _properties.value = AlcoApi.retrofitService.getProperties(filter.value)
                _status.value = AlcoApiStatus.DONE
            } catch (e: Exception) {
                _status.value = AlcoApiStatus.ERROR
                _properties.value = ArrayList()
            }
        }
    }

    /**
     */

    /**
     * When the property is clicked, set the [_navigateToSelectedProperty] [MutableLiveData]
     * @param alcoProperty The [AlcoProperty] that was clicked on.
     */
    fun displayPropertyDetails(alcoProperty: AlcoProperty) {
        _navigateToSelectedProperty.value = alcoProperty
    }

    /**
     * After the navigation has taken place, make sure navigateToSelectedProperty is set to null
     */
    fun displayPropertyDetailsComplete() {
        _navigateToSelectedProperty.value = null
    }

    /**
     * Updates the data set filter for the web services by querying the data with the new filter
     * by calling [getMarsRealEstateProperties]
     * @param filter the [AlcoApiFilter] that is sent as part of the web server request
     */
    fun updateFilter(filter: AlcoApiFilter) {
        getAlcoProperties(filter)
    }
}
