import android.content.ContentResolver
import android.media.SoundPool
import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.cmykcmyk.mk3uipractice.R
import com.cmykcmyk.mk3uipractice.ui.theme.MK3UIPracticeTheme
import kotlinx.coroutines.delay

private enum class StageId { Text, Logo, Closing }

@Composable
private fun _StartScreen(
    onExit: () -> Unit = {},
    playBell: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black,
        onClick = onExit
    ) {
        var stage by remember { mutableStateOf(StageId.Text) }

        if (stage == StageId.Text) {
            var animAlphaTarget by remember { mutableFloatStateOf(0.001f) }
            val animAlpha by animateFloatAsState(
                targetValue = animAlphaTarget,
                animationSpec = keyframes {
                    delayMillis = 1000; durationMillis = 9000; 0f at 0; 1f at 1000; 1f at 6000; 0f at 7000; 0f at 9000;
                },
                label = "",
                finishedListener = { stage = StageId.Logo }
            )

            LaunchedEffect(Unit) {
                animAlphaTarget = 0f
            }

            Row(
                modifier = Modifier.alpha(animAlpha),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.intro_text).uppercase(),
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            }
        } else if (stage == StageId.Logo) {
            var animAlphaTarget by remember { mutableFloatStateOf(0.001f) }
            val animAlpha by animateFloatAsState(
                targetValue = animAlphaTarget,
                animationSpec = keyframes {
                    durationMillis = 12000; 0f at 0; 1f at 500; 1f at 11500; 0f at 12000;
                },
                label = "",
                finishedListener = { stage = StageId.Closing; onExit() }
            )

            LaunchedEffect(Unit) {
                animAlphaTarget = 0f
                playBell()
                delay(4000)
                playBell()
                delay(4000)
                playBell()
            }

            Row(
                modifier = Modifier.alpha(animAlpha),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(painter = painterResource(R.drawable.title), contentDescription = "")
            }
        }
    }
}

@Preview(device = "spec:width=1280px,height=800px,orientation=landscape")
@Composable
fun StartScreenPreview() {
    MK3UIPracticeTheme {
        _StartScreen()
    }
}

@Composable
fun StartScreen(
    onExit: () -> Unit = {}
) {
    val context = LocalContext.current
    val music = remember { ExoPlayer.Builder(context).build().also { player ->
            val uri = Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE).path(R.raw.music_biography.toString()).build()
            player.setMediaItem(MediaItem.fromUri(uri))
            player.repeatMode = Player.REPEAT_MODE_ONE
            player.prepare()
            player.play()
        }
    }

    val bell = remember { SoundPool.Builder().build().also { pool ->
            pool.load(context, R.raw.bell, 1)
        }
    }

    _StartScreen(
        onExit = {
            music.stop()
            onExit()
        },
        playBell = { bell.play(1, 1f, 1f, 1, 0, 1f) }
    )
}
