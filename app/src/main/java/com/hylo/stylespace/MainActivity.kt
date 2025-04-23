package com.hylo.stylespace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.identity.Identity
import com.hylo.stylespace.googleAuth.GoogleAuthUiClient
import com.hylo.stylespace.model.Establishment
import com.hylo.stylespace.ui.screens.MainScreen
import com.hylo.stylespace.ui.screens.SignInScreen
import com.hylo.stylespace.ui.theme.StyleSpaceTheme
import com.hylo.stylespace.viewmodel.AppointmentViewModel
import com.hylo.stylespace.viewmodel.ServicesViewModel
import com.hylo.stylespace.viewmodel.SignInViewModel
import com.hylo.stylespace.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import java.util.UUID

class MainActivity : ComponentActivity() {

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val signedInUser = googleAuthUiClient.getSignedInUser()

        if (signedInUser != null) {
            lifecycleScope.launch {
                userViewModel.loadUser(signedInUser)
            }
        }

        setContent {
            StyleSpaceTheme(
                dynamicColor = false
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val navController = rememberNavController()

                    val servicesViewModel = viewModel<ServicesViewModel>()
                    val appointmentViewModel = viewModel<AppointmentViewModel>()

                    NavHost(navController = navController, startDestination = "splash") {
                        composable("splash") {
                            val isUserLoaded by userViewModel.isUserLoaded.collectAsStateWithLifecycle()

                            LaunchedEffect(key1 = isUserLoaded) {
                                if (signedInUser != null) {
                                    if (isUserLoaded) {
                                        navController.navigate("home") {
                                            popUpTo(0) { inclusive = true }
                                            launchSingleTop = true
                                        }
                                    }

                                } else {
                                    navController.navigate("sign_in") {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        }

                        composable("sign_in") {
                            val viewModel = viewModel<SignInViewModel>()

                            val establishmentLocalInformation = Establishment(
                                id = UUID.randomUUID().toString(),
                                name = getString(R.string.app_name),
                                address = "Via Madonna, 87",
                            )

                            val state by viewModel.state.collectAsStateWithLifecycle()

                            val launcher = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.StartIntentSenderForResult(),
                                onResult = { result ->
                                    if(result.resultCode == RESULT_OK) {
                                        lifecycleScope.launch {
                                            val signInResult = googleAuthUiClient.signInWithIntent(
                                                intent = result.data ?: return@launch
                                            )
                                            viewModel.onSignInResult(signInResult)
                                        }
                                    }
                                }
                            )

                            SignInScreen(
                                userViewModel = userViewModel,
                                establishmentLocalInformation = establishmentLocalInformation,
                                googleAuthUiClient = googleAuthUiClient,
                                navController = navController,
                                state = state,
                                onSignInClick = {
                                    lifecycleScope.launch {
                                        val signInIntentSender = googleAuthUiClient.signIn()
                                        launcher.launch(
                                            IntentSenderRequest.Builder(
                                                signInIntentSender ?: return@launch
                                            ).build()
                                        )
                                    }
                                }
                            )
                        }
                        composable("home") {

                            MainScreen(
                                userViewModel = userViewModel,
                                servicesViewModel = servicesViewModel,
                                appointmentViewModel = appointmentViewModel,
                                onSignOut = {
                                    lifecycleScope.launch {
                                        googleAuthUiClient.signOut()

                                        navController.navigate("sign_in") {
                                            popUpTo(0) { inclusive = true }
                                            launchSingleTop = true
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
