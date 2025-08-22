package com.example.pixeltechtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pixeltechtest.data.repository.UserRepository
import com.example.pixeltechtest.ui.navigation.MainNavigationScreen
import com.example.pixeltechtest.ui.theme.PixelTechTestTheme
import com.example.pixeltechtest.ui.viewmodel.UsersViewModel

class MainActivity : ComponentActivity() {

    private val dataStore by preferencesDataStore("user_preferences")

    private val usersViewModel: UsersViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return UsersViewModel(UserRepository(dataStore)) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PixelTechTestTheme {
                MainNavigationScreen(viewModel = usersViewModel)
            }
        }
    }
}
