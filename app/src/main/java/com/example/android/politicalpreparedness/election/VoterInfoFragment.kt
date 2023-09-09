package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.network.CivicsApiStatus
import com.example.android.politicalpreparedness.network.models.Election

class VoterInfoFragment : Fragment() {

    private lateinit var binding: FragmentVoterInfoBinding
    private val viewModel: VoterInfoViewModel by viewModels {
        VoterInfoViewModel.VoterInfoViewModelFactory(ElectionDatabase.getInstance(requireContext()).electionDao)
    }

    // Arguments
    private lateinit var argElection: Election


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve arguments from navigation
        argElection = VoterInfoFragmentArgs.fromBundle(arguments).argElection
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVoterInfoBinding.inflate(inflater)
        binding.apply {
            lifecycleOwner = this@VoterInfoFragment
            voterInfoViewModel = viewModel
        }

        // Edit the screen UI according to whether data is correctly retrieved or not
        handleNetworkStatus()

        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
         */

        // TODO: Handle loading of URLs
        // Observe voting info live data to open the link
        viewModel.clickVotingInfoFlag.observe(viewLifecycleOwner) {
            if (it) {
                val votingUriString =
                    viewModel.voterInfo.value?.state?.get(0)?.electionAdministrationBody?.votingLocationFinderUrl
                votingUriString?.let { loadUrl(it) }
            }
            viewModel.offVotingInfoClicked()
        }

        // Observe ballot info live data to open the link
        viewModel.clickBallotInfoFlag.observe(viewLifecycleOwner) {
            if (it) {
                val ballotUriString =
                    viewModel.voterInfo.value?.state?.get(0)?.electionAdministrationBody?.ballotInfoUrl
                ballotUriString?.let { loadUrl(it) }
            }
            viewModel.offBallotInfoClicked()
        }

        return binding.root
    }


    private fun handleNetworkStatus() {
        viewModel.networkStatus.observe(viewLifecycleOwner) {
            when (it) {
                CivicsApiStatus.LOADING -> binding.apply {
                    dataCard.visibility = View.GONE
                    connectionErrorImage.visibility = View.GONE
                    loadingImage.visibility = View.VISIBLE
                }
                CivicsApiStatus.SUCCESS -> binding.apply {
                    dataCard.visibility = View.VISIBLE
                    loadingImage.visibility = View.GONE
                }
                CivicsApiStatus.ERROR -> binding.apply {
                    dataCard.visibility = View.GONE
                    loadingImage.visibility = View.GONE
                    connectionErrorImage.visibility = View.VISIBLE
                }
                else -> throw Exception("Invalid HTTP connection status")
            }
        }
    }

    /**
     * Load on a web browser the URL data upon a click on voting or ballot information
     */
    private fun loadUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}