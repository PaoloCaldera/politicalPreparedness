package com.example.android.politicalpreparedness.representative


import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.CivicsApiStatus
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.Representative
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class RepresentativeViewModel : ViewModel() {

    // LiveData variables exploiting 2-way data binding, used for EditText views (and Spinner)
    val line1 = MutableLiveData<String>()
    val line2 = MutableLiveData<String?>()
    val city = MutableLiveData<String>()
    val state = MutableLiveData<String>()
    val zip = MutableLiveData<String>()


    // Variable containing the representatives list for the recycler view
    private val _representativesList = MutableLiveData<List<Representative>?>(null)
    val representativeList: LiveData<List<Representative>?>
        get() = _representativesList

    // Variable containing the network status related to the web service call
    private val _networkStatus = MutableLiveData<CivicsApiStatus?>()
    val networkStatus: LiveData<CivicsApiStatus?>
        get() = _networkStatus

    // Flag triggering an alert if the form is not correctly filled
    private val _emptyFormFlag = MutableLiveData<Boolean>()
    val emptyFormFlag: LiveData<Boolean>
        get() = _emptyFormFlag

    // Flag that triggers the location permission check
    private val _locationPermissionFlag = MutableLiveData<Boolean>()
    val locationPermissionFlag: LiveData<Boolean>
        get() = _locationPermissionFlag

    // Flag that triggers the device location activation check
    private val _activeDeviceLocationFlag = MutableLiveData<Boolean>()
    val activeDeviceLocationFlag: LiveData<Boolean>
        get() = _activeDeviceLocationFlag

    // Flag that triggers the retrieval of the current location
    private val _currentLocationFlag = MutableLiveData<Boolean>()
    val currentLocationFlag: LiveData<Boolean>
        get() = _currentLocationFlag

    // Flag that triggers the current location decoding
    private val _geocodeLocationFlag = MutableLiveData<Location?>()
    val geocodeLocationFlag: LiveData<Location?>
        get() = _geocodeLocationFlag


    /**
     * "Find my representatives" button clicked
     */
    fun onFindMyRepresentativesClicked() {
        if (line1.value.isNullOrEmpty() ||
            city.value.isNullOrEmpty() ||
            state.value.isNullOrEmpty() ||
            zip.value.isNullOrEmpty()
        ) {

            emptyFormFlagOn()
            return
        }

        getRepresentatives(
            Address(
                line1 = line1.value!!,
                line2 = line2.value,
                city = city.value!!,
                state = state.value!!,
                zip = zip.value!!
            )
        )
        emptyFormFlagOff()
    }

    /**
     * "Use my location" button clicked
     */
    fun onUseMyLocationClicked() {
        locationPermissionFlagOn()
    }


    /**
     * Turn on and off the emptyFormFlag
     */
    private fun emptyFormFlagOn() {
        _emptyFormFlag.value = true
    }

    fun emptyFormFlagOff() {
        _emptyFormFlag.value = false
    }

    /**
     * Turn on and off the locationPermissionFlag
     */
    private fun locationPermissionFlagOn() {
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
        autofillForm(address)
        getRepresentatives(address)
    }

    /**
     * Fill automatically the header form when clicking the Use My Location button
     */
    private fun autofillForm(address: Address) {
        line1.value = address.line1
        line2.value = address.line2
        city.value = address.city
        state.value = address.state
        zip.value = address.zip
    }


    /**
     * Call the /representatives resource to fetch the representatives
     */
    private fun getRepresentatives(address: Address) = viewModelScope.launch {
        _networkStatus.value = CivicsApiStatus.LOADING
        try {
            val (offices, officials) = CivicsApi.retrofitService.getRepresentatives(address.toFormattedString())
            // Use the flatMap function to combine all the lists to a single representative list
            _representativesList.value =
                offices.flatMap { office -> office.getRepresentatives(officials) }
            _networkStatus.value = CivicsApiStatus.SUCCESS
        } catch (e: Exception) {
            _networkStatus.value = CivicsApiStatus.ERROR
        }
    }


    /**
     * View model factory class: instantiate the view model in the fragment class
     */
    @Suppress("UNCHECKED_CAST")
    class RepresentativeViewModelFactory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RepresentativeViewModel::class.java))
                return RepresentativeViewModel() as T
            throw IllegalArgumentException("Unknown view model class RepresentativeViewModel")
        }
    }
}
