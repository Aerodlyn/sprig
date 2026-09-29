package io.aerodlyn.sprig

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import io.aerodlyn.sprig.data.db.DatabaseInitializer
import io.aerodlyn.sprig.data.db.SprigDatabase
import io.aerodlyn.sprig.domain.CareRepositoryImpl
import io.aerodlyn.sprig.ui.home.HomeScreen
import io.aerodlyn.sprig.ui.home.HomeViewModel
import io.aerodlyn.sprig.ui.theme.SprigTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = SprigDatabase.getDatabase(applicationContext)
        val careRepository = CareRepositoryImpl(database)

        lifecycleScope.launch {
            DatabaseInitializer.seedIfEmpty(database)
        }

        val viewModel: HomeViewModel by viewModels {
            HomeViewModel.Factory(careRepository)
        }

        setContent {
            SprigTheme {
                HomeScreen(viewModel = viewModel)
            }
        }
    }
}
