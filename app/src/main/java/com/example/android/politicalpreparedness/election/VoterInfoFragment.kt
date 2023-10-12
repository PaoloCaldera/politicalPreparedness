package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.network.CivicsApiStatus
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class VoterInfoFragment : Fragment() {

    private lateinit var args: VoterInfoFragmentArgs
    private lateinit var binding: FragmentVoterInfoBinding
    private lateinit var viewModel: VoterInfoViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Arguments late initialization
        val argsDefinition: VoterInfoFragmentArgs by navArgs()
        args = argsDefinition

        // ViewModel late initialization
        val viewModelDefinition: VoterInfoViewModel by viewModels {
            VoterInfoViewModel.VoterInfoViewModelFactory(
                args.argElection, ElectionDatabase.getInstance(requireContext()).electionDao
            )
        }
        viewModel = viewModelDefinition
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVoterInfoBinding.inflate(inflater, container, false)

        binding.apply {
            lifecycleOwner = this@VoterInfoFragment
            voterInfoViewModel = viewModel
        }

        // Edit the screen UI according to whether data is correctly retrieved or not
        viewModel.networkStatus.observe(viewLifecycleOwner) { apiStatus ->
            when (apiStatus) {
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

        // Observe the voter info variable to handle correctly the UI
        viewModel.voterInfo.observe(viewLifecycleOwner) { voterInfo ->
            voterInfo?.let {
                if (it.state == null) {
                    // If the voter info state is null, no detailed election data is available
                    Toast.makeText(
                        requireContext(),
                        "No election detail data provided",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().popBackStack()
                } else if (it.state[0].electionAdministrationBody.correspondenceAddress == null)
                    binding.address.visibility = View.GONE
                else if (it.state[0].electionAdministrationBody.votingLocationFinderUrl == null)
                    binding.stateLocations.visibility = View.GONE
                else if (it.state[0].electionAdministrationBody.ballotInfoUrl == null)
                    binding.stateBallot.visibility = View.GONE
            }
        }

        // Observe the fab status live data to alert the user when following/unfollowing the election
        viewModel.fabStatusFollowingFlag.observe(viewLifecycleOwner) { election ->
            if (election) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(resources.getString(R.string.following_title))
                    .setMessage(resources.getString(R.string.following_message, args.argElectionName))
                    .setPositiveButton(resources.getString(android.R.string.ok)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            } else {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(resources.getString(R.string.unfollowing_title))
                    .setMessage(resources.getString(R.string.unfollowing_message, args.argElectionName))
                    .setPositiveButton(resources.getString(android.R.string.ok)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }
        }

        // Observe voting info live data to open the link
        viewModel.clickVotingInfoFlag.observe(viewLifecycleOwner) { flag ->
            if (flag) {
                val votingUriString =
                    viewModel.voterInfo.value?.state?.get(0)?.electionAdministrationBody?.votingLocationFinderUrl
                votingUriString?.let { loadUrl(it) }
                viewModel.clickVotingInfoFlagOff()
            }
        }

        // Observe ballot info live data to open the link
        viewModel.clickBallotInfoFlag.observe(viewLifecycleOwner) { flag ->
            if (flag) {
                val ballotUriString =
                    viewModel.voterInfo.value?.state?.get(0)?.electionAdministrationBody?.ballotInfoUrl
                ballotUriString?.let { loadUrl(it) }
                viewModel.clickBallotInfoFlagOff()
            }
        }

        return binding.root
    }

    /**
     * Load on a web browser the URL data upon a click on voting or ballot information
     */
    private fun loadUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}