package com.example.sadkaquran.ui.feature.moshaf

import android.media.MediaPlayer
import android.net.Uri
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.sadkaquran.R
import com.example.sadkaquran.data.models.Data
import com.example.sadkaquran.data.models.Quran
import com.example.sadkaquran.data.models.Reader
import com.example.sadkaquran.exoplayer.isPlaying
import com.example.sadkaquran.navigation.Screens
import com.example.sadkaquran.ui.component.ErrorUI
import com.example.sadkaquran.ui.component.LoadingScreen
import com.example.sadkaquran.ui.component.ShikhMenuDropDown
import com.example.sadkaquran.util.Resource
import com.google.accompanist.coil.rememberCoilPainter

@Composable
fun MoshafScreen(
    navController: NavController,
    viewModel: MoshafViewModel = hiltViewModel()
) {
    val elMoshaf = viewModel.moshafList.collectAsState().value
    val shikhList = viewModel.shiks.collectAsState().value
    val playbackStateCompat by viewModel.playbackState.observeAsState()

    var shikhIdentifier by remember {
        mutableStateOf("ar.abdullahbasfar")
    }
    var shikhName by remember {
        mutableStateOf("ar.abdullahbasfar")
    }

    var uri by remember {
        mutableStateOf("https://cdn.islamic.network/quran/audio-surah/128/ar.alafasy/1.mp3")
    }
    val context = LocalContext.current
//    val mp = MediaPlayer.create(context, Uri.parse(uri))
//    val mp = MediaPlayer()
//
//    mp.setDataSource(context, Uri.parse(uri))

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
                        shikhIdentifier = shikh!!.identifier
                        shikhName = shikh!!.name
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
                playbackStateCompat?.let {
                    SurahLazyList(list,
                        it,
                        onPlayClick = { surah ->
                            val reader = Reader(surah.number, shikhIdentifier)
                            uri = reader.getUri()

                            val quran = Quran(
                                surah.number.toString(), title = surah.name,
                                subtitle = shikhName, url = uri
                            )
                            Log.d("Moshaf", "$reader")
                            Log.d("Moshaf", "uri = $uri")

                            viewModel.playOrToggleSong(quran, true)
                            //                    mp.start()


                        }) {
                        navController.navigate("surah" + "/${it.number}/${shikhName}")
                    }
                }
            }

            is Resource.Error -> {
                ErrorUI(state.error)
            }
        }


    }

}


@Composable
fun SurahLazyList(
    data: List<Data>,
    playbackStateCompat: PlaybackStateCompat,
    onPlayClick: (Data) -> Unit,
    onClick: (Data) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize(), content = {
        items(data) { surah ->
            SurahItem(
                modifier = Modifier
                    .animateContentSize()
                    .clickable { onClick(surah) },
                surah,
                playbackStateCompat,
                onPlayClick = { onPlayClick(surah) }
            )
        }
    }, contentPadding = PaddingValues(8.dp))
}


@Composable
fun SurahItem(
    modifier: Modifier = Modifier,
    surah: Data,
    playbackStateCompat: PlaybackStateCompat,
    onPlayClick: (Data) -> Unit
) {
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
            if (playbackStateCompat?.isPlaying == false) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_round_play_arrow),
                    contentDescription = "listen"
                )
            } else {
                Icon(
                    painter = painterResource(id =  R.drawable.ic_round_pause),
                    contentDescription = "listen"
                )
            }

        }
//        val painter = rememberCoilPainter(
//            request =
//            if (playbackStateCompat?.isPlaying == false) {
//                R.drawable.ic_round_play_arrow
//            } else {
//                R.drawable.ic_round_pause
//            }
//        )
//
//        Image(
//            painter = painter,
//            contentDescription = "Quran",
//            contentScale = ContentScale.Crop,
//
//            modifier = Modifier
//                .padding(end = 16.dp)
//                .size(48.dp)
//                .clickable(
//                    interactionSource = remember {
//                        MutableInteractionSource()
//                    },
//                    indication = rememberRipple(
//                        bounded = false,
//                        radius = 24.dp
//                    )
//                ) { onPlayClick(surah) },
//        )


    }
}