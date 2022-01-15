package com.khairy.core.helpers

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 *  Shared ViewModel is used to connect ViewModels
 *
 *  Functionality:
 *  1) internetConnectionListener LiveData;
 */
@HiltViewModel
class MainShareViewModel
@Inject
constructor(val internetConnectionListener: InternetConnectionListener) : ViewModel()
