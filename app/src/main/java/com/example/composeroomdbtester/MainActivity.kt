package com.example.composeroomdbtester

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val database = getDatabase(this)
        val userViewModel = UserViewModel(database)
        setContent {
            UserListScreen(userViewModel)
        }
    }
}

@Composable
fun UserListScreen(userViewModel: UserViewModel = viewModel()) {
    var name = remember { mutableStateOf("") }
    var scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            OutlinedTextField(
                value = name.value,
                onValueChange = { name.value = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    scope.launch {
                        if (name.value.isNotEmpty() && !userViewModel.checkNameExists(name.value)) {
                            userViewModel.addUser(name.value)
                            name.value = ""
                        } else {
                            Toast.makeText(
                                context,
                                "Name already exists or input is empty",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }

                }, modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add User")
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = {
                    if (name.value.isNotEmpty()) {
                        userViewModel.deleteAllUsers()
                        name.value = ""
                    }
                }, modifier = Modifier.fillMaxWidth()
            ) {
                Text("Delete All")
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(Color(0xFFfdd3ff))
                .height(400.dp)
                .padding(10.dp)
                .verticalScroll(scrollState)
        ) {
            Column {
                userViewModel.users.forEach {
                    Column(
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp, horizontal = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = it.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            IconButton(onClick = {
                                userViewModel.deleteUser(it)
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_delete_24),
                                    contentDescription = "Delete"
                                )
                            }
                        }
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}
