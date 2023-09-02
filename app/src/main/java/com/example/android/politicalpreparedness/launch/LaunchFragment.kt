package com.example.android.politicalpreparedness.launch

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.databinding.FragmentLaunchBinding

class LaunchFragment : Fragment() {

    private lateinit var binding: FragmentLaunchBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLaunchBinding.inflate(inflater)
        binding.lifecycleOwner = this

        // Navigate to representatives fragment
        binding.representativeButton.setOnClickListener {
            findNavController()
                .navigate(LaunchFragmentDirections.actionLaunchFragmentToRepresentativeFragment())
        }

        // Navigate to elections fragment
        binding.upcomingButton.setOnClickListener {
            findNavController()
                .navigate(LaunchFragmentDirections.actionLaunchFragmentToElectionsFragment())
        }

        return binding.root
    }
}
