package com.example.android.politicalpreparedness

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
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
fun bindPhotoUrl(imageView: ImageView, url: String) {
    Glide.with(imageView)
        .load(url)
        .error(R.drawable.ic_profile)
        .into(imageView)
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