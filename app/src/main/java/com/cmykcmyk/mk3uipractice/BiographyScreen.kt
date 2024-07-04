package com.cmykcmyk.mk3uipractice

import android.content.ContentResolver
import android.media.SoundPool
import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.cmykcmyk.mk3uipractice.ui.theme.MK3UIPracticeTheme
import kotlin.math.min

@Composable
private fun Frame(content: @Composable () -> Unit) {
    val tile = ImageBitmap.imageResource(R.drawable.tile_brush)
    val background = remember(tile) { ShaderBrush(ImageShader(tile, TileMode.Repeated, TileMode.Repeated)) }
    val padding = 24.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawWithContent {
                val contentSize = this.size
                clipPath(
                    path = Path().apply {
                        addRect(
                            Rect(
                                padding.toPx(),
                                padding.toPx(),
                                contentSize.width * 0.4f,
                                contentSize.height - padding.toPx()
                            )
                        )
                    },
                    clipOp = ClipOp.Difference
                ) {
                    drawRect(background)
                }

                drawContent()
            }
    ) {
        Row(
            modifier = Modifier
                .padding(padding)
                .drawWithContent { drawContent() },
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.weight(0.4f))

            Box(modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight()) {
                content()
            }
        }
    }
}

@Composable
private fun _BiographyScreen(
    character: CharacterData.CharacterId,
    startFadeInAlpha: Float = 0f,
    onExit: () -> Unit = {},
    playBackPressed: () -> Unit = {}
) {
    var fadeInAnimTarget: Float by remember { mutableFloatStateOf(startFadeInAlpha) }
    val fadeInAnimCurValue: Float by animateFloatAsState(targetValue = fadeInAnimTarget, tween(1000, 3000))

    var fadeOutAnimTarget: Float by remember { mutableFloatStateOf(1f) }
    val fadeOutAnimCurValue: Float by animateFloatAsState(
        targetValue = fadeOutAnimTarget,
        animationSpec = keyframes { durationMillis = 3000; 0f at 1000 },
        finishedListener = {
            onExit()
        }
    )

    LaunchedEffect(Unit) {
        fadeInAnimTarget = 1f
    }

    val curAlpha = min(fadeInAnimCurValue, fadeOutAnimCurValue)

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.biography),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(modifier = Modifier.alpha(curAlpha)) {
            Frame {
                val scroll = rememberScrollState()

                Column(
                    modifier = Modifier
                        .verticalScroll(scroll, true)
                        .padding(end = 4.dp)
                        .drawWithContent {
                            drawContent()

                            if (scroll.maxValue > 0) {
                                val scrollHeight = size.height - 2 * scroll.maxValue
                                val rect = Rect(
                                    Offset(
                                        size.width - 4.dp.toPx(),
                                        2 * scroll.value.toFloat()
                                    ), Size(4.dp.toPx(), scrollHeight)
                                )
                                drawRoundRect(
                                    topLeft = rect.topLeft,
                                    size = rect.size,
                                    cornerRadius = CornerRadius(4.dp.toPx()),
                                    color = Color(
                                        255,
                                        255,
                                        255,
                                        if (scroll.isScrollInProgress) 255 else 127
                                    )
                                )
                            }
                        },
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 14.sp,
                        text = "- ${stringResource(CharacterData.getName(character))} -".uppercase(),
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 11.sp,
                        text = stringResource(id = CharacterData.getStory(character)).uppercase(),
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                }
            }

            Row {
                Image(
                    painter = painterResource(id = CharacterData.getArt(character)),
                    contentDescription = null,
                    modifier = Modifier
                        .weight(0.4f)
                        .offset(24.dp, (-24).dp)
                        .fillMaxHeight(),
                    alignment = Alignment.BottomCenter
                )

                Spacer(modifier = Modifier.weight(0.6f))
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset((-8).dp, 8.dp),
                color = Color.Transparent,
                onClick = {
                    fadeOutAnimTarget = 0f
                    playBackPressed()
                }
            ) {
                Text(text = stringResource(R.string.back).uppercase(), color = Color.White)
            }
        }
    }
}

@Preview(device = "spec:width=1280px,height=800px,orientation=landscape")
@Composable
fun BiographyScreenPreview() {
    MK3UIPracticeTheme {
        _BiographyScreen(CharacterData.CharacterId.Sonya, 1f)
    }
}

@Preview(device = "spec:width=1600px,height=900px,orientation=landscape")
@Composable
fun BiographyScreenPreview1600() {
    BiographyScreenPreview()
}

@Preview(device = "spec:width=1920px,height=1080px,orientation=landscape")
@Composable
fun BiographyScreenPreview1920() {
    BiographyScreenPreview()
}

@Composable
fun BiographyScreen(
    character: CharacterData.CharacterId,
    startFadeInAlpha: Float = 0f,
    onExit: () -> Unit = {}
) {
    val context = LocalContext.current
    val music = remember { ExoPlayer.Builder(context).build().also { player ->
        val uri = Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE).path(R.raw.music_biography.toString()).build()
        player.setMediaItem(MediaItem.fromUri(uri))
        player.repeatMode = Player.REPEAT_MODE_ONE
        player.prepare()
        player.play()
    } }

    val backSound = remember { SoundPool.Builder().build().also { pool ->
        pool.load(context, R.raw.back, 1)
    } }

    _BiographyScreen(
        character = character,
        startFadeInAlpha = startFadeInAlpha,
        onExit = {
            music.stop()
            onExit()
        },
        playBackPressed = {
            backSound.play(1, 1f, 1f, 1, 0, 1f)
        }
    )
}
