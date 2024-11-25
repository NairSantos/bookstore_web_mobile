package com.example.livraria

import Login
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.livraria.telas.CriarConta
import com.example.livraria.telas.Livros


@Composable
fun Navigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = "login") {
        composable("login") { Login(navController) }
        composable("cadastrar") { CriarConta(navController) }
        composable("livros/{usuario_id}") { backStackEntry ->
            val usuario_id = backStackEntry.arguments?.getString("usuario_id")
            Livros(navController = navController, userId = usuario_id ?: "",  context = context)
        }
    }
}