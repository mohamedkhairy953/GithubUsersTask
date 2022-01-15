package com.khairy.user_list.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.khairy.core.helpers.MainShareViewModel
import com.khairy.core.helpers.PaginationScrollListener
import com.khairy.core.helpers.extensions.shouldShow
import com.khairy.core.helpers.isInternetConnected
import com.khairy.core.helpers.network.Resource
import com.khairy.shared_models.*
import com.khairy.shared_models.models.User
import com.khairy.user_list.R
import com.khairy.user_list.databinding.FragmentUserListBinding
import com.khairy.user_list.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi


@InternalCoroutinesApi
@AndroidEntryPoint
class UserListFragment : Fragment() {

    private val viewModel: UserListViewModel by activityViewModels()
    private val sharedViewModel: MainShareViewModel by activityViewModels()

    private lateinit var userListAdapter: UserListAdapter
    lateinit var binding: FragmentUserListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        initUsersList()

        initObserverListeners()

        initViewsListeners()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getUsers()
    }

    private fun initUsersList() {
        binding.userListRecycleView.apply {
            layoutManager = LinearLayoutManager(context)

            userListAdapter = UserListAdapter(
                onClick = { user, avatarImg -> openProfileFragment(user, avatarImg) }
            )

            adapter = userListAdapter
        }
    }

    private fun openProfileFragment(user: User, avatarImg: ImageView) {
        viewModel.cancelAnyUserLoadingJobJob()

     val bundle = bundleOf(
            USER_ID to user.id,
            USER_LOGIN to user.login,
            USER_AVATAR_URL to user.avatarUrl
        )

        val extras = FragmentNavigatorExtras(avatarImg to user.avatarUrl)

        findNavController().navigate(
            R.id.action_userListFragment_to_profileFragment,
            bundle, null, extras
        )
    }

    private fun initObserverListeners() {

        sharedViewModel.internetConnectionListener.observe(viewLifecycleOwner) {
            val isInternetConnectionReturned =
                it && it != sharedViewModel.internetConnectionListener.previousInternetConnectionState

            if (isInternetConnectionReturned) {
                viewModel.getUsers()
            }

            binding.userListNoWifiImg.shouldShow(!it)
        }

        viewModel.users.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer

            var showLoading = false

            when (it) {

                is Resource.Loading -> {
                    binding.userListSearchInput.text?.clear()

                    if (!userListAdapter.isLastLoadingItemIsShown) {
                        showLoading = true
                    }
                }

                is Resource.Success -> {
                    userListAdapter.showLastLoadingItem(false)
                    viewModel.isUsersLoading = false
                }

                is Resource.Error -> {
                    userListAdapter.showLastLoadingItem(false)

                    Snackbar.make(binding.userListToolbar, getString(R.string.error), Snackbar.LENGTH_LONG)
                        .show()
                }
            }

            it.getData?.let { users -> userListAdapter.submitList(users) }

            binding.userListSwipeRefreshLayout.isRefreshing = showLoading
        })

        viewModel.showShimmerLD.observe(viewLifecycleOwner,{
            binding.progressDialogShimmer.root.shouldShow(it)
        })
    }

    private fun initViewsListeners() {

        binding.userListSwipeRefreshLayout.setOnRefreshListener { viewModel.getUsers(isFromSwipeToRefresh = true) }

        binding.userListRecycleView.addOnScrollListener(object :
            PaginationScrollListener(binding.userListRecycleView.layoutManager as LinearLayoutManager) {

            override fun loadMoreItems() {
                viewModel.isUsersLoading = true
                userListAdapter.showLastLoadingItem(true)
                viewModel.getUsers(viewModel.users.value?.getData ?: listOf())
            }

            override val isPaginationAvailable: Boolean
                get() = binding.userListSearchInput.text?.isEmpty() ?: false &&
                        !viewModel.isUsersLoading &&
                        this@UserListFragment.requireContext().isInternetConnected
        })

        binding.userListSearchInput.doOnTextChanged { text, _, _, _ ->
            viewModel.searchUsers(text.toString())

        }

    }
}