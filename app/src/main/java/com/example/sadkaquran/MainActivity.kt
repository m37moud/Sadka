package com.example.sadkaquran

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.compose.rememberNavController
import com.example.sadkaquran.navigation.NavGraph
import com.example.sadkaquran.ui.theme.SadkaQuranTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
//            val systemUiController = rememberSystemUiController()
//            val useDarkIcons = !isSystemInDarkTheme()
//            SideEffect {
//                systemUiController.setSystemBarsColor(color = Color.Transparent, darkIcons = false)
//            }
            SadkaQuranTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
//                    Greeting("Android")
                    val context = LocalContext.current

                    if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                        // Prompt the user to grant permission to show notifications
                        var intent: Intent? = null
                        intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        context.startActivity(intent)
                    }

                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen(){
    val navController = rememberNavController()
    NavGraph(navController = navController)
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SadkaQuranTheme {
        Greeting("Android")
    }
}