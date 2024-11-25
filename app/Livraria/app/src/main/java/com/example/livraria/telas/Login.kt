import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.example.livraria.telas.traduzirMensagemErroFirebase
import com.example.livraria.ui.theme.Azul
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlin.system.exitProcess

@Composable
fun Login(navController: NavController) {
    val auth = Firebase.auth
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    val context = LocalContext.current

    val sharedPreferences = context.getSharedPreferences("usuario", Context.MODE_PRIVATE)
    val id = sharedPreferences.getString("id", null) // Retorna o userId ou null se não encontrado

    if(id != null){
        navController.navigate("livros/$id")
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Login", fontSize = 40.sp)
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

                auth.signInWithEmailAndPassword(email, senha)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val usuario_id = task.result?.user?.uid ?: ""

                            // Salva o ID do usuário na sessão
                            val sharedPreferences = context.getSharedPreferences("usuario", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("id", usuario_id)  // Salva o userId no SharedPreferences
                            editor.apply()

                            navController.navigate("livros/$usuario_id") // Redireciona para ListScreen
                        } else {
                            val mensagemErro = traduzirMensagemErroFirebase(task.exception?.message)

                            Log.e("Login", "Erro ao fazer login", task.exception)
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
            Text("Login")
        }

        TextButton(onClick = { navController.navigate("cadastrar") }) {
            Text("Criar conta")
        }
    }
}
