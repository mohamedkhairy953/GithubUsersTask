package com.khairy.core.helpers.network

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@ExperimentalCoroutinesApi
@SmallTest
class NetworkBoundResourceTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private var localDatabaseElements = mutableListOf(1, 2, 3, 4, 5)

    @Test
    fun `networkBoundResource test`() = runBlockingTest {
        val request = networkBoundResource(
            fromCache = { getLocalDatabaseElements() },
            requestCall = { requestNewElementsFromServer() },
            saveInCache = { newElements -> saveNewElementsInDatabase(newElements) }
        )

        request.collect {
            when (it) {
                is Resource.Success -> {
                    Assert.assertEquals(it.data.size,10)
                }
                is Resource.Loading -> {
                    Assert.assertEquals(it.data?.size,5)
                }
                else -> {
                    //Do nothing
                }
            }
        }
    }

    @Test
    fun `networkBoundResource returns local data if requestCall return exception`() =
        runBlockingTest {
            val request = networkBoundResource(
                fromCache = { getLocalDatabaseElements() },
                requestCall = { requestNewElementsFromServerExceptionScenario() },
                saveInCache = { newElements -> saveNewElementsInDatabase(newElements) }
            )

            request.collect {
                when (it) {
                    is Resource.Loading -> {
                        Assert.assertEquals(it.data?.size,5)
                    }
                    is Resource.Error -> {
                        Assert.assertEquals(it.data?.size,5)
                    }
                    else -> {
                        //Do nothing
                    }
                }
            }
        }

    private fun getLocalDatabaseElements() = flow {
        emit(localDatabaseElements)
    }

    private fun requestNewElementsFromServer(): List<Int> {
        return listOf(6, 7, 8, 9, 10)
    }

    private fun requestNewElementsFromServerExceptionScenario(): List<Int> {
        throw IOException("Test Error")
    }

    private fun saveNewElementsInDatabase(newElements: List<Int>) {
        localDatabaseElements.addAll(newElements)
    }
}