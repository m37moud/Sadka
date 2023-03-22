package com.example.sadkaquran.ui.feature.surah

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.sadkaquran.data.models.DataX
import com.example.sadkaquran.data.models.Surah
import com.example.sadkaquran.ui.component.ErrorUI
import com.example.sadkaquran.ui.component.LoadingScreen
import com.example.sadkaquran.util.Resource

@Composable
fun SurahScreen(
    navController: NavController,
    shrahNumber: Int = 1,
    elShikh: String = "",
    viewModel: SurahViewModel = hiltViewModel()
) {

    val surah = viewModel.surah.collectAsState().value
    viewModel.getSurah(shrahNumber)

//    val surah = produceState<Resource<DataX>>(initialValue = Resource.Loading) {
//        value = viewModel.getSurah(shrahNumber)
//    }.value

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
                Log.d("moshaf" , "${read.toString()}")
//                SurahRead(read)
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
//        for (aiah in surah.ayahs) {
//            Row() {
//
//                Text(text = aiah.text,
////                    modifier = Modifier.align(Alignment.Center)
//                )
//                Text(text = aiah.number.toString(),
////                    modifier = Modifier.align(Alignment.Center)
//                )
//            }
//        }

    }

}