package com.khairy.user_profile.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.khairy.core.helpers.MainShareViewModel
import com.khairy.core.helpers.extensions.shouldShow
import com.khairy.core.helpers.hideKeyBoard
import com.khairy.core.helpers.network.Resource
import com.khairy.shared_models.*
import com.khairy.shared_models.models.User
import com.khairy.user_profile.R
import com.khairy.user_profile.databinding.FragmentProfileBinding
import com.khairy.user_profile.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import java.util.*

@InternalCoroutinesApi
@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private val viewModel: ProfileViewModel by activityViewModels()
    private val sharedViewModel: MainShareViewModel by activityViewModels()
    lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)

        binding.profileAvatarImg.apply {

            transitionName = arguments?.getString(USER_AVATAR_URL)

            Glide.with(this).load(arguments?.getString(USER_AVATAR_URL)).into(this)
        }
    }

    override fun onStart() {
        super.onStart()

        viewModel.initialization(arguments?.getLong(USER_ID), arguments?.getString(USER_LOGIN))

        initObserverListeners()

        initViewsListeners()
    }

    private fun initObserverListeners() {

        sharedViewModel.internetConnectionListener.observe(viewLifecycleOwner) {
            val isInternetConnectionReturned =
                it && it != sharedViewModel.internetConnectionListener.previousInternetConnectionState

            if (isInternetConnectionReturned) {
                viewModel.initialization(
                    arguments?.getLong(USER_ID),
                    arguments?.getString(USER_LOGIN)
                )
            }

            binding.profileNoWifiImg.shouldShow(!it)
        }

        viewModel.user.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer

            when (it) {

                is Resource.Loading -> {
                    it.getData?.profile?.let { _ ->
                        setupUserUiData(it.getData!!)
                    }
                }

                is Resource.Success,
                is Resource.Error -> {
                    it.getData?.let { user -> setupUserUiData(user) }
                }
            }
        })
    }

    private fun initViewsListeners() {

        binding.profileButtonSave.setOnClickListener {

            this.hideKeyBoard()
            viewModel.saveNote(binding.profileNotesInput.text.toString())
            Snackbar.make(it, getString(R.string.note_saved), Snackbar.LENGTH_LONG).show()
        }

        binding.profileToolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    @SuppressLint("SetTextI18n")
    fun setupUserUiData(user: User) {

        binding.profileToolbar.title = user.login.capitalize(Locale.getDefault())

        binding.profileFollowers.text =
            "${getString(R.string.followers)}: ${user.profile?.followers ?: 0}"
        binding.profileFollowing.text =
            "${getString(R.string.following)}: ${user.profile?.following ?: 0}"

        binding.profileUserName.text =
            "${getString(R.string.name)}: ${user.profile?.name ?: getString(R.string.no_name)}"

        binding.profileUserCompany.text =
            "${getString(R.string.company)}: ${user.profile?.company ?: getString(R.string.no_company)}"

        binding.profileUserBlog.text =
            "${getString(R.string.blog)}: ${user.profile?.blog ?: getString(R.string.no_blog)}"

        binding.profileNotesInput.setText(user.notes)
    }
}