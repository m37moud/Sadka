package com.example.sadkaquran.ui.feature.moshaf

import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.widget.Space
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.sadkaquran.data.models.Data
import com.example.sadkaquran.data.models.Reader
import com.example.sadkaquran.navigation.Screens
import com.example.sadkaquran.ui.component.ErrorUI
import com.example.sadkaquran.ui.component.LoadingScreen
import com.example.sadkaquran.ui.component.ShikhMenuDropDown
import com.example.sadkaquran.util.Resource

@Composable
fun MoshafScreen(
    navController: NavController,
    viewModel: MoshafViewModel = hiltViewModel()
) {
    val elMoshaf = viewModel.moshafList.collectAsState().value
    val shikhList = viewModel.shiks.collectAsState().value
    var shikhPlayer by remember {
        mutableStateOf("ar.abdullahbasfar")
    }

    var uri by remember {
        mutableStateOf("https://cdn.islamic.network/quran/audio-surah/128/ar.alafasy/1.mp3")
    }
   val context = LocalContext.current
    val mp = MediaPlayer.create(context, Uri.parse(uri))



    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        when (val state = shikhList) {
            is Resource.Success -> {
                ShikhMenuDropDown(
                    modifier = Modifier.fillMaxWidth(),
                    name = "Shikh",
                    shiks = state.data,
                    menuItemSelected = { shikh ->
                        shikhPlayer = shikh!!.identifier
                    })
            }
        }
        Spacer(modifier = Modifier.height(8.dp))



        when (val state = elMoshaf) {
            is Resource.Loading -> {
                LoadingScreen()
            }
            is Resource.Success -> {

                val list = state.data
//                Log.d("moshaf" , "${list.size}")
                SurahLazyList(list, onPlayClick = { surah ->
                    val reader = Reader(surah.number, shikhPlayer)
                    Log.d("Moshaf" , "$reader")
                    uri = reader.getUri()
                    Log.d("Moshaf" , "uri = $uri")


                    mp.start()


                }) {
                    navController.navigate(Screens.Surah.route)
                }
            }

            is Resource.Error -> {
                ErrorUI(state.error)
            }
        }


    }

}


@Composable
fun SurahLazyList(data: List<Data>, onPlayClick: (Data) -> Unit, onClick: (Data) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize(), content = {
        items(data) { surah ->
            SurahItem(
                modifier = Modifier
                    .animateContentSize()
                    .clickable { onClick(surah) },
                surah,
                onPlayClick = { onPlayClick(surah) }
            )
        }
    }, contentPadding = PaddingValues(8.dp))
}


@Composable
fun SurahItem(modifier: Modifier = Modifier, surah: Data, onPlayClick: (Data) -> Unit) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = surah.number.toString())

        Text(text = surah.name)
        Button(
            onClick = { onPlayClick(surah) },
            shape = CircleShape,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
        ) {
            Icon(imageVector = Icons.Outlined.PlayArrow, contentDescription = "listen")
        }

    }
}