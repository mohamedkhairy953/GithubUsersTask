package com.khairy.user_list.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.khairy.core.helpers.network.Resource
import com.khairy.core.test_utils.CoroutineTestRule
import com.khairy.core.test_utils.observeOnce
import com.khairy.shared_models.models.User
import com.khairy.user_list.model.repo.UserRepositoryImpl
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class UserListViewModelTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    private lateinit var viewModel: UserListViewModel
    private lateinit var repo: UserRepositoryImpl
    private lateinit var responseBody: User

    @Before
    fun setUp() {
        repo = mockk()
        viewModel = UserListViewModel(repo)
    }

    @Test
    fun `fetch list  successful`() {
        responseBody = mockk()
        val response = Resource.Success(listOf(responseBody))
        runBlockingTest {
            val r = getMockedRepo(response)
            viewModel = UserListViewModel(r)
            viewModel.getUsers(listOf())
            var responseEntity: List<User>? = null
            viewModel.users.observeOnce {
                responseEntity = it.getData
            }
            Assert.assertEquals(responseEntity?.size, 1)
        }
    }

    private fun getMockedRepo(response: Resource<List<User>>): UserRepositoryImpl {
        return mockk {
            io.mockk.coEvery { getProductList(any()) } returns flowOf(response)
            io.mockk.coEvery { searchUsers(any()) } returns flowOf(response)
        }
    }
}