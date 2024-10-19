package com.mucheng.qingyuan.wallpaper.ui.route

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mucheng.qingyuan.wallpaper.ui.route.main.MainScreen
import com.mucheng.qingyuan.wallpaper.ui.route.splash.SplashScreen

@Composable
fun Router() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = RouterDestination.SPLASH
    ) {
        composable(RouterDestination.SPLASH) {
            SplashScreen(
                popUpToMain = {
                    navController.navigate(RouterDestination.MAIN) {
                        popUpTo(RouterDestination.SPLASH) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(RouterDestination.MAIN) {
            MainScreen()
        }
    }
}

object RouterDestination {
    const val SPLASH = "/"
    const val MAIN = "/main"
}