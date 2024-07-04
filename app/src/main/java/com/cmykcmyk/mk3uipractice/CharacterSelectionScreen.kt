package com.cmykcmyk.mk3uipractice

import android.content.ContentResolver
import android.media.SoundPool
import android.net.Uri
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.cmykcmyk.mk3uipractice.ui.theme.MK3UIPracticeTheme
import kotlinx.coroutines.delay

@Composable
private fun CharacterCard(
    character: CharacterData.CharacterId,
    highlighted: Boolean = false,
    selected: Boolean = false,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .width(IntrinsicSize.Max)
            .height(IntrinsicSize.Max),
        color = Color.Transparent,
        onClick = onClick
    ) {
        Box(modifier = Modifier
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(Color(32, 32, 32), Color(140, 140, 140))
                ),
                shape = RectangleShape
            )
        ) {
            Image(
                painter = painterResource(id = CharacterData.getAvatar(character)),
                contentDescription = null,
                modifier = Modifier.size(64.dp, 75.dp)
            )
        }

        if (selected) {
            var target: Int by remember { mutableIntStateOf(0) }
            val light by animateIntAsState(
                targetValue = target,
                animationSpec = tween(100 * target, 0, LinearEasing),
                label = ""
            )

            LaunchedEffect(Unit) {
                target = 7
            }

            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .background(
                        if ((light and 1) == 0) Color(255, 255, 255, 128) else Color(
                            0,
                            0,
                            0,
                            128
                        )
                    )
            ) {}
        } else if (highlighted) {
            val anim = rememberInfiniteTransition(label = "")
            val colorAlternation by anim.animateValue(
                initialValue = 1,
                targetValue = 0,
                typeConverter = Int.VectorConverter,
                animationSpec = infiniteRepeatable(animation = tween(300, delayMillis = 200), RepeatMode.Restart),
                label = "")

            Box(modifier = Modifier
                .border(
                    width = 3.dp,
                    color = if (colorAlternation == 1) Color(32, 180, 32) else Color(0, 100, 0),
                    shape = RectangleShape
                )
            ) {}
        }
    }
}

@Preview
@Composable
fun CharacterCardPreview() {
    CharacterCard(character = CharacterData.CharacterId.SubZero)
}

@Composable
private fun CharacterGrid(
    onCursorChanged: (pos: Int) -> Unit = {},
    onSelected: () -> Unit = {}
) {
    var cursorPosition by remember { mutableIntStateOf(0) }
    var selected by remember { mutableStateOf(false) }

    val onCardClick: (Int) -> Unit = onCardClick@ {
        if (selected)
            return@onCardClick

        if (cursorPosition == it) {
            selected = true
            onSelected()
        }
        else {
            cursorPosition = it
            onCursorChanged(it)
        }
    }

    val ranges = listOf(0..4, 5..9, 10..14)
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        for (r in ranges) {
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                for (i in r) {
                    CharacterCard(
                        character = CharacterData.CharacterId.entries[i],
                        highlighted = i == cursorPosition,
                        selected = selected && i == cursorPosition
                    ) {
                        onCardClick(i)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun CharacterGridPreview() {
    CharacterGrid()
}

@Preview
@Composable
fun CharacterSkinPreview() {
    CharacterData.getSkin(CharacterData.CharacterId.Sonya, CharacterData.AnimId.FightingStance)
}

@Composable
private fun _CharacterSelectionScreen(
    onCharacterSelected: (selection: CharacterData.CharacterId) -> Unit,
    playCursorChange: () -> Unit = {},
    playCharacterSelected: () -> Unit = {},
    playNameSound: (selection: CharacterData.CharacterId) -> Unit = {}
) {
    var curChar: CharacterData.CharacterId by remember { mutableStateOf(CharacterData.CharacterId.ShangTsung) }
    var selected: Boolean by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = selected) {
        if (!selected)
            return@LaunchedEffect

        delay(3000)
        onCharacterSelected(curChar)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(107, 107, 107)
    ) {
        Column(
            modifier = Modifier.padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp, alignment = Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = LocalContext.current.getString(R.string.selection_title).uppercase(),
                color = Color.White
            )

            Box(
                modifier = Modifier.heightIn(max = 229.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                CharacterGrid(
                    onCursorChanged = {
                        curChar = CharacterData.CharacterId.entries[it]
                        playCursorChange()
                    },
                    onSelected = {
                        if (selected)
                            return@CharacterGrid

                        selected = true
                        playCharacterSelected()
                        playNameSound(curChar)
                    }
                )

                Box(
                    modifier = Modifier.offset((-190).dp)
                ) {
                    CharacterData.getSkin(curChar, if (selected) CharacterData.AnimId.VictoryPose else CharacterData.AnimId.FightingStance)
                }
            }

            Text(
                text = LocalContext.current.getString(R.string.selection_note).uppercase(),
                fontSize = 10.sp,
                color = Color.White
            )
        }
    }
}

@Preview(device = "spec:width=1280px,height=800px,orientation=landscape")
@Composable
fun CharacterSelectionScreenPreview() {
    MK3UIPracticeTheme {
        var character: CharacterData.CharacterId by remember { mutableStateOf(CharacterData.CharacterId.ShangTsung) }
        var selected: Boolean by remember { mutableStateOf(false) }

        _CharacterSelectionScreen(
            onCharacterSelected = { character = it; selected = true }
        )

        if (selected) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "${stringResource(CharacterData.getName(character))} is selected",
                    fontSize = 24.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .background(Color.Black),
                    color = Color.White
                )
            }
        }
    }
}

@Preview(device = "spec:width=1600px,height=900px,orientation=landscape")
@Composable
fun CharacterSelectionScreenPreview1600() {
    CharacterSelectionScreenPreview()
}

@Preview(device = "spec:width=1920px,height=1080px,orientation=landscape")
@Composable
fun CharacterSelectionScreenPreview1920() {
    CharacterSelectionScreenPreview()
}



@Composable
fun CharacterSelectionScreen(
    onCharacterSelected: (selection: CharacterData.CharacterId) -> Unit
) {
    val context = LocalContext.current
    val music = remember { ExoPlayer.Builder(context).build().also { player ->
            val uri = Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE).path(R.raw.music_selection.toString()).build()
            player.setMediaItem(MediaItem.fromUri(uri))
            player.repeatMode = Player.REPEAT_MODE_ONE
            player.prepare()
            player.play()
        }
    }

    // don't know... if add Builder().setContext(context) - app crash
    val cursorSound = remember { SoundPool.Builder().build().also { pool ->
            pool.load(context, R.raw.cursor_changed, 1)
            pool.load(context, R.raw.character_selected, 1)
        }
    }

    val nameSound = remember { SoundPool.Builder().build().also { pool ->
            pool.load(context, R.raw.shangtsung, 1)
            pool.load(context, R.raw.sindel, 1)
            pool.load(context, R.raw.jax, 1)
            pool.load(context, R.raw.kano, 1)
            pool.load(context, R.raw.liukang, 1)
            pool.load(context, R.raw.sonya, 1)
            pool.load(context, R.raw.stryker, 1)
            pool.load(context, R.raw.smoke, 1)
            pool.load(context, R.raw.subzero, 1)
            pool.load(context, R.raw.cyrax, 1)
            pool.load(context, R.raw.sektor, 1)
            pool.load(context, R.raw.nightwolf, 1)
            pool.load(context, R.raw.sheeva, 1)
            pool.load(context, R.raw.kunglao, 1)
            pool.load(context, R.raw.kabal, 1)
        }
    }

    _CharacterSelectionScreen(
        onCharacterSelected = { selection ->
            music.stop()
            onCharacterSelected(selection)
        },
        playCursorChange = {
            cursorSound.play(1, 1f, 1f,1, 0, 1f)
        },
        playCharacterSelected = {
            cursorSound.play(2, 1f, 1f, 1, 0, 1f)
        },
        playNameSound = { selection ->
            nameSound.play(selection.ordinal + 1, 1f, 1f, 1, 0, 1f)
        }
    )
}
