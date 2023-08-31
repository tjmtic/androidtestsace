package com.tiphubapps.ax.data.test

import android.util.Log
import com.tiphubapps.ax.data.repository.UserRepositoryImpl
import com.tiphubapps.ax.data.entity.UserEntity
import com.tiphubapps.ax.domain.model.User
import com.tiphubapps.ax.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
internal class UserRepositoryImplTest {

    private val task1 = UserEntity(0, 1, "a1", 0, 0, 0, "[]", "[]", "[]", "[]", "[]", 0, "test@email.com", "test 1", "", "", "", "","","","")

    private val task2 = UserEntity(1, 2, "b2", 0, 0, 0,
        "[]", "[]", "[]", "[]", "[]", 0,
        "test2@email.com", "test 2", "", "", "",
        "","","","")

    private val task3 = UserEntity(2, 3, "c3", 0, 0, 0,
        "[]", "[]", "[]", "[]", "[]", 0,
        "test3@email.com", "test 3", "", "", "",
        "","","","")


    private val remoteUsers = listOf(task1, task2).sortedBy { it.id }
    private val localUsers = listOf(task3).sortedBy { it.id }
    private val newUsers = listOf(task3).sortedBy { it.id }


    private val user3 = User(2, 3, "c3", 0, 0, 0,
        "[]", "[]", "[]", "[]", "[]", 0,
        "test3@email.com", "test 3", "", "", "",
        "","","","")


    private lateinit var usersRemoteDataSource: FakeRemoteDataSource
    private lateinit var usersLocalDataSource: FakeLocalDataSource

    // Class under test
    private lateinit var usersRepository: UserRepositoryImpl

    @Before
    fun createRepository(){
        //Initialize Data
        usersRemoteDataSource = FakeRemoteDataSource(remoteUsers.toMutableList())
        usersLocalDataSource = FakeLocalDataSource(localUsers.toMutableList())

        usersRepository = UserRepositoryImpl(usersRemoteDataSource, usersLocalDataSource, Dispatchers.Unconfined)

    }

    @Test
    fun getToken() {

        //Hypothesis
        val expected = null

        //Experiment
        val actual = usersRepository.token

        //Evaluate
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun setToken() {
        //Hypothesis
        val expected = "test"

        //Experiment
        usersRepository.setCurrentToken("test")

        //Evaluate
        val actual = usersRepository.token
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun getCurrentUser() {
        //Hypothesis
        val expected = null
        //Experiment
        val actual = usersRepository.currentUser
        //Evaluate
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun setCurrentUser() {
        //Hypothesis
        val expected = "test"
        //Experiment
        usersRepository.currentUser = "test"
        //Evaluate
        val actual = usersRepository.currentUser
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun getLocalValue() {
        //Hypothesis
        val expected = "Initial Value"
        //Experiment
        val actual = usersRepository.getLocalValueFlow().value
        //Evaluate
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun isLoggedIn() {
        //Hypothesis
        val expected = false
        //Experiment
        val actual = usersRepository.isLoggedIn
        //Evaluate
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun updateLocalValue() {

        //Hypothesis
        val expected = "test"

        //Experiment
        usersRepository.updateLocalValue("test")

        //Evaluate
        val actual = usersRepository.localValue.value
        Assert.assertEquals(expected, actual)
    }





    @Test
    fun testGetCurrentUser() {
    }

    @Test
    fun getCurrentUserWithToken() {
    }

    @Test
    fun getUserById() {
    }

    @Test
    fun getUsersById() {
    }

    @Test
    fun createSessionByUsers() {
    }

    @Test
    fun getAllUsers() {

    }

    @Test
    fun getAllUsersWithToken() {
    }

    @Test
    fun getUsersFromDB() = runBlockingTest {

            //Hypothesis
            val expected = user3
            lateinit var actual : User

            //Experiment
            val new = usersRepository.getUsersFromDB(3).also{

            }

            new.collect{
                println("TIME123 COllection value: ${it}")
                if (it != null) {
                    actual = it
                }
            }

            new.collectLatest{
                println("TIME123 COllection value: ${it}")
                if (it != null) {
                    actual = it
                }
            }

        println("TIME123 COllection END")
            //Evaluate
            Assert.assertEquals(expected, actual)
    }

    @Test
    fun postLogin() {
    }

    @Test
    fun setCurrentToken() {
    }

    @Test
    fun getCurrentToken() {
    }

    @Test
    fun getLocalValueFlow() {
    }

    @Test
    fun logout() {
    }
}