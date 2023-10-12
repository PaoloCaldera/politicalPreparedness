package com.example.android.politicalpreparedness.election

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.CivicsApiStatus
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.IllegalArgumentException

class ElectionsViewModel(private val dataSource: ElectionDao) : ViewModel() {

    // UI variable: upcoming election list
    private val _upcomingElections = MutableLiveData<List<Election>?>(null)
    val upcomingElections: LiveData<List<Election>?>
        get() = _upcomingElections

    // UI variable: saved election list (from the local database)
    val savedElections: LiveData<List<Election>?> = dataSource.selectAll()


    // Network status related to the web service call
    private val _networkStatus = MutableLiveData<CivicsApiStatus?>()
    val networkStatus: LiveData<CivicsApiStatus?>
        get() = _networkStatus


    // Flag that triggers the navigation to VoterInfoFragment
    private val _navigateToVoterInfoFlag = MutableLiveData<Election?>(null)
    val navigateToVoterInfoFlag: LiveData<Election?>
        get() = _navigateToVoterInfoFlag


    // Flag that triggers the location permission check
    private val _locationPermissionFlag = MutableLiveData(false)
    val locationPermissionFlag: LiveData<Boolean>
        get() = _locationPermissionFlag

    // Flag that triggers the device location activation check
    private val _activeDeviceLocationFlag = MutableLiveData(false)
    val activeDeviceLocationFlag: LiveData<Boolean>
        get() = _activeDeviceLocationFlag

    // Flag that triggers the retrieval of the current location
    private val _currentLocationFlag = MutableLiveData(false)
    val currentLocationFlag: LiveData<Boolean>
        get() = _currentLocationFlag

    // Flag that triggers the current location decoding
    private val _geocodeLocationFlag = MutableLiveData<Location?>(null)
    val geocodeLocationFlag: LiveData<Location?>
        get() = _geocodeLocationFlag


    // Save the function in a variable to use it also in the binding adapter
    val onItemClick: (Election) -> Unit = this::navigateToVoterInfoFlagOn


    init {
        getUpcomingElections()
    }


    /**
     * Retrieve the upcoming elections from the internet
     */
    private fun getUpcomingElections() = viewModelScope.launch {
        _networkStatus.value = CivicsApiStatus.LOADING
        try {
            _upcomingElections.value =
                CivicsApi.retrofitService.getElections().elections.sortedBy { it.electionDay }
            _networkStatus.value = CivicsApiStatus.SUCCESS
        } catch (e: Exception) {
            _networkStatus.value = CivicsApiStatus.ERROR
        }
    }

    /**
     * Clear the saved elections list by removing all the occurrences from the local database
     */
    fun clearElections() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dataSource.clear()
            }
        }
    }


    /**
     * Handle navigation for saved or upcoming election list into voter info fragment
     */
    private fun navigateToVoterInfoFlagOn(election: Election) {
        _navigateToVoterInfoFlag.value = election
    }

    fun navigateToVoterInfoFlagOff() {
        _navigateToVoterInfoFlag.value = null
    }


    /**
     * Turn on and off the locationPermissionFlag
     */
    fun locationPermissionFlagOn() {
        _locationPermissionFlag.value = true
    }

    fun locationPermissionFlagOff() {
        _locationPermissionFlag.value = false
    }

    /**
     * Turn on and off the device locationActivationFlag
     */
    fun activeDeviceLocationFlagOn() {
        _activeDeviceLocationFlag.value = true
    }

    fun activeDeviceLocationFlagOff() {
        _activeDeviceLocationFlag.value = false
    }

    /**
     * Turn on and off the current locationRetrievalFlag
     */
    fun currentLocationFlagOn() {
        _currentLocationFlag.value = true
    }

    fun currentLocationFlagOff() {
        _currentLocationFlag.value = false
    }

    /**
     * Turn on and off the geocodeLocationFlag
     */
    fun geocodeLocationFlagOn(location: Location) {
        _geocodeLocationFlag.value = location
    }

    fun geocodeLocationFlagOff(address: Address) {
        _geocodeLocationFlag.value = null

        // Trigger again the navigation flag, but this time the state has a value
        _navigateToVoterInfoFlag.value = navigateToVoterInfoFlag.value!!.copy(
            division = navigateToVoterInfoFlag.value!!.division.copy(
                state = address.state
            )
        )
    }


    /**
     * View model factory class: instantiate the view model in the fragment class
     */
    @Suppress("UNCHECKED_CAST")
    class ElectionsViewModelFactory(private val dataSource: ElectionDao) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ElectionsViewModel::class.java))
                return ElectionsViewModel(dataSource) as T
            throw IllegalArgumentException("Unknown view model class ElectionsViewModel")
        }
    }
}