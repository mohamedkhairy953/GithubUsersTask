package com.khairy.user_list.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide
import com.khairy.core.helpers.Util
import com.khairy.core.helpers.extensions.shouldShow
import com.khairy.core.helpers.getDrawableImage
import com.khairy.shared_models.models.User
import com.khairy.user_list.R
import com.khairy.user_list.databinding.ItemUserBinding
import java.util.*


private const val USER_HOLDER = 1
private const val LAST_LOADING_ITEM_HOLDER = 2

private val LAST_LOADING_ITEM = User(0, "loading_item", "loading_item", "loading_item")

/**
 *  User list adapter;
 *  Created by using DiffUtil
 *
 *  Have two Holders: USER_HOLDER and LAST_LOADING_ITEM_HOLDER (For pagination)
 *
 *  @param onClick ([User], ImageView) -> Unit
 */
class UserListAdapter(private val onClick: (user: User, avatarImg: ImageView) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private val diffCallback = object : DiffUtil.ItemCallback<User>() {

        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var isLastLoadingItemIsShown = false

    fun showLastLoadingItem(show: Boolean) {
        if (show != isLastLoadingItemIsShown) isLastLoadingItemIsShown = show
    }

    /**
     *  Updating the list
     *  Add/Remove the last loading Item if its required
     */
    fun submitList(list: List<User>) {
        differ.submitList(
            list.toMutableList().apply {
                if (isLastLoadingItemIsShown && !list.contains(LAST_LOADING_ITEM))
                    add(LAST_LOADING_ITEM)
                else if (!isLastLoadingItemIsShown && list.contains(LAST_LOADING_ITEM))
                    remove(LAST_LOADING_ITEM)
            }
        )
    }

    /**
     *  @return 1 (USER_HOLDER) if is user object; Or 2 (LAST_LOADING_ITEM_HOLDER) if is a last position and isLastLoadingItemIsShown = true
     */
    override fun getItemViewType(position: Int): Int {
        return if (position == differ.currentList.size - 1 && isLastLoadingItemIsShown) LAST_LOADING_ITEM_HOLDER else USER_HOLDER
    }

    /**
     *  @return Instance of [UserHolder] or [LAST_LOADING_ITEM_HOLDER]
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            USER_HOLDER -> {
                UserHolder(
                    parent.context,
                   ItemUserBinding.inflate( LayoutInflater.from(parent.context),
                       parent, false
                    ), onClick
                )
            }

            LAST_LOADING_ITEM_HOLDER -> {
                LastLoadingItemHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_user_last_loading, parent, false
                    )
                )
            }

            else -> {
                LastLoadingItemHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_user_last_loading, parent, false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is UserHolder -> {
                holder.bind(differ.currentList[position], position)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    /**
     *  User Holder
     *  Every fourth element will be with inverted avatar colors and different background
     */
    class UserHolder(
        private val context: Context,
        private val binding: ItemUserBinding,
        private val onClick: (user: User, avatarImg: ImageView) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: User, position: Int) = with(binding) {

            itemUserNameTextView.text = item.login.capitalize(Locale.getDefault())

            itemUserNoteImg.shouldShow(item.notes.isNotBlank())

            itemUserLayout.background = context.getDrawableImage(
                if (position % 4 == 0) {
                    R.drawable.bg_item_user_second_color_ripple
                } else {
                    R.drawable.bg_item_user_ripple
                }
            )

            itemUserAvatarImg.apply {
                transitionName = item.avatarUrl

                colorFilter = if (position % 4 == 0) Util.getInvertedColorFilter() else null

                Glide.with(context)
                    .load(item.avatarUrl)
                    .error(context.getDrawableImage(R.drawable.ic_user))
                    .into(this)
            }

            root.setOnClickListener {
                onClick(item, itemUserAvatarImg)
            }
        }
    }

    class LastLoadingItemHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView)
}