import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toOffset
import androidx.compose.ui.unit.toSize
import com.cmykcmyk.mk3uipractice.R
import com.cmykcmyk.mk3uipractice.ui.theme.MK3UIPracticeTheme

@Composable
fun Intro1Screen() {
    Row(
        modifier = Modifier/*.fillMaxSize()*/,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        val textMeasurer = rememberTextMeasurer()

        var gradPosTarget by remember { mutableFloatStateOf(0f) }
        val gradPos by animateFloatAsState(
            targetValue = gradPosTarget,
            animationSpec = repeatable(
                iterations = 2,
                animation = tween(2000, 1000, LinearEasing)
            )
        )

        LaunchedEffect(Unit) {
            gradPosTarget = -1f
        }

        val textStyle = TextStyle(
            fontSize = 32.sp,
            fontFamily = FontFamily(Font(R.font.segalogofontrusbylyajka))
        )
        val requiredSize = textMeasurer.measure("CMYK", textStyle).size
        Canvas(modifier = Modifier
            .alpha(0.99f)  // HACK: https://stackoverflow.com/questions/73313025/jetpack-compose-canvas-blendmode-not-working-as-expected
            .fillMaxSize()) {
            var pos = (this.size.center - requiredSize.center.toOffset()) / 1f
            val cyan = Color(0, 200, 224)
            val blue = Color(0, 0, 192)

            for (c in "CMYK") {
                val layout = textMeasurer.measure(c.toString(), textStyle)
                drawText(layout, Color.Black, pos)

                val rect = Rect(offset = pos, size = layout.size.toSize())
                drawRect(
                    brush = Brush.linearGradient(
                        gradPos + 0.25f to cyan,
                        gradPos + 0.75f to blue,
                        gradPos + 1f to blue,
                        gradPos + 1.25f to cyan,
                        gradPos + 1.75f to blue,
                        gradPos + 2f to blue,
                        start = rect.topLeft,
                        end = rect.bottomRight
                    ),
                    topLeft = pos,
                    size = layout.size.toSize(),
                    blendMode = BlendMode.SrcIn
                )

                pos += Offset(layout.size.width.toFloat(), 0f)
            }
        }
    }
}

@Preview(device = "spec:width=1280px,height=800px,orientation=landscape")
@Composable
fun Intro1ScreenPreview() {
    MK3UIPracticeTheme {
        Intro1Screen()
    }
}
