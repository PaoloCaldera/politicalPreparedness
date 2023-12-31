package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.CivicsApiStatus
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.IllegalArgumentException

class VoterInfoViewModel(private val election: Election, private val dataSource: ElectionDao) :
    ViewModel() {

    // Information about the voter info
    private val _voterInfo = MutableLiveData<VoterInfoResponse?>(null)
    val voterInfo: LiveData<VoterInfoResponse?>
        get() = _voterInfo

    // Network status related to the web service call
    private val _networkStatus = MutableLiveData<CivicsApiStatus?>()
    val networkStatus: LiveData<CivicsApiStatus?>
        get() = _networkStatus

    // Status of the save/remove election FAB
    private val _fabStatus = MutableLiveData<Election?>(null)
    val fabStatus: LiveData<Election?>
        get() = _fabStatus

    // Flag associated to the status change of the election FAB
    private val _fabStatusFollowingFlag = MutableLiveData<Boolean>()
    val fabStatusFollowingFlag: LiveData<Boolean>
        get() = _fabStatusFollowingFlag

    // Flag associated to the user click to the voting info link
    private val _clickVotingInfoFlag = MutableLiveData(false)
    val clickVotingInfoFlag: LiveData<Boolean>
        get() = _clickVotingInfoFlag

    // Flag associated to the user click to the ballot info link
    private val _clickBallotInfoFlag = MutableLiveData(false)
    val clickBallotInfoFlag: LiveData<Boolean>
        get() = _clickBallotInfoFlag


    init {
        getVoterInfo(election)
        checkFabStatus()
    }


    /**
     * Retrieve voter info data from the web service
     */
    private fun getVoterInfo(election: Election) = viewModelScope.launch {
        _networkStatus.value = CivicsApiStatus.LOADING
        try {
            val address = "${election.division.state}, ${election.division.country}"
            _voterInfo.value = CivicsApi.retrofitService.getVoterInfo(address, election.id)
            _networkStatus.value = CivicsApiStatus.SUCCESS
        } catch (e: Exception) {
            _networkStatus.value = CivicsApiStatus.ERROR
        }
    }


    /**
     * Check the FAB status by querying the local database, to verify if the election
     * has been already saved
     */
    private fun checkFabStatus() {
        viewModelScope.launch {
            var selectedElection: Election?
            withContext(Dispatchers.IO) {
                // Use a custom variable: LiveData value cannot be set inside the I/O dispatcher
                selectedElection = dataSource.select(election.id)
            }
            _fabStatus.value = selectedElection
        }
    }


    /**
     * Based on the FAB status, save/remove the election to/from the local database
     */
    fun onFabClick() {
        if (_fabStatus.value == null) {
            insertElection()
            _fabStatus.value = election
            _fabStatusFollowingFlag.value = true
        } else {
            deleteElection()
            _fabStatus.value = null
            _fabStatusFollowingFlag.value = false
        }
    }

    /**
     * Follow the election, so save it into the local database
     */
    private fun insertElection() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dataSource.insert(election)
            }
        }
    }

    /**
     * Unfollow the election, so remove it from the local database
     */
    private fun deleteElection() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dataSource.delete(election)
            }
        }
    }


    /**
     * Functions aimed to handle the user click on the voting info link
     */
    fun clickVotingInfoFlagOn() {
        _clickVotingInfoFlag.value = true
    }

    fun clickVotingInfoFlagOff() {
        _clickVotingInfoFlag.value = false
    }

    /**
     * Functions aimed to handle the user click on the ballot info link
     */
    fun clickBallotInfoFlagOn() {
        _clickBallotInfoFlag.value = true
    }

    fun clickBallotInfoFlagOff() {
        _clickBallotInfoFlag.value = false
    }


    /**
     * View model factory class: instantiate the view model in the fragment class
     */
    @Suppress("UNCHECKED_CAST")
    class VoterInfoViewModelFactory(
        private val election: Election,
        private val dataSource: ElectionDao
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(VoterInfoViewModel::class.java))
                return VoterInfoViewModel(election, dataSource) as T
            throw IllegalArgumentException("Unknown view model class VoterInfoViewModel")
        }
    }
}