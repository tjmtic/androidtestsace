package com.tiphubapps.ax.rain.presentation.screen.details

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.capitalize
import androidx.lifecycle.*
import com.tiphubapps.ax.rain.Rain
import com.tiphubapps.ax.domain.repository.UserRepository
import com.tiphubapps.ax.domain.useCase.GetCurrentUserUseCase
import com.tiphubapps.ax.domain.useCase.UserUseCases
import com.google.gson.JsonObject
import com.tiphubapps.ax.domain.repository.AndroidFrameworkRepository
import com.tiphubapps.ax.domain.repository.AppError
import com.tiphubapps.ax.domain.repository.UseCaseResult
import com.tiphubapps.ax.rain.R
import com.tiphubapps.ax.rain.presentation.helper.performVibration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import com.tiphubapps.ax.rain.util.SessionManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import java.util.Locale
import kotlin.math.absoluteValue

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userUseCases: UserUseCases,
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager,
    private val application: Application,
) : AndroidViewModel(application) {

    init {
       println("TIME123 LoginViewModel Start")
        //Initialize token from DB to check for loginUiState?
        //TODO: SHOULD DO THIS IN MAINACTIVITY?
        sessionManager.getEncryptedPreferencesValue("userToken")?.let {
            userRepository.updateLocalValue(it)
        }
    }

    ///////// ViewModel State setup ///////////
    //(OPEN) slugs
    private val _state = MutableStateFlow(LoginViewState())
    val state : StateFlow<LoginViewState> = _state

    //(PIPE) Expose the values as a Flows to the UI
    val localValueFlow: StateFlow<String> = userRepository.getLocalValueFlow()

    //(MIX) Transform Data as appropriate
    // ...

    ///////////////////////////////////////////

    //////////////////TODO: Get these methods out///////////////////////////////////////////////////
    // Remove need for APPLICATION references
    // Then AndroidViewModel can be jsut ViewModel??????!!!!!!!!!
    // Also hard to test these separately

    ////////Android Framework (Espresso Instrumented?)///////////////
    //Add haptic interactions on Composable states instead of calling them here
    //Will remove Android Framework calls from ViewModel
    fun handleError(error: String){
        //Move this to composable, on viewing/animating new error (and short one on swipe out?)
        //Then this method can be removed entirely
        performVibration(context = application.applicationContext)
        updateErrorMessage(error)
    }
    //Pretty sure this should be in the Application at the very least
    //Would rather it be part of Compose error handling, or other message handling from Compose side
    private fun showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        application.applicationContext.let {
            Toast.makeText(it, message, duration).show()
        }
    }
    //////////////////////////////////////////////////////

    //TODO: implement injection of this?
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        val errorMessage = throwable.message ?: "An error occurred"
        showToast(errorMessage)
    }
    ///////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////View Events  --- View-ViewModel State Changes/////////////////////////
    //JUnit ViewModel tests
    fun onEvent(event: LoginViewEvent) {
        when (event) {
            is LoginViewEvent.EmailChanged -> {
                _state.value = _state.value.copy(email = event.email)
            }
            is LoginViewEvent.PasswordChanged -> {
                _state.value = _state.value.copy(password = event.password)
            }
            is LoginViewEvent.NameChanged -> {
                _state.value = _state.value.copy(name = event.name)
            }
            is LoginViewEvent.LoginClicked -> {
                postLogin(_state.value.email, _state.value.password)
            }
            is LoginViewEvent.SignupClicked -> {
                postSignup(_state.value.email, _state.value.password)
            }
            is LoginViewEvent.ForgotClicked -> {
                postForgot(_state.value.email)
            }
            is LoginViewEvent.ConsumeError -> {
                _state.value = _state.value.copy(errors = _state.value.errors.filterNot { it  == _state.value.error }, error = "")

                //Reset Current Error if one exists
                if (_state.value.errors.isNotEmpty()) { _state.value = _state.value.copy(error = _state.value.errors[0]) }
            }

            else -> {}
        }
    }
    internal fun updateErrorMessage(error: String){
        val errorMessage = when(error){
            "Service Issue."  -> {
                "Service Issue."
            }
            "Input Error."  -> {
                "Input Error."
            }
            "Network Error" -> {
                "Network Error"
            }

            else -> { "Unknown Error" }
        }
        _state.value = _state.value.copy(error = errorMessage, errors = _state.value.errors.plus(errorMessage))
    }
    ////////////////////////////

    /////////////////////////////COROUTINES//////////////////////////////
    //Model Events ---  ViewModel-Model State -- Mocked Data Tests?
    fun postLogin(username: String, password:String) {
        viewModelScope.launch (
            //TODO: Injected dispatcher here
            Dispatchers.IO, CoroutineStart.DEFAULT
        ) {
                //TODO: Can this be done when response = Result.Loading?
                // -- currently there is no Result.Loading...
                //Show Loading
                _state.value = _state.value.copy(isLoading = true)

                //Display Error or Success
                //TODO: Should pass SessionManager with call???
                // Implicitly save token, only need to display on error?
                // NO -- do not put higher level module as dependency in lower level module
                // -- IF sessionManager was a module in DOMAIN it would be acceptable? UTIL module (Feature-Module Architecture)
                when (val response: UseCaseResult<JsonObject> = userUseCases.useCaseLogin(username, password)) {

                       is UseCaseResult.UseCaseSuccess -> (response.data).get("data").let {
                           sessionManager.setUserToken(it.asString)
                       }
                       is UseCaseResult.UseCaseError -> {
                           response.exception.message?.let { handleError(it) }
                       }

                        else -> { println("Loading --- Login") }
                }
                //Hide loading
                _state.value = _state.value.copy(isLoading = false)
        }
    }

    fun postSignup(username:String, password:String){
        //TODO: Implement this call
        println("SIGNING UP WITH ${username} and ${password}")
    }

    fun postForgot(username:String){
        //TODO: Implement this call
        println("FORGOT ACCOUNT WITH ${username}")
    }
    /////////////////////////////////////////////////////////////////

    ////////ViewState Changes (UI tests)//////////////////
    fun showLogin(){
        _state.value = _state.value.copy(viewState = LoginUiState.Login)
    }
    fun showSignup(){
        _state.value = _state.value.copy(viewState = LoginUiState.Signup)
    }
    fun showForgot(){
        _state.value = _state.value.copy(viewState = LoginUiState.Forgot)
    }
    ////////////////////////////////////////////////////////


    //ViewModel State Classes
    data class LoginViewState(
        val name: String = "",
        val email: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
        val error: String = "",
        val errors: List<String> = emptyList(),
        val viewState: LoginUiState = LoginUiState.Default
    )

    sealed class LoginUiState {
        object Default: LoginUiState()
        object Home: LoginUiState()
        object Login: LoginUiState()
        object Signup: LoginUiState()
        object Forgot: LoginUiState()
        data class Error(val exception: Throwable): LoginUiState()
    }


    //ViewModel Events
    sealed class LoginViewEvent {
        data class EmailChanged(val email: String) : LoginViewEvent()
        data class PasswordChanged(val password: String) : LoginViewEvent()
        data class NameChanged(val name: String) : LoginViewEvent()
        object LoginClicked : LoginViewEvent()
        object SignupClicked : LoginViewEvent()
        object ForgotClicked : LoginViewEvent()
       // data class CreateError(val name: String) : LoginViewEvent()
        object ConsumeError : LoginViewEvent()
    }



}