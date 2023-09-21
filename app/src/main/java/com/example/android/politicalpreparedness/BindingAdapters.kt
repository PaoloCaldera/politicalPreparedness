package com.example.android.politicalpreparedness

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListViewItem
import com.example.android.politicalpreparedness.network.models.Election
import com.google.android.material.floatingactionbutton.FloatingActionButton

@BindingAdapter("header", "listData", "onItemClick")
fun bindElectionListData(
    recyclerView: RecyclerView,
    header: String,
    listData: List<ElectionListViewItem>,
    onItemClick: (Election) -> Unit
) {
    val adapter =
        ElectionListAdapter(header, ElectionListAdapter.ElectionListener { onItemClick(it) })
    adapter.submitList(listData)
    recyclerView.adapter = adapter
}


@BindingAdapter("status")
fun bindSaveRemoveFab(fab: FloatingActionButton, election: Election?) {
    if (election == null)
        fab.setImageResource(R.drawable.ic_save)        // To save/follow
    else
        fab.setImageResource(R.drawable.ic_remove)      // To remove/unfollow
}


@BindingAdapter("photoUrl")
fun bindPhotoUrl(imageView: ImageView, url: String?) {
    url?.let {
        val uri = url.toUri().buildUpon().scheme("https").build()
        Glide.with(imageView)
            .load(uri)
            .error(R.drawable.ic_profile)
            .circleCrop()
            .into(imageView)
    }
}


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


@BindingAdapter("autoHideKeyboard")
fun bindAutoHideKeyboardOption(editText: EditText, autoHideKeyboard: Boolean) {
    if (!autoHideKeyboard) return

    editText.setOnFocusChangeListener { v, hasFocus ->
        if (!hasFocus) {
            val inputMethodManager =
                v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                v.windowToken,
                InputMethodManager.HIDE_IMPLICIT_ONLY
            )
        }
    }
}