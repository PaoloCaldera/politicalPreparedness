package com.example.android.politicalpreparedness.representative

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.election.VoterInfoViewModel
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import java.lang.IllegalArgumentException

class RepresentativeViewModel : ViewModel() {

    //TODO: Establish live data for representatives and address
    val line1 = MutableLiveData<String>()
    val line2 = MutableLiveData<String?>()
    val city = MutableLiveData<String>()
    val statePosition = MutableLiveData(0)
    val zip = MutableLiveData<String>()

    private val _representativesList = MutableLiveData<List<Representative>>()
    val representativeList: LiveData<List<Representative>>
        get() = _representativesList


    private val _representativesFlag = MutableLiveData(false)
    val representativesFlag: LiveData<Boolean>
        get() = _representativesFlag

    private val _geocodeLocationFlag = MutableLiveData(false)
    val geocodeLocationFlag: LiveData<Boolean>
        get() = _geocodeLocationFlag

    private fun stateFromPosition(): String {
        return Resources.getSystem().getStringArray(R.array.states)[statePosition.value!!]
    }

    fun onFindMyRepresentativesClicked() {
        _representativesFlag.value = true
        getRepresentatives(
            Address(
                line1 = line1.value!!,
                line2 = line2.value,
                city = city.value!!,
                state = stateFromPosition(),
                zip = zip.value!!
            )
        )
    }

    fun onUseMyLocationClicked() {
        _representativesFlag.value = true
        _geocodeLocationFlag.value = true
    }
    fun onGeocodeLocationRetrieved(address: Address) {
        _geocodeLocationFlag.value = false
        getRepresentatives(address)
    }

    //TODO: Create function to fetch representatives from API from a provided address
    private fun getRepresentatives(address: Address) {
        // Populate the LiveData variable representativesList with the result of the HTTP call
    }
    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */

    //TODO: Create function get address from geo location

    //TODO: Create function to get address from individual fields

    @Suppress("UNCHECKED_CAST")
    class RepresentativeViewModelFactory() : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RepresentativeViewModel::class.java))
                return RepresentativeViewModel() as T
            throw IllegalArgumentException("Unknown view model class RepresentativeViewModel")
        }
    }
}
