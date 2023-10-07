package com.example.android.politicalpreparedness


import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.network.models.Representative
import com.google.android.material.floatingactionbutton.FloatingActionButton


/*  ElectionsFragment */

/**
 * Define the adapter of the elections recycler view, using the header string, the list of
 * elections and the onClick function
 */
@BindingAdapter("header", "listData", "onItemClick")
fun bindElectionListData(
    recyclerView: RecyclerView,
    header: String,
    listData: List<Election>?,
    onItemClick: (Election) -> Unit
) {
    val adapter =
        ElectionListAdapter(header, ElectionListAdapter.ElectionListener { onItemClick(it) })
    adapter.addHeaderAndSubmitList(listData)
    recyclerView.adapter = adapter
}

/**
 * Change the icon of the floating action button according to the election selected. If the
 * election has not been saved yet, show the save button. If the election has been already saved,
 * show the remove button
 */
@BindingAdapter("status")
fun bindSaveRemoveFab(fab: FloatingActionButton, election: Election?) {
    if (election == null) {
        fab.setImageResource(R.drawable.ic_save)        // To save/follow
        fab.contentDescription = fab.context.resources.getString(R.string.follow_election)
    } else {
        fab.setImageResource(R.drawable.ic_remove)      // To remove/unfollow
        fab.contentDescription = fab.context.resources.getString(R.string.unfollow_election)
    }
}




/*  RepresentativeFragment  */

/**
 * Define the adapter of the representatives recycler view, using the header string and the
 * list of representatives
 */
@BindingAdapter("header", "listData")
fun bindRepresentativeListData(
    recyclerView: RecyclerView,
    header: String,
    listData: List<Representative>?
) {
    val adapter = RepresentativeListAdapter(header)
    adapter.addHeaderAndSubmitList(listData)
    recyclerView.adapter = adapter
}


/**
 * Put the image associated to a url inside the image view using Glide. If there is any error
 * in retrieving the image, use the placeholder profile icon
 */
@BindingAdapter("photoUrl")
fun bindPhotoUrl(imageView: ImageView, url: String?) {
    url?.let {
        Glide.with(imageView)
            .load(url.toUri().buildUpon().scheme("https").build())
            .circleCrop()
            .placeholder(R.drawable.ic_profile)
            .error(R.drawable.ic_broken_image)
            .into(imageView)
    } ?: imageView.setImageResource(R.drawable.ic_profile)
}

/**
 * Set the correct spinner value.
 * The function is very useful when the user decides to use its selection and the header
 */
@BindingAdapter("stateValue")
fun Spinner.setNewValue(value: String?) {
    val adapter = toTypedAdapter<String>(this.adapter as ArrayAdapter<*>)
    val position = when (adapter.getItem(0)) {
        is String -> adapter.getPosition(value)
        else -> this.selectedItemPosition
    }
    if (position >= 0) {
        setSelection(position)
    }
}
@Suppress("UNCHECKED_CAST")
inline fun <reified T> toTypedAdapter(adapter: ArrayAdapter<*>): ArrayAdapter<T> {
    return adapter as ArrayAdapter<T>
}