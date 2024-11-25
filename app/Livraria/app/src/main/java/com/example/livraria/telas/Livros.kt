package com.example.livraria.telas


import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.livraria.Livro
import com.example.livraria.R
import com.example.livraria.ui.theme.Azul
import com.example.livraria.ui.theme.LightGray
import com.google.firebase.firestore.FirebaseFirestore
@Composable
fun Livros(navController: NavController, userId: String, context: Context) {
    val livros = remember { mutableStateOf<List<Livro>>(emptyList()) }
    val firestore = FirebaseFirestore.getInstance()

    LaunchedEffect(Unit) {
        firestore.collection("livros")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("Livros", "Erro ao buscar livros", exception)
                    return@addSnapshotListener
                }
                val list = snapshot?.map { document ->
                    Livro(
                        id = document.id,
                        autor = document.getString("autor") ?: "",
                        titulo = document.getString("titulo") ?: "",
                        ano = document.getString("ano") ?: ""
                    )
                }.orEmpty()
                livros.value = list
            }
    }

    Column(modifier = Modifier.background(Color.White).fillMaxSize()) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Spacer(Modifier.height(30.dp))
            Button(
                onClick = {
                    val sharedPreferences =
                        context.getSharedPreferences("usuario", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.remove("id")
                    editor.apply()
                    navController.navigate("login")
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Azul,
                    contentColor = Color.White
                ),
                modifier = Modifier.width(150.dp)
            ) {
                Text(text = "Sair")
            }

            // Cabeçalho da tabela
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp, top = 50.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Titulo", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text(text = "Autor", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text(text = "Ano", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            }

            // Corpo da tabela (Lista de livros)
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(livros.value) { index, livro ->
                    val backgroundColor = if (index % 2 == 0) LightGray else Color.Transparent
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(backgroundColor),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = livro.titulo,
                            modifier = Modifier.weight(1f).padding(10.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = livro.autor,
                            modifier = Modifier.weight(1f).padding(10.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = livro.ano,
                            modifier = Modifier.weight(1f).padding(10.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Imagem do rodapé
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.rodape),
                    contentDescription = "Imagem de rodapé",
                    modifier = Modifier
                        .height(150.dp)
                        .fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }

}
