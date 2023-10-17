package com.example.android.politicalpreparedness.representative

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.politicalpreparedness.LocationAppServices
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.CivicsApiStatus
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class RepresentativeFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: FragmentRepresentativeBinding
    private val viewModel: RepresentativeViewModel by viewModels {
        RepresentativeViewModel.RepresentativeViewModelFactory(this.requireActivity().application)
    }

    // LocationAppServices: class with methods for checking location permission and activation
    private val locationAppServices = LocationAppServices(this)

    // Variable for saving the motion state of the animation handled by motion layout
    private var motionState: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentRepresentativeBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = this@RepresentativeFragment
            representativeViewModel = viewModel
            addressLine1.setOnFocusChangeListener { v, hasFocus -> hideSoftKeyboard(v, hasFocus) }
            addressLine2.setOnFocusChangeListener { v, hasFocus -> hideSoftKeyboard(v, hasFocus) }
            city.setOnFocusChangeListener { v, hasFocus -> hideSoftKeyboard(v, hasFocus) }
            zip.setOnFocusChangeListener { v, hasFocus -> hideSoftKeyboard(v, hasFocus) }
        }

        // Handle the layout based on the network status
        viewModel.networkStatus.observe(viewLifecycleOwner) { apiStatus ->
            when (apiStatus) {
                CivicsApiStatus.LOADING -> binding.apply {
                    representativesRecyclerView.visibility = View.INVISIBLE
                    connectionErrorImage.visibility = View.INVISIBLE
                    loadingImage.visibility = View.VISIBLE
                }

                CivicsApiStatus.SUCCESS -> binding.apply {
                    loadingImage.visibility = View.INVISIBLE
                    connectionErrorImage.visibility = View.INVISIBLE
                    representativesRecyclerView.visibility = View.VISIBLE
                }

                CivicsApiStatus.ERROR -> binding.apply {
                    representativesRecyclerView.visibility = View.INVISIBLE
                    loadingImage.visibility = View.INVISIBLE
                    connectionErrorImage.visibility = View.VISIBLE
                }

                else -> throw Exception("Invalid HTTP connection status")
            }
        }

        // Trigger an alert if the form is not correctly filled
        viewModel.emptyFormFlag.observe(viewLifecycleOwner) { flag ->
            if (flag) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(resources.getString(R.string.form_not_filled_title))
                    .setMessage(
                        resources.getString(
                            R.string.form_not_filled_message,
                            if (viewModel.line1.value.isNullOrEmpty())
                                resources.getString(R.string.address_line_1)
                            else if (viewModel.city.value.isNullOrEmpty())
                                resources.getString(R.string.city)
                            else if (viewModel.zip.value.isNullOrEmpty())
                                resources.getString(R.string.zip_code)
                            else
                                resources.getString(R.string.state)
                        )
                    )
                    .setPositiveButton(resources.getString(android.R.string.ok)) { dialog, _ ->
                        viewModel.emptyFormFlagOff()
                        dialog.dismiss()
                    }
                    .create()
                    .show()
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

        // Initialize the spinner by applying to it an array adapter
        val spinner = binding.state
        spinner.onItemSelectedListener = this@RepresentativeFragment
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.states,
            android.R.layout.simple_spinner_item
        ).also { arrayAdapter ->
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = arrayAdapter
        }

        return binding.root
    }

    // Restore the current position of the recycler view
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let { bundle ->
            // Save the exact transition state
            motionState = bundle.getInt(getString(R.string.current_motion_state_key))
            motionState?.let { binding.motionLayout?.transitionToState(it) }
        }
    }

    // Save the current position of the recycler view
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.motionLayout?.let { layout -> motionState = layout.currentState }
        motionState?.let { outState.putInt(getString(R.string.current_motion_state_key), it) }
    }


    // Spinner selection
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        parent?.let { adapterView ->
            viewModel.state.value = adapterView.getItemAtPosition(position) as String
        }
    }

    // Spinner null selection
    override fun onNothingSelected(parent: AdapterView<*>?) {}


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


    /**
     * Hide soft keyboard when EditText view loses focus
     */
    private fun hideSoftKeyboard(view: View, hasFocus: Boolean) {
        if (!hasFocus) {
            val inputMethodManager =
                view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_IMPLICIT_ONLY
            )
        }
    }
}