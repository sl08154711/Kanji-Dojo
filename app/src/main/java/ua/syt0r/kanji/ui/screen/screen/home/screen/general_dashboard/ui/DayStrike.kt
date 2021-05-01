package ua.syt0r.kanji.ui.screen.screen.home.screen.general_dashboard.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.syt0r.kanji.ui.theme.KanjiDojoTheme
import ua.syt0r.kanji.ui.theme.secondary

@Composable
fun DayStrike(
    days: Int
) {

    val daysText = days.toString()

    val stylizedText = AnnotatedString.Builder("$daysText days strike").apply {
        addStyle(SpanStyle(color = secondary), 0, daysText.length)
    }

    Column(
        modifier = Modifier.padding(horizontal = 22.dp)
    ) {

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stylizedText.toAnnotatedString(),
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(12.dp))

    }

}


@Preview(showSystemUi = true)
@Composable
fun DayStrikePreview() {

    KanjiDojoTheme {
        DayStrike(
            days = 5
        )
    }

}