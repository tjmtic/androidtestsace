package com.farhan.tanvir.androidcleanarchitecture.presentation.screen.details

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.farhan.tanvir.androidcleanarchitecture.presentation.navigation.Screen
import com.farhan.tanvir.domain.model.User
import com.farhan.tanvir.domain.repository.UserRepository
import com.farhan.tanvir.domain.useCase.GetCurrentUserUseCase
import com.farhan.tanvir.domain.useCase.UserUseCases
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject
import com.farhan.tanvir.androidcleanarchitecture.util.Result

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userUseCases: UserUseCases,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Login)
    val uiState: StateFlow<LoginUiState> = _uiState
    private val _networkUiState = MutableStateFlow<NetworkUiState>(NetworkUiState.Neutral)


    private val _username = MutableStateFlow<String>("");
    val username: StateFlow<String> = _username
    private val _password = MutableStateFlow<String>("");
    val password: StateFlow<String> = _password


    fun updateUsername(update: String){ _username.value = update;}
    fun updatePassword(update: String){ _password.value = update;}


    //CONVERT TO FLOW
    //ON COLLECT IF STATE IS LOGIN.SUCCESS -> navigateToHOme

    private val _selectedUser: MutableStateFlow<JsonObject?> = MutableStateFlow(null)
    val selectedUser: StateFlow<JsonObject?> = _selectedUser

    private val _selectedToken = userRepository.getCurrentToken();//MutableStateFlow<String> = MutableStateFlow("")
    //val selectedToken: StateFlow<String> = _selectedToken

    val isLoggedIn : MutableStateFlow<Boolean> = MutableStateFlow(false)
    //combineAndCompute( selectedUser && selectedToken )

    fun getUserDetails(userID: Int) {
        viewModelScope.launch {
            userUseCases.getUsersFromDBUseCase.invoke(userID).collect {
               // _selectedUser.value = it
            }
        }
    }

    fun postLogin() {
        viewModelScope.launch (
            Dispatchers.IO, CoroutineStart.DEFAULT
        ){
            //Show Loading
            _networkUiState.value = NetworkUiState.Loading
            //Send Request
            ////TODO: Should make a UseCaseFactory , implement invoke() method calls for injection / hoisting
            userUseCases.postLoginUseCase.username = username.value
            userUseCases.postLoginUseCase.password = password.value

            //Should set value as current user token in user repository in use case
            //This object should hold the network response (success/err/err)
            val response: JsonObject? = userUseCases.postLoginUseCase.invoke()

            //val response: Result<String> = userUseCases.postLoginUseCase.invoke()
           /* response?.get("token")?.let{
                           // _selectedToken.value = it.asString;
                println(_selectedToken.value)

            }*/

            //Remove Loading, Display Error
            //when (response) {
            //    is Result.Success -> networkUiState.value = NetworkUiState.Success
                //is Response.Failure -> networkUiState.value = NetworkUiState.Failure(it.value)
             //   is Result.Error -> networkUiState.value = NetworkUiState.Error(it.value)
            //}

            if(userRepository.getCurrentToken() != null){
                _uiState.value = LoginUiState.Home
            }
        }
    }

    fun postSignup(username:String, password:String){
        println("SIGNING UP WITH ${username} and ${password}")
    }

    fun postForgot(username:String){
        println("FORGOT ACCOUNT WITH ${username}")
    }

    fun getCurrentUser() {
       /* viewModelScope.launch {
            _selectedUser.value = userUseCases.getCurrentUserUseCase()
            // navController.navigate(route = Screen.Home.route)
            Log.d("TIME123", "New current user:" + _selectedUser.value)

        }*/
        viewModelScope.launch(
            Dispatchers.IO, CoroutineStart.DEFAULT
        ) {
            withNetworkModal(userUseCases.getCurrentUserUseCase,
                            {user: JsonObject -> showCurrentUser(user)},
                            {errorMsg: String -> showError(errorMsg)})
        }
    }

    fun showCurrentUser(user: JsonObject){

    }
    fun showError(msg: String){
        //toast - text
    }

    suspend fun withNetworkModal(wrappedContent: GetCurrentUserUseCase,
                                 successCallback: (JsonObject) -> Unit,
                                 errorCallback: (String) -> Unit){
            //Show Loading
            _networkUiState.value = NetworkUiState.Loading
            //Send Request
           // _selectedToken.value = wrappedContent()
            //Remove Loading, Display Data, Display Error
           // when (_selectedToken.value) {
                //Success -> networkUiState.value = NetworkUiState.Success ; successCallback(_selectedToken.value);
                //Failure -> networkUiState.value = NetworkUiState.Failure(errorMsg) ; errorCallback(errorMsg);
                //Unknown -> networkUiState.value = NetworkUiState.Failure("Unknown Error");
          //  }
    }

    fun showLogin(){
        _uiState.value = LoginUiState.Login
    }
    fun showSignup(){
        _uiState.value = LoginUiState.Signup
    }
    fun showForgot(){
        _uiState.value = LoginUiState.Forgot
    }

    sealed class LoginUiState {
        object Home: LoginUiState()
        object Login: LoginUiState()
        object Signup: LoginUiState()
        object Forgot: LoginUiState()
        data class Error(val exception: Throwable): LoginUiState()
    }

    sealed class NetworkUiState {
        object Neutral: NetworkUiState()
        object Success: NetworkUiState()
        object Loading: NetworkUiState()
        data class Failure(val error: String): NetworkUiState()
        data class Error(val exception: Throwable): NetworkUiState()
    }
}