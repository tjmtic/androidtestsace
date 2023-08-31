package com.tiphubapps.ax.data.repository

import android.util.Log
import com.tiphubapps.ax.data.repository.dataSource.UserLocalDataSource
import com.tiphubapps.ax.data.repository.dataSource.UserRemoteDataSource
import com.tiphubapps.ax.data.entity.UserEntity
import com.tiphubapps.ax.domain.repository.UserRepository
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.tiphubapps.ax.data.db.Converters
import com.tiphubapps.ax.data.repository.dataSource.succeeded
import com.tiphubapps.ax.domain.model.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import org.json.JSONObject

class UserRepositoryImpl(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val userLocalDataSource: UserLocalDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
   // val authToken: String
) :
    UserRepository {
    //In-Memory Cache
    var token: String?;

    var currentUser : String?;

    // MutableStateFlow to hold and update the local value
    private val _localValue = MutableStateFlow("Initial Value")
    // Expose the value as a StateFlow (immutable view)
    val localValue: StateFlow<String> = _localValue

    init {
        currentUser = null;
        token = null;
    }

    val isLoggedIn: Boolean
        get() = currentUser != null



    override fun updateLocalValue(value: String) {
        _localValue.value = value
    }

    override suspend fun getCurrentUser(): JsonObject? =
        userRemoteDataSource.getCurrentUser()

    override suspend fun getCurrentUserWithToken(token: String): JsonObject? {
        Log.d("TIME123", "GEtting User with token ${token}")
        return withContext(ioDispatcher) {
            userRemoteDataSource.setUserToken(token)
            return@withContext userRemoteDataSource.getCurrentUser()
        }
    }

    override suspend fun getUserById(id: String, token: String): JsonObject? {
        userRemoteDataSource.setUserToken(token)
        return userRemoteDataSource.getUserById(id)
    }
    override suspend fun getUsersById(historyIds: JsonArray, contributorIds: JsonArray, token: String): JsonObject? {
        userRemoteDataSource.setUserToken(token)
        return userRemoteDataSource.getAllUsersById(historyIds, contributorIds)
    }

    override suspend fun createSessionByUsers(data: JsonObject, token: String): JsonObject? {
        userRemoteDataSource.setUserToken(token)
        return userRemoteDataSource.createSessionByUsers(data)
    }

    override suspend fun getAllUsers() =
        userRemoteDataSource.getAllUsers()

    override suspend fun getAllUsersWithToken(token: String): JsonArray? {
        userRemoteDataSource.setUserToken(token)
        Log.d("TIME123","GETTING ALL USERS 2");
        val allUsersResp = userRemoteDataSource.getAllUsers()
        Log.d("TIME123","GETTING ALL USERS RESPONSE 2" + allUsersResp.toString());

        return allUsersResp;
    }

    override fun getUsersFromDB(userId: Int): Flow<User?> {
        val baseUser = userLocalDataSource.getUsersFromDB(userId)

        //TODO:
        // NEED DTO OBJECT FROM DATA > DOMAIN
        // ADD A CONVERTER!
        //Cool.
        return baseUser.mapLatest {
            it?.let { it1 -> Converters.userFromUserEntity(it1) }
        }
    }

    override suspend fun postLogin(email:String, password:String): JsonObject? {
        Log.d("TIME123", "ACtual;ly loging in. 444.." + email + password)
        //return userRemoteDataSource.postLogin(email, password)

        return withContext(ioDispatcher) {
            val result = userRemoteDataSource.postLogin(email, password)
            Log.d("TIME123", "ACtual;ly loging in. 444xxx.." + result.toString())

            /*if (result is Result.Success) {
            setLoggedInUser(result.data)
        }*/
            result?.get("token")?.let {
                //this.token = it.asString;
                setCurrentToken(it.asString)
                Log.d("TIME123", "ACtual;ly loging in. 666.." + token)
                Log.d("TIME123", "ACtual;ly loging in. 777.." + getCurrentToken())
                setLoggedInUser(it.asString)

                updateLocalValue(it.asString)
            }

            return@withContext result
        }
    }

    fun setCurrentToken(value: String){
        this.token = value
    }
    override fun getCurrentToken(): String?{
        return token;
    }

    override fun getLocalValueFlow(): StateFlow<String>{
        return localValue
    }

    private fun setLoggedInUser(loggedInUserToken: String) {
        this.token = loggedInUserToken
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore

        //set application token


    }

    override suspend fun logout(){

        //val result = userRemoteDataSource.postLogout();

        /*if (result is Result.Success) {
            setLoggedInUser()
        }*/

        /*result?.get("status")?.let{
            this.token = null;
            Log.d("TIME123", "ACtual;ly loging in. 666.." + token)
            Log.d("TIME123", "ACtual;ly loging in. 777.." + getCurrentToken())
            //setLoggedInUser(result.data)
        }*/

        token = null;
        currentUser = null;


    }
}