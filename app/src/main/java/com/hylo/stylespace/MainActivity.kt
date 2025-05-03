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
import com.hylo.stylespace.ui.screens.MainScreen
import com.hylo.stylespace.ui.screens.SignInScreen
import com.hylo.stylespace.ui.theme.StyleSpaceTheme
import com.hylo.stylespace.viewmodel.SignInViewModel
import com.hylo.stylespace.viewmodel.UserViewModel
import kotlinx.coroutines.launch

/**
 * Main entry point of the application.
 *
 * This activity handles:
 * - Initial Google One Tap sign-in integration using [GoogleAuthUiClient]
 * - Loading user data into the [UserViewModel]
 * - Navigating between composable screens using Jetpack Navigation Compose
 * - Displaying the app UI using Jetpack Compose and theming with [StyleSpaceTheme]
 *
 * Navigation includes:
 * - "splash": Entry screen that checks authentication status
 * - "sign_in": Google sign-in screen
 * - "home": Main screen shown after successful login
 *
 * @constructor Initializes the main activity and sets up the navigation and authentication logic.
 * @author Eliorodr2104
 */
class MainActivity : ComponentActivity() {

    // Variable lazy for management Google authentication side client
    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    // View Model for management interaction with user object
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Get the user logged if exist
        val signedInUser = googleAuthUiClient.getSignedInUser()

        // Control if user logged
        if (signedInUser != null) {

            lifecycleScope.launch {
                userViewModel.loadUser(signedInUser) // Load user in user View Model

            }

        }

        setContent {

            StyleSpaceTheme(
                dynamicColor = false // Disable Dynamic color for custom theme app
            ) {

                Surface(
                    modifier = Modifier
                        .fillMaxSize(),

                    color = MaterialTheme.colorScheme.background,
                ) {

                    // Nav controller for management the flow principal screens
                    val navController = rememberNavController()

                    // Create the screens flow
                    NavHost(
                        navController    = navController,
                        startDestination = "splash"
                    ) {

                        // Splash screen to avoid flickering in loading data
                        composable("splash") {

                            // Get actually user logged
                            val isUserLoaded by userViewModel.isUserLoaded.collectAsStateWithLifecycle()

                            // Creating an async flow for loading data
                            LaunchedEffect(
                                key1 = isUserLoaded
                            ) {

                                if (signedInUser != null && isUserLoaded) {

                                    // Navigate to [HomeScreen], if user is logged
                                    navController.navigate("home") {

                                        // Clean the stack navigation
                                        popUpTo(0) {
                                            inclusive = true
                                        }

                                        launchSingleTop = true

                                    }

                                } else {

                                    // Navigate to [SignInScreen], if user not logged
                                    navController.navigate("sign_in") {

                                        // Clean the stack navigation
                                        popUpTo(0) {
                                            inclusive = true
                                        }

                                        launchSingleTop = true

                                    }
                                }
                            }
                        }

                        // Sign in screen to create a new user
                        composable("sign_in") {

                            // View Model fer management the sign in flow
                            val viewModel = viewModel<SignInViewModel>()

                            // Get the current sign in state flow
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            // Initialize the launcher for start the Google sign in
                            val launcher = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.StartIntentSenderForResult(),

                                onResult = { result ->

                                    if (result.resultCode == RESULT_OK) {

                                        lifecycleScope.launch {
                                            val signInResult = googleAuthUiClient.signInWithIntent(
                                                intent = result.data ?: return@launch
                                            )

                                            viewModel.onSignInResult(signInResult)
                                        }
                                    }
                                }
                            )

                            // Show the sign in screen
                            SignInScreen(
                                userViewModel = userViewModel,
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

                        // Home screen for management user information
                        composable("home") {

                            MainScreen(
                                userViewModel = userViewModel,

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