package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.election.adapter.ElectionListViewItem
import com.example.android.politicalpreparedness.network.CivicsApiStatus
import com.example.android.politicalpreparedness.network.models.Election
import java.lang.IllegalArgumentException

//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel : ViewModel() {

    // UI variable: upcoming election list
    private val _upcomingElections = MutableLiveData<List<ElectionListViewItem>>()
    val upcomingElections: LiveData<List<ElectionListViewItem>>
        get() = _upcomingElections

    // UI variable: saved election list
    private val _savedElections = MutableLiveData<List<ElectionListViewItem>>()
    val savedElections: LiveData<List<ElectionListViewItem>>
        get() = _savedElections

    // Network status related to the web service call
    private val _networkStatus = MutableLiveData<CivicsApiStatus?>()
    val networkStatus: LiveData<CivicsApiStatus?>
        get() = _networkStatus

    // Navigation to VoterInfoFragment flag
    private val _navigateToVoterInfoFlag = MutableLiveData<Election?>(null)
    val navigateToVoterInfoFlag: LiveData<Election?>
        get() = _navigateToVoterInfoFlag

    // Save the function in a variable to use it also in the binding adapter
    val onItemClick: (Election) -> Unit = this::navigateToVoterInfoFlagOn


    init {
        getUpcomingElections()
        selectSavedElections()
    }


    /**
     * Retrieve the upcoming elections from the internet
     */
    private fun getUpcomingElections() {
        _networkStatus.value = CivicsApiStatus.LOADING
        try {
            // Retrieve the upcoming elections by calling the web service
            _networkStatus.value = CivicsApiStatus.SUCCESS
        } catch (e: Exception) {
            _networkStatus.value = CivicsApiStatus.ERROR
        }
    }

    /**
     * Retrieve the saved elections from the local database
     */
    private fun selectSavedElections() {
        // Retrieve the saved elections by selecting them from the local database
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
     * View model factory class: instantiate the view model in the fragment class
     */
    @Suppress("UNCHECKED_CAST")
    class ElectionsViewModelFactory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ElectionsViewModel::class.java))
                return ElectionsViewModel() as T
            throw IllegalArgumentException("Unknown view model class ElectionsViewModel")
        }
    }
}