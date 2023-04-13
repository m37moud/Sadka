package com.example.sadkaquran.ui.feature.surah

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.sadkaquran.R
import com.example.sadkaquran.data.models.AyahX
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
    LaunchedEffect(key1 = true) {

        viewModel.getSurah(shrahNumber)
    }
    val surah = viewModel.surah.collectAsState().value


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
                Log.d("moshaf", "${read.toString()}")
                SurahRead(read)
            }

            is Resource.Error -> {
                ErrorUI(state.error)
            }
        }


    }

}

@Composable
fun SurahRead(surah: DataX) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.TopCenter)) {
            Image(
                painter = painterResource(id = R.drawable.suraborder),
                contentDescription = "",
                modifier = Modifier.size(width = 150.dp, height = 50.dp)
            )
            Text(text = surah.name)
        }
        LazyColumn(content = {
            items(surah.ayahs) { ayah ->
                ItemAyah(modifier = Modifier.animateContentSize(), ayah = ayah)
            }
        })
//        for (aiah in surah.ayahs) {
//            Row() {
//
//                Text(
//                    text = aiah.text,
////                    modifier = Modifier.align(Alignment.Center)
//                )
//                Text(
//                    text = aiah.number.toString(),
////                    modifier = Modifier.align(Alignment.Center)
//                )
//            }
//        }

    }

}

@Composable
fun ItemAyah(modifier: Modifier = Modifier, ayah: AyahX) {
    Row(modifier = modifier) {
        Text(
            text = ayah.text,
//                    modifier = Modifier.align(Alignment.Center)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = ayah.number.minus(1).toString(),
//                    modifier = Modifier.align(Alignment.Center)
        )
    }
}