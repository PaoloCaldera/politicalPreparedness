package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.LocationAppServices
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.network.CivicsApiStatus
import com.example.android.politicalpreparedness.network.models.Election
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ElectionsFragment : Fragment() {

    private lateinit var binding: FragmentElectionBinding
    private val viewModel: ElectionsViewModel by viewModels {
        ElectionsViewModel.ElectionsViewModelFactory(
            ElectionDatabase.getInstance(requireContext()).electionDao
        )
    }

    // LocationAppServices: class with methods for checking location permission and activation
    private val locationAppServices = LocationAppServices(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentElectionBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = this@ElectionsFragment
            electionsViewModel = viewModel
        }

        viewModel.savedElections.observe(viewLifecycleOwner) { electionList ->
            if (electionList.isNullOrEmpty()) {
                binding.savedRecyclerView.visibility = View.INVISIBLE
            } else {
                binding.savedRecyclerView.visibility = View.VISIBLE
            }
        }

        // Handle the layout based on the network status
        viewModel.networkStatus.observe(viewLifecycleOwner) { apiStatus ->
            when (apiStatus) {
                CivicsApiStatus.LOADING -> binding.apply {
                    upcomingRecyclerView.visibility = View.INVISIBLE
                    connectionErrorImage.visibility = View.INVISIBLE
                    loadingImage.visibility = View.VISIBLE
                }

                CivicsApiStatus.SUCCESS -> binding.apply {
                    loadingImage.visibility = View.INVISIBLE
                    connectionErrorImage.visibility = View.INVISIBLE
                    upcomingRecyclerView.visibility = View.VISIBLE
                }

                CivicsApiStatus.ERROR -> binding.apply {
                    upcomingRecyclerView.visibility = View.INVISIBLE
                    loadingImage.visibility = View.INVISIBLE
                    connectionErrorImage.visibility = View.VISIBLE
                }

                else -> throw Exception("Invalid HTTP connection status")
            }
        }

        // Handle the navigation to VoterInfoFragment
        viewModel.navigateToVoterInfoFlag.observe(viewLifecycleOwner) { election ->
            election?.let {
                if (it.division.state == "") {
                    // Get the state of the user's current location
                    viewModel.locationPermissionFlagOn()
                    return@let
                }
                navigateToVoterInfo(it)
                viewModel.navigateToVoterInfoFlagOff()
            }
        }

        // Trigger the location permission check
        viewModel.locationPermissionFlag.observe(viewLifecycleOwner) { flag ->
            if (flag && locationAppServices.checkLocationPermission()) {
                viewModel.activeDeviceLocationFlagOn()
                viewModel.locationPermissionFlagOff()
            }
        }

        // Trigger the device location enabled check
        viewModel.activeDeviceLocationFlag.observe(viewLifecycleOwner) { flag ->
            if (flag) {
                locationAppServices.enableDeviceLocation()
                    .addOnSuccessListener {// Device location already enabled
                        viewModel.currentLocationFlagOn()
                        viewModel.activeDeviceLocationFlagOff()
                    }
                    .addOnFailureListener { exception ->  // Device location currently inactive
                        if (!locationAppServices.solveOnDeviceLocationInactive(exception))
                        // When there is no automatic resolution, reset the flag for the next check
                            viewModel.activeDeviceLocationFlagOff()
                    }
            }
        }

        // Trigger the calculation of the user's current location
        viewModel.currentLocationFlag.observe(viewLifecycleOwner) { flag ->
            if (flag) {
                locationAppServices.getCurrentLocation()
                    .addOnSuccessListener { location ->  // Location has been correctly retrieved
                        viewModel.geocodeLocationFlagOn(location)
                        viewModel.currentLocationFlagOff()
                    }
                    .addOnFailureListener { // Location has not been retrieved
                        locationAppServices.onCurrentLocationError()
                        viewModel.currentLocationFlagOff()
                    }
            }
        }

        // Trigger the current location decoding, to obtain the address
        viewModel.geocodeLocationFlag.observe(viewLifecycleOwner) { location ->
            location?.let {
                val address = locationAppServices.geocodeLocation(it)
                viewModel.geocodeLocationFlagOff(address)
            }
        }

        setHasOptionsMenu(true)

        return binding.root
    }

    /**
     * Inflate the menu layout
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_election, menu)
    }

    /**
     * If the menu option is selected, clear all the saved elections from the database
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.clear_following -> {
                viewModel.clearElections()
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(resources.getString(R.string.clearing_title))
                    .setMessage(resources.getString(R.string.clearing_message))
                    .setPositiveButton(resources.getString(android.R.string.ok)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    /**
     * Navigate to voter info fragment with required safe args
     */
    private fun navigateToVoterInfo(election: Election) {
        val action = ElectionsFragmentDirections
            .actionElectionsFragmentToVoterInfoFragment(election, election.name)
        findNavController().navigate(action)
    }


    /**
     * Check the result of the permission grant request
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (locationAppServices.onRequestPermissionsResult(grantResults)) {
            viewModel.activeDeviceLocationFlagOn()
        }
        /* Reset the permission flag:
           - for the next check if permissions are not granted yet
           - for finishing the location permission check if permissions are granted
         */
        viewModel.locationPermissionFlagOff()
    }


    /**
     * Result for the device location activation request
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LocationAppServices.TURN_DEVICE_LOCATION_ON_REQUEST_CODE) {
            // Turn off and on again the flag to perform automatically another check
            viewModel.activeDeviceLocationFlagOff()
            viewModel.activeDeviceLocationFlagOn()
        }
    }
}