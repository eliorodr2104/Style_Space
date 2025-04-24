package com.hylo.stylespace.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import com.hylo.stylespace.R
import com.hylo.stylespace.model.NavigationItem
import com.hylo.stylespace.model.TypeServices
import com.hylo.stylespace.model.UserRole
import com.hylo.stylespace.model.enums.Screen
import com.hylo.stylespace.viewmodel.AppointmentViewModel
import com.hylo.stylespace.viewmodel.ServicesViewModel
import com.hylo.stylespace.viewmodel.UserViewModel
import com.hylo.stylespace.viewmodel.factory.AppointmentViewModelFactory
import com.hylo.stylespace.viewmodel.factory.ServicesViewModelFactory


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    userViewModel: UserViewModel,
    onSignOut: () -> Unit
) {

    var selectedNavigationIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var typeServiceSelected by remember {
        mutableStateOf<TypeServices?>(null)
    }

    val user by userViewModel.user.collectAsState()

    val appointmentViewModel: AppointmentViewModel = viewModel(
        factory = AppointmentViewModelFactory(
            establishmentId = user?.establishmentUsed?.firstOrNull().toString(),
            userId = user?.id ?: ""
        )
    )

    val servicesViewModel: ServicesViewModel = viewModel(
        factory = ServicesViewModelFactory(user?.establishmentUsed?.firstOrNull().toString())
    )

    val navigationItems by remember {
        mutableStateOf(
            listOf(
                NavigationItem(
                    title = "Home",
                    icon = Icons.Outlined.Home,
                    selectedIcon = Icons.Filled.Home,
                    route = Screen.Home.route
                ),
                NavigationItem(
                    title = "Date",
                    icon = Icons.Outlined.DateRange,
                    selectedIcon = Icons.Filled.DateRange,
                    route = Screen.Appointment.route
                ),
                NavigationItem(
                    title = "Profile",
                    icon = Icons.Outlined.Person,
                    selectedIcon = Icons.Filled.Person,
                    route = Screen.Profile.route
                ),
                NavigationItem(
                    title = "Settings",
                    icon = Icons.Outlined.Settings,
                    selectedIcon = Icons.Filled.Settings,
                    route = Screen.Setting.route
                )
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {

                    AnimatedVisibility (
                        visible = currentRoute == Screen.CreateAppointment.route,

                        enter = slideInHorizontally(
                            initialOffsetX = { fullWidth -> -fullWidth },
                            animationSpec = tween(durationMillis = 300)

                        ) + fadeIn(),


                        exit = fadeOut()

                    ) {

                        IconButton(
                            onClick = {
                                navController.popBackStack()
                            }

                        ) {
                            Icon(
                                painter = rememberVectorPainter(Icons.AutoMirrored.Filled.ArrowBack),
                                contentDescription = ""
                            )
                        }
                    }
                },

                title = {
                    Text(
                        text = when(currentRoute) {
                            Screen.Home.route -> {
                                stringResource(R.string.title_home)
                            }

                            Screen.Appointment.route -> {
                                stringResource(R.string.title_appointment)
                            }

                            Screen.Profile.route -> {
                                stringResource(R.string.title_profile)
                            }

                            Screen.Setting.route -> {
                                stringResource(R.string.title_settings)
                            }

                            Screen.CreateAppointment.route -> {
                                "Crea Appuntamento"
                            }

                            else -> {
                                "NULL"
                            }
                        },
                        fontWeight = FontWeight.Bold
                    )
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },

        bottomBar = {
            BottomAppBar {
                NavigationBar {
                    navigationItems.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selectedNavigationIndex == index,
                            onClick = {
                                selectedNavigationIndex = index
                                navController.navigate(item.route)
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selectedNavigationIndex == index) item.selectedIcon else item.icon,
                                    contentDescription = item.title
                                )
                            },
                            label = {
                                Text(
                                    item.title,
                                    color = if (index == selectedNavigationIndex)
                                        MaterialTheme.colorScheme.onSecondaryContainer

                                    else
                                        MaterialTheme.colorScheme.outline
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.surface,
                                indicatorColor = MaterialTheme.colorScheme.primary
                            )

                        )
                    }
                }
            }

        }

    ) { innerPadding ->

        val graph = navController.createGraph(
                startDestination = Screen.Home.route
            ) {
                composable(route = Screen.Appointment.route) {
                    Column {  }
                    //CartScreen()
                }

                composable(route = Screen.Setting.route) {
                    Column {  }
                    //SettingScreen()
                }

                composable(route = Screen.Home.route) {
                    if (user?.type == UserRole.Manager.role) {
                        DashboardScreen()

                    } else {
                        HomeScreen(
                            mainScreenNavController = navController,
                            userViewModel = userViewModel,
                            servicesViewModel = servicesViewModel,
                            appointmentViewModel = appointmentViewModel,

                            changeServicesSelected = { typeServiceSelected = it }
                        )

                    }

                }

                composable(route = Screen.CreateAppointment.route) {
                    typeServiceSelected?.let { it ->
                        CreateAppointmentScreen(
                            mainScreenNavController = navController,
                            servicesViewModel = servicesViewModel,
                            typeServiceSelected = it
                        )
                    }

                }

                composable(
                    route = Screen.Profile.route
                ) {
                    ProfileScreen(
                        user = user,
                        onSignOut = onSignOut
                    )
                }
            }

        NavHost(
            navController = navController,
            graph = graph,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
