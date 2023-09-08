package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter

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
        binding.lifecycleOwner = this

        // TODO: Add binding values

        // TODO: Link elections to voter info

        // Logic for setting upcoming recycler view adapter with upcoming elections data
        val upcoming = binding.upcomingRecyclerView
        val upcomingAdapter = ElectionListAdapter(
            resources.getString(R.string.upcoming_list_title),
            ElectionListAdapter.ElectionListener { electionId, electionDivision ->
                val action = ElectionsFragmentDirections
                    .actionElectionsFragmentToVoterInfoFragment(electionId, electionDivision)
                findNavController().navigate(action)
            }
        )
        upcomingAdapter.submitList(viewModel.upcomingElections.value)
        upcoming.adapter = upcomingAdapter


        // Logic for setting saved recycler view adapter with saved elections data
        val saved = binding.savedRecyclerView
        val savedAdapter = ElectionListAdapter(
            resources.getString(R.string.saved_list_title),
            ElectionListAdapter.ElectionListener { electionId, electionDivision ->
                val action = ElectionsFragmentDirections
                    .actionElectionsFragmentToVoterInfoFragment(electionId, electionDivision)
                findNavController().navigate(action)
            }
        )
        savedAdapter.submitList(viewModel.savedElections.value)
        saved.adapter = savedAdapter


        return binding.root
    }

    // TODO: Refresh adapters when fragment loads
}