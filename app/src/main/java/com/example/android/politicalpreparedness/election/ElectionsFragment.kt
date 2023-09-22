package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.network.CivicsApiStatus
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

        // Observe LiveData variable aimed to handle navigation to VoterInfoFragment
        viewModel.navigateToVoterInfoFlag.observe(viewLifecycleOwner) {
            it?.let {
                navigateToVoterInfo(it)
                viewModel.navigateToVoterInfoFlagOff()
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