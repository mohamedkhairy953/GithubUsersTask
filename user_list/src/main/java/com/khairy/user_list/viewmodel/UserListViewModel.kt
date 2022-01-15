package com.khairy.user_list.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khairy.core.helpers.network.Resource
import com.khairy.shared_models.models.User
import com.khairy.user_list.model.repo.UserRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UserListViewModel @Inject constructor(private val userRepository: UserRepositoryImpl) :
    ViewModel() {

    private var userLoadingJob: Job? = null

    var isUsersLoading = false

    private val _users = MutableLiveData<Resource<List<User>>>()
    val users: LiveData<Resource<List<User>>>
        get() = _users

    /**
     *  Download users list, use existingUsers to download 30 more
     *  By default download first 30 users
     *
     *  @param existingUsers List of [User] (optional)
     */
    fun getUsers(existingUsers: List<User> = listOf()) {
        cancelAnyUserLoadingJobJob()

        isUsersLoading = true

        userLoadingJob = viewModelScope.launch {

            userRepository.getUsers(existingUsers).onEach { resultUsers ->
                _users.value = resultUsers
            }.launchIn(viewModelScope)
        }
    }

    fun searchUsers(input: String) {
        cancelAnyUserLoadingJobJob()
        userLoadingJob = viewModelScope.launch {
            userRepository.searchUsers(input).cancellable().collect { user ->
                _users.value = user
            }
        }
    }

    fun cancelAnyUserLoadingJobJob() {
        userLoadingJob?.cancel()
    }
}