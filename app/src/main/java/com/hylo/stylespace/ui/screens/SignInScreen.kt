package com.hylo.stylespace.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hylo.stylespace.googleAuth.GoogleAuthUiClient
import com.hylo.stylespace.R
import com.hylo.stylespace.model.Establishment
import com.hylo.stylespace.model.SignIn.SignInState
import com.hylo.stylespace.model.User
import com.hylo.stylespace.viewmodel.UserViewModel

@Composable
fun SignInScreen(
    userViewModel: UserViewModel,
    establishmentLocalInformation: Establishment,
    googleAuthUiClient: GoogleAuthUiClient,
    navController: NavController,
    state: SignInState,
    onSignInClick: () -> Unit
) {
    var isSignInProcess by remember {
        mutableStateOf(false)
    }

    val navControllerSignIn = rememberNavController()
    val context = LocalContext.current

    val isUserLoaded by userViewModel.isUserLoaded.collectAsState()
    val userUsage by userViewModel.user.collectAsState()

    LaunchedEffect(key1 = state.signInError) {
        state.signInError?.let { error ->
            Toast.makeText(
                context,
                error,
                Toast.LENGTH_LONG
            ).show()
        }

        isSignInProcess = false
    }

    LaunchedEffect(state.isSignInSuccessful, isUserLoaded, userUsage) {
        if (state.isSignInSuccessful) {
            if (!isUserLoaded && userUsage == null) {
                navControllerSignIn.navigate("complete_register")

            } else {
                navController.navigate("home")

            }

            isSignInProcess = false
        }
    }


    Scaffold(
        modifier = Modifier.fillMaxSize()

    ) { innerPadding ->

        NavHost(
            navController = navControllerSignIn,
            startDestination = "sign_in_google"
        ) {

            composable(
                route = "sign_in_google"
            ) {
                GoogleFirstLogin(
                    innerPadding = innerPadding,
                    userViewModel = userViewModel,
                    googleAuthUiClient = googleAuthUiClient,
                    onSignInClick = onSignInClick,
                    signInStatus = isSignInProcess,
                    changeSignInStatus = {
                        isSignInProcess = it
                    }
                )
            }


            composable(
                route = "complete_register"
            ) {
                FormRegister(
                    userViewModel = userViewModel,
                    establishmentLocalInformation = establishmentLocalInformation,
                    innerPadding = innerPadding,
                    user = googleAuthUiClient.getSignedInUser()
                )

            }
        }
    }

}

@Composable
private fun FormRegister(
    userViewModel: UserViewModel,
    establishmentLocalInformation: Establishment,
    innerPadding: PaddingValues,
    user: User?
) {
    var nameText by remember { mutableStateOf(user?.name ?: "") }
    var lastNameText by remember { mutableStateOf("") }
    var phoneText by remember { mutableStateOf(user?.phone ?: "") }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(
                top = 25.dp,
                start = 20.dp,
                end = 20.dp,
                bottom = 15.dp
            )
            .imePadding(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(R.string.welcome_register),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier
                .padding(3.dp)
        )

        Text(
            text = stringResource(R.string.text_welcome_register),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 20.dp
                ),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(30.dp)
                ) {
                    CustomTextField(
                        name = stringResource(R.string.name_textfield),
                        text = nameText,
                        labelText = stringResource(R.string.text_name_textfield),
                        onValueChange = { nameText = it }
                    )

                    CustomTextField(
                        name = stringResource(R.string.lastname_textfield),
                        text = lastNameText,
                        labelText = stringResource(R.string.text_lastname_textfield),
                        onValueChange = { lastNameText = it }
                    )

                    CustomTextField(
                        name = "Telefono",
                        text = phoneText,
                        labelText = "+39 123 456 7890",
                        onValueChange = { phoneText = it }
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
            )

            Button(
                onClick = {
                    userViewModel.loginUser(
                        user = User(
                            id = user?.id ?: "",
                            name = nameText,
                            email = user?.email ?: "",
                            phone = phoneText,
                            type = "client",
                            establishmentUsed = listOf(
                                establishmentLocalInformation.id
                            )
                        ),

                        establishment = establishmentLocalInformation
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(),
                shape = ShapeDefaults.Medium
            ) {
                Text(
                    text = stringResource(R.string.button_complete_registration),
                    modifier = Modifier
                        .padding(5.dp)
                )
            }
        }
    }
}

@Composable
private fun CustomTextField(
    name: String,
    text: String,
    labelText: String,
    onValueChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(
            space = 5.dp
        )

    ) {

        Text(
            text = name,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.titleMedium
        )

        OutlinedTextField(
            value = text,
            onValueChange = { onValueChange(it) },
            placeholder = {
                Text(
                    text = labelText
                )
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth(),
            shape = ShapeDefaults.Medium,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor   = MaterialTheme.colorScheme.primary,
                errorIndicatorColor     = Color.Transparent,
                disabledIndicatorColor  = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            )
        )
    }
}

@Composable
private fun GoogleFirstLogin(
    innerPadding: PaddingValues,
    userViewModel: UserViewModel,
    googleAuthUiClient: GoogleAuthUiClient,
    onSignInClick: () -> Unit,
    signInStatus: Boolean,
    changeSignInStatus: (Boolean) -> Unit
) {

    Box(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
    ) {

        Column {
            Image(
                painter = painterResource(R.drawable.hylo_logo),
                contentDescription = "Logo",
                modifier = Modifier
                    //.align(Alignment.TopStart)
                    .size(150.dp)
            )

            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxHeight()
                    .clip(
                        shape = RoundedCornerShape(
                            topEnd = 50.dp
                        )
                    )
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer
                    )
                    .padding(
                        all = 25.dp
                    )
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.welcome_app),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineLarge
                    )

                    Text(
                        text = stringResource(R.string.text_welcome_app),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                Button(
                    onClick = {
                        onSignInClick()

                        changeSignInStatus(true)

                        googleAuthUiClient.getSignedInUser()?.let { userViewModel.loadUser(it) }


                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = ShapeDefaults.Medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    border = BorderStroke(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.logo_google),
                            contentDescription = "Google Logo",
                            modifier = Modifier
                                .align(Alignment.CenterStart),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )

                        Text(
                            text = if (signInStatus) {
                                stringResource(R.string.login_process_text)

                            } else {
                                stringResource(R.string.login_google)
                            },

                            color = if (signInStatus) {
                                MaterialTheme.colorScheme.outline
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    }
                }

                Text(
                    text = stringResource(R.string.policy_text),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}