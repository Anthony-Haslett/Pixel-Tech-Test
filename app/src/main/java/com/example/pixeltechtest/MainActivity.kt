package com.example.pixeltechtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.datastore.preferences.preferencesDataStore
import com.example.pixeltechtest.data.repository.UserRepository
import com.example.pixeltechtest.ui.navigation.MainNavigationScreen
import com.example.pixeltechtest.ui.theme.PixelTechTestTheme
import com.example.pixeltechtest.ui.viewmodel.UsersViewModel

class MainActivity : ComponentActivity() {

    private val dataStore by preferencesDataStore("user_preferences")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Create repository and view model
        val userRepository = UserRepository(dataStore)
        val usersViewModel = UsersViewModel(userRepository)

        setContent {
            PixelTechTestTheme {
                MainNavigationScreen(viewModel = usersViewModel)
            }
        }
    }
}
