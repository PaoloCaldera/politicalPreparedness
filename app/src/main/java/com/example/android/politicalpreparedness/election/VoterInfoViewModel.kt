package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApiStatus
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import java.lang.IllegalArgumentException

class VoterInfoViewModel(private val dataSource: ElectionDao) : ViewModel() {

    //TODO: Add live data to hold voter info
    private val _voterInfo = MutableLiveData<VoterInfoResponse>(null)
    val voterInfo: LiveData<VoterInfoResponse>
        get() = _voterInfo

    private val _networkStatus = MutableLiveData<CivicsApiStatus?>()
    val networkStatus: LiveData<CivicsApiStatus?>
        get() = _networkStatus

    private val _fabStatus = MutableLiveData<Election?>(null)
    val fabStatus: LiveData<Election?>
        get() = _fabStatus

    private val _clickVotingInfoFlag = MutableLiveData(false)
    val clickVotingInfoFlag: LiveData<Boolean>
        get() = _clickVotingInfoFlag

    private val _clickBallotInfoFlag = MutableLiveData(false)
    val clickBallotInfoFlag: LiveData<Boolean>
        get() = _clickBallotInfoFlag

    init {
        getVoterInfo()
        checkFabStatus()
    }

    //TODO: Add var and methods to populate voter info
    private fun getVoterInfo() {
        _networkStatus.value = CivicsApiStatus.LOADING
        try {
            // Call function to retrieve voter info data from internet
            _networkStatus.value = CivicsApiStatus.SUCCESS
        } catch (e: Exception) {
            _networkStatus.value = CivicsApiStatus.ERROR
        }
    }


    //TODO: Add var and methods to save and remove elections to local database
    fun onFabClick() {
        if (_fabStatus.value == null)
            insertElection()
        else
            deleteElection()
    }

    private fun checkFabStatus() {
        _fabStatus.value = selectElection()
    }

    private fun selectElection(): Election? {
        // Use the function to verify if the election has been already saved in the database
        return null
    }
    private fun insertElection() {
        // Use the function to follow/save the election into the database
    }
    private fun deleteElection() {
        // Use the function to unfollow/remove the election from the database
    }


    //TODO: Add var and methods to support loading URLs
    fun onVotingInfoClicked() {
        _clickVotingInfoFlag.value = true
    }
    fun offVotingInfoClicked() {
        _clickVotingInfoFlag.value = false
    }

    fun onBallotInfoClicked() {
        _clickBallotInfoFlag.value = true
    }
    fun offBallotInfoClicked() {
        _clickBallotInfoFlag.value = false
    }



    @Suppress("UNCHECKED_CAST")
    class VoterInfoViewModelFactory(private val dataSource: ElectionDao) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(VoterInfoViewModel::class.java))
                return VoterInfoViewModel(dataSource) as T
            throw IllegalArgumentException("Unknown view model class VoterInfoViewModel")
        }
    }
}