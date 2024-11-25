package com.example.livraria.telas

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.livraria.ui.theme.Azul
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun CriarConta(navController: NavController) {
    val auth = Firebase.auth
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Cadastro", fontSize = 40.sp)
        Spacer(modifier = Modifier.height(50.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Email:") }
        )

        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = senha,
            onValueChange = { senha = it },
            placeholder = { Text("Senha:") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        )

        Spacer(modifier = Modifier.height(50.dp))

        Button(
            onClick = {
                if(email == "" || senha == ""){
                    Toast.makeText(
                        context,
                        "Informe seu email e senha",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@Button
                }

                auth.createUserWithEmailAndPassword(email, senha)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                context,
                                "Conta criada",
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.navigate("login") {
                                popUpTo("login") {
                                    inclusive = true
                                }
                            }
                        } else {
                            val mensagemErro = traduzirMensagemErroFirebase(task.exception?.message)

                            Log.e("CriarConta", "Erro ao criar conta", task.exception)
                            Toast.makeText(
                                context,
                                mensagemErro,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Azul,
                contentColor = Color.White
            ),
            modifier = Modifier.width(150.dp)
        ){
            Text(text = "Criar conta")
        }

        TextButton(onClick = { navController.navigate("login") }) {
            Text("Fazer login")
        }
    }
}

fun traduzirMensagemErroFirebase(mensagemOriginal: String?): String {
    return when {
        mensagemOriginal?.contains("The email address is badly formatted") == true ->
            "O endereço de email está em um formato inválido."

        mensagemOriginal?.contains("The email address is already in use") == true ->
            "Esse email já está em uso."

        mensagemOriginal?.contains("Password should be at least") == true ->
            "A senha precisa ter no minimo 8 caracteres."

        mensagemOriginal?.contains("There is no user record") == true ->
            "Usuário não encontrado. Verifique os dados e tente novamente."

        mensagemOriginal?.contains("The password is invalid") == true ->
            "Senha incorreta. Tente novamente."

        mensagemOriginal?.contains("The supplied auth credential is incorrect, malformed or has expired") == true ->
            "Email e/ou senha incorretos."

        else -> mensagemOriginal ?: "Erro desconhecido ao criar conta."
    }
}