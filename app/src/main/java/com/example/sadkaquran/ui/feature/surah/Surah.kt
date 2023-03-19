package com.example.sadkaquran.ui.feature.surah

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.sadkaquran.data.models.Data
import com.example.sadkaquran.data.models.DataX
import com.example.sadkaquran.data.models.Reader
import com.example.sadkaquran.navigation.Screens
import com.example.sadkaquran.ui.component.ErrorUI
import com.example.sadkaquran.ui.component.LoadingScreen
import com.example.sadkaquran.ui.feature.moshaf.SurahLazyList
import com.example.sadkaquran.util.Resource

@Composable
fun SurahScreen(
    navController: NavController,
    shrahNumber: Int,
    elShikh: String,
    viewModel: SurahViewModel = hiltViewModel()
) {
    viewModel.getSurah(shrahNumber)
    val surah = viewModel.surah.collectAsState().value
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (val state = surah) {
            is Resource.Loading -> {
                LoadingScreen()
            }
            is Resource.Success -> {

                val read = state.data
//                Log.d("moshaf" , "${list.size}")
                SurahRead(read)
            }

            is Resource.Error -> {
                ErrorUI(state.error)
            }
        }


    }

}
@Composable
fun SurahRead(surah : DataX){
    Box(modifier = Modifier.fillMaxSize() , contentAlignment = Alignment.Center) {
        Text(text = surah.name , modifier = Modifier.align(Alignment.TopCenter))
        for (aiah in surah.ayahs) {
            Text(text = aiah, modifier = Modifier.align(Alignment.Center))
        }

    }

}