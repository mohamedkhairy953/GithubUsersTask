package com.khairy.user_profile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khairy.core.helpers.network.Resource
import com.khairy.database.entities.NotesEntity
import com.khairy.shared_models.models.User
import com.khairy.user_profile.model.repo.ProfileRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(private val profileRepository: ProfileRepositoryImpl) :
    ViewModel() {

    private var job: Job? = null

    private val _user = MutableLiveData<Resource<User>?>()
    val user: LiveData<Resource<User>?>
        get() = _user

    fun initialization(userId: Long?, userLogin: String?) {
        if (userId != null && userLogin != null) {

            _user.value = null
            cancelAnyWorkingJob()

            getUserPortfolio(userId, userLogin)
        }
    }

    /**
     *  Download user portfolio and add it to local database user
     */
    private fun getUserPortfolio(userId: Long, userLogin: String) {
        cancelAnyWorkingJob()

        job = viewModelScope.launch {
            profileRepository.getUserPortfolioData(userId, userLogin).onEach { user ->
                _user.value = user
            }.launchIn(viewModelScope)
        }
    }

    fun saveNote(notes: String) {
        _user.value?.getData?.notes = notes

        viewModelScope.launch {
            user.value?.getData?.id?.let { userId ->
                profileRepository.updateUserNotes(NotesEntity(userId, notes))
            }
        }
    }

    private fun cancelAnyWorkingJob() {
        job?.cancel()
    }
}