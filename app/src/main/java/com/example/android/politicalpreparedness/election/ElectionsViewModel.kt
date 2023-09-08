package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.network.models.Election
import java.lang.IllegalArgumentException

//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel : ViewModel() {

    // UI variable: upcoming election list
    private val _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElections: LiveData<List<Election>>
        get() = _upcomingElections

    // UI variable: saved election list
    private val _savedElections = MutableLiveData<List<Election>>()
    val savedElections: LiveData<List<Election>>
        get() = _savedElections

    private val _navigateToVoterInfoFlag = MutableLiveData<Election?>(null)
    val navigateToVoterInfoFlag: LiveData<Election?>
        get() = _navigateToVoterInfoFlag

    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database

    // Handle navigation for saved or upcoming election list into voter info fragment
    fun onElectionClicked(election: Election) {
        _navigateToVoterInfoFlag.value = election
    }
    fun onClickResolved() {
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