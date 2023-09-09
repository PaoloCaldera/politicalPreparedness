package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.network.models.Election

class ElectionsFragment : Fragment() {

    private lateinit var binding: FragmentElectionBinding
    private val viewModel: ElectionsViewModel by viewModels {
        ElectionsViewModel.ElectionsViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentElectionBinding.inflate(inflater)
        binding.apply {
            lifecycleOwner = this@ElectionsFragment
            electionsViewModel = viewModel
        }

        // TODO: Link elections to voter info

        // Logic for setting upcoming recycler view adapter with upcoming elections data
        /*val upcoming = binding.upcomingRecyclerView
        val upcomingAdapter = ElectionListAdapter(
            resources.getString(R.string.upcoming_list_title),
            ElectionListAdapter.ElectionListener { election -> viewModel.onElectionClicked(election) }
        )
        upcomingAdapter.submitList(viewModel.upcomingElections.value)
        upcoming.adapter = upcomingAdapter*/


        // Logic for setting saved recycler view adapter with saved elections data
        /*val saved = binding.savedRecyclerView
        val savedAdapter = ElectionListAdapter(
            resources.getString(R.string.saved_list_title),
            ElectionListAdapter.ElectionListener { election -> viewModel.onElectionClicked(election) }
        )
        savedAdapter.submitList(viewModel.savedElections.value)
        saved.adapter = savedAdapter*/


        // Observed variable aimed to handle navigation to voter info fragment
        viewModel.navigateToVoterInfoFlag.observe(viewLifecycleOwner) {
            it?.let {
                navigateToVoterInfo(it)
                viewModel.offElectionClicked()
            }

        }

        return binding.root
    }


    /**
     * Navigate to voter info fragment with required safe args
     */
    private fun navigateToVoterInfo(election: Election) {
        val action = ElectionsFragmentDirections
            .actionElectionsFragmentToVoterInfoFragment(election)
        findNavController().navigate(action)
    }

    // TODO: Refresh adapters when fragment loads
}