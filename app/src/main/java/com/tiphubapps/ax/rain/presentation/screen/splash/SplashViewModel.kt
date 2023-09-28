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
import com.tiphubapps.ax.data.util.CoroutineContextProvider
import com.tiphubapps.ax.domain.repository.AndroidFrameworkRepository
import com.tiphubapps.ax.domain.repository.AppError
import com.tiphubapps.ax.domain.repository.UseCaseResult
import com.tiphubapps.ax.domain.useCase.LoginUseCases
import com.tiphubapps.ax.rain.R
import com.tiphubapps.ax.rain.presentation.helper.performVibration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import com.tiphubapps.ax.data.util.SessionManager
import com.tiphubapps.ax.domain.useCase.AuthUseCases
import com.tiphubapps.ax.domain.useCase.SplashUseCases
import com.tiphubapps.ax.rain.presentation.screen.login.AuthedViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.Locale
import javax.inject.Named
import kotlin.math.absoluteValue

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val splashUseCases: SplashUseCases,
    coroutineContextProvider: CoroutineContextProvider
) : ViewModel() {

    /////////Default ViewModel Setup///////////
    private val _state = MutableStateFlow(SplashState())
    val state : StateFlow<SplashState> = _state

    private val _eventBus = Channel<SplashEvent>()
    val eventBus = _eventBus.receiveAsFlow()

    var job : Job?
    ///////////////////////////////////////////////


    //TODO: implement injection of this?
    // how to standardize onEvent responses?
    // also, standardized onEvent a good thing?
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        //val errorMessage = throwable.message ?: "An error occurred"
        viewModelScope.launch {
            onEvent(ERROR_A)
            //TODO: should probably be fatal error here, close and re-open the app
            // standardized responses are probably not the best approach
            // default? for logging?
        }
    }

    init {
        println("TIME123 SplashViewModel Init Start")

        //TODO:
        // Init Firebase - mainActivity?
        // Init Analytics/Logging?
        // Init Background Services
        // Init Websocket
        // Init UserData -- complete
        // awaitAll()

        job = viewModelScope.launch(
            coroutineContextProvider.io + coroutineExceptionHandler, CoroutineStart.DEFAULT
        ) {
            try {
                onEvent(LOADING_INIT)

                when (splashUseCases.useCaseAuthGetToken()) {
                    is UseCaseResult.UseCaseSuccess -> {
                        onEvent(TOKEN_LOADED)
                        initUserData()
                    }

                    else -> {
                        onEvent(LOGIN_FAIL)
                    }
                }

                onEvent(LOADING_STOP)

            } catch (e: Exception) {
                onEvent(ERROR_0)
                return@launch
            }
        }
        println("TIME123 SplashViewModel Init End")
    }

    private suspend fun initUserData(){
        when(splashUseCases.useCaseUserGetCurrentUser()){
            is UseCaseResult.UseCaseSuccess -> { onEvent(LOGIN_SUCCESS) }
            is UseCaseResult.Loading -> Unit
            else -> { onEvent(ERROR_1) }
        }
    }

//////////////////////////////
            //val bbcArticles = scrapeArticlesFromBBCNews()

           /* bbcArticles.forEachIndexed { index, article ->
                println("Article ${index + 1}:")
                println("Title: ${article.title}")
                println("Description: ${article.description}")
                println("Link: ${article.link}")
                println("header: ${article.header}")
                println("image: ${article.image}")
                println("par: ${article.desc}")

                println()
            }*/
////////////////////////////////////


     //   }
            /////////////////////////////////////////////////////////////////////////////////
        //}
    //    println("TIME123 SplashViewModel Init End")
    //}

    data class Article(
        val title: String,
        val description: String,
        val link: String,
        val header: String,
        val image: String,
        val desc: String
    )


    fun scrapeArticlesFromBBCNews(): List<Article> {
        val url = "https://www.bbc.com/news"
        val articles = mutableListOf<Article>()

        val fullArticles = mutableListOf<Article>()

        try {
            val doc: Document = Jsoup.connect(url).get()
            val elements = doc.select("div.gs-c-promo")

            for (element in elements) {
                val title = element.select("h3.gs-c-promo-heading").text()
                val description = element.select("p.gs-c-promo-summary").text()
                val link = element.select("a.gs-c-promo-heading").attr("href")

                val fullLink = if(link.contains("www")) link else "https://www.bbc.com$link"

                val doc2: Document = Jsoup.connect(fullLink).get()
                val elements2 = doc2.select("#main-content")

                val header = elements2.select("#main-heading").text()
                val image = elements2.select("picture source").attr("srcset")

                val desc = elements2.select("p b").joinToString { it.text() }

                val article = Article(title, description, fullLink, header, image, desc)
                articles.add(article)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return articles
    }

    override fun onCleared() {
        //TODO: How to test?
        viewModelScope.cancel()

        //TODO: Also, is this redundant call after viewModelScope?
        job?.cancel()
    }

    /////////////////////State-UI Events/////////////////////////
    //Update STATE -> Send EVENT
    private suspend fun onEvent(event: SplashViewEvent) {
        when (event) {
            is SplashViewEvent.LoadingStarted -> {
                _state.value = _state.value.copy(isLoading = true)
                _eventBus.send(SplashEvent.SPLASHING)
            }
            is SplashViewEvent.LoadingFinished -> {
                _state.value = _state.value.copy(isLoading = false)
                _eventBus.send(SplashEvent.SPLASHED)
            }
            is SplashViewEvent.LoadingError -> {
                _state.value = _state.value.copy(isLoading = false, error = event.message)
                _eventBus.send(SplashEvent.Error(msg = event.message))
            }
            is SplashViewEvent.TokenLoaded -> {
                //_state.value = _state.value.copy(isLoggedIn = true)
                println("Event Called -- Token Loaded from AuthRepo")
            }
            is SplashViewEvent.LoginFinished -> {
                _state.value = _state.value.copy(isLoggedIn = true)
            }
            is SplashViewEvent.LoginFailed -> {
                _state.value = _state.value.copy(isLoggedIn = false)
            }
            else -> { println("Unknown Event Called") }
        }
    }
    ////////////////////////////////////////////////////////////


    //ViewModel State (i.e. Status Implementation)
    data class SplashState(
        val isLoggedIn: Boolean = false,
        val isLoading: Boolean = false,
        val error: String? = null
    )

    //UI State-Events (i.e. Status Interface)
    sealed class SplashEvent {
        object SPLASHING: SplashEvent()
        object SPLASHED: SplashEvent()
        data class Error(val msg: String = "Error"): SplashEvent()
    }

    //ViewModel State-Events (i.e. Event Interface)
    sealed class SplashViewEvent {
        object LoadingStarted: SplashViewEvent()
        object LoadingFinished: SplashViewEvent()
        data class LoadingError(val message: String): SplashViewEvent()
        object TokenLoaded: SplashViewEvent()
        object LoginFinished : SplashViewEvent()
        object LoginFailed : SplashViewEvent()
    }
    //Enumerated Events (i.e. Event Implementation)
    companion object {
        //Loading Status (... Can We Use a Loading Delegate?)
        val LOADING_INIT = SplashViewEvent.LoadingStarted
        val LOADING_STOP = SplashViewEvent.LoadingFinished
        //Action Steps
        val TOKEN_LOADED = SplashViewEvent.TokenLoaded
        val LOGIN_SUCCESS = SplashViewEvent.LoginFinished
        val LOGIN_FAIL = SplashViewEvent.LoginFailed
        //Error Responses
        val ERROR_A = SplashViewEvent.LoadingError("Error A")
        val ERROR_0 = SplashViewEvent.LoadingError("Error 0")
        val ERROR_1 = SplashViewEvent.LoadingError("Error 1")
    }
}