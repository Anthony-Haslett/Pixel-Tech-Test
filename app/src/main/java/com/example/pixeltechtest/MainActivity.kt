package com.example.pixeltechtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pixeltechtest.data.repository.UserRepository
import com.example.pixeltechtest.ui.compose.UsersScreen
import com.example.pixeltechtest.ui.theme.PixelTechTestTheme
import com.example.pixeltechtest.ui.viewmodel.UsersViewModel
import com.example.pixeltechtest.ui.viewmodel.UsersViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val userRepository = UserRepository(this)
        val viewModelFactory = UsersViewModelFactory(userRepository)

        setContent {
            PixelTechTestTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val viewModel: UsersViewModel = viewModel(factory = viewModelFactory)
                        UsersScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
