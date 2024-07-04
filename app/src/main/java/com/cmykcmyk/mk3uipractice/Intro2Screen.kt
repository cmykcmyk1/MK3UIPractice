import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmykcmyk.mk3uipractice.R
import com.cmykcmyk.mk3uipractice.ui.theme.MK3UIPracticeTheme

@Composable
fun Intro2Screen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.app_name),
            fontFamily = FontFamily(Font(R.font.mk3)),
            color = Color.White
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            fontFamily = FontFamily(Font(R.font.freesansboldoblique)),
            fontSize = 11.sp,
            text = stringResource(id = R.string.app_description).uppercase(),
            textAlign = TextAlign.Center,
            color = Color.White
        )
    }
}

@Preview(device = "spec:width=1280px,height=800px,orientation=landscape")
@Composable
fun Intro2ScreenPreview() {
    MK3UIPracticeTheme {
        Intro2Screen()
    }
}
