package com.example.sadkaquran

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.sadkaquran.navigation.NavGraph
import com.example.sadkaquran.ui.feature.moshaf.MoshafScreen
import com.example.sadkaquran.ui.theme.SadkaQuranTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
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