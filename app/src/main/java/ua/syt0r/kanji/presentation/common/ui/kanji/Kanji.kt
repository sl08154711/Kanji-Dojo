package ua.syt0r.kanji.presentation.common.ui.kanji

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ua.syt0r.kanji.core.svg.SvgPathCreator
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji_dojo.shared.svg.SvgCommandParser
import kotlin.random.Random

const val KanjiSize = 109
const val StrokeWidth = 3f

@Composable
fun defaultStrokeColor(): Color {
    return MaterialTheme.colorScheme.onSurface
}

@Composable
fun Kanji(
    modifier: Modifier = Modifier,
    strokes: List<Path>,
    strokesToDraw: Int = strokes.size,
    strokeColor: Color = defaultStrokeColor(),
    stokeWidth: Float = StrokeWidth
) {

    Canvas(modifier) {

        val (width, height) = drawContext.size.run { width to height }

        scale(width / KanjiSize, height / KanjiSize, Offset.Zero) {

            strokes.take(strokesToDraw)
                .forEach {
                    drawPath(
                        path = it,
                        color = strokeColor,
                        style = Stroke(
                            width = stokeWidth,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }

        }

    }
}

@Composable
fun Stroke(
    path: Path,
    modifier: Modifier = Modifier,
    color: Color = defaultStrokeColor(),
    stokeWidth: Float = StrokeWidth
) {

    Canvas(modifier) {
        val (width, height) = drawContext.size.run { width to height }
        scale(width / KanjiSize, height / KanjiSize, Offset.Zero) {
            clipRect {
                drawPath(
                    path = path,
                    color = color,
                    alpha = color.alpha,
                    style = Stroke(
                        width = stokeWidth,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }
    }

}

@Composable
fun StrokeInput(
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    onUserPathDrawn: suspend (Path) -> Unit
) {

    val drawPathState = remember {
        mutableStateOf(Path(), neverEqualPolicy())
    }

    var areaSize = 0

    Stroke(
        path = drawPathState.value,
        modifier = modifier
            .onGloballyPositioned {
                areaSize = it.size.height
            }
            .pointerInput(1, 2) {
                detectDragGestures(
                    onDragStart = {
                        drawPathState.value = Path().apply {
                            moveTo(
                                it.x / areaSize * KanjiSize,
                                it.y / areaSize * KanjiSize
                            )
                        }
                    },
                    onDragEnd = {
                        coroutineScope.launch {
                            onUserPathDrawn(drawPathState.value)
                            drawPathState.value = Path()
                        }
                    },
                    onDrag = { change, dragAmount ->
                        drawPathState.value = drawPathState.value.apply {
                            relativeLineTo(
                                dragAmount.x / areaSize * KanjiSize,
                                dragAmount.y / areaSize * KanjiSize
                            )
                        }
                    }
                )
            }
    )

}

fun parseKanjiStrokes(strokes: List<String>): List<Path> {
    return strokes.map { SvgCommandParser.parse(it) }
        .map { SvgPathCreator.convert(it) }
}

/***
 * Sample kanji for use in previews
 */
object PreviewKanji {

    const val kanji = "書"
    val meanings = listOf("to write", "to compose", "to pen", "to draw", "to paint")
    val kun = listOf("か.く", "-が.き", "-がき")
    val on = listOf("ショ")

    val strokes: List<Path> = listOf(
        "M23.78,19c0.58,0.35,1.93,2.58,1.93,3.28c0,7.24,0.24,37.86,0.24,47.16",
        "M13.33,46.23c0.64,0.38,1.33,1.99,1.28,2.76c-0.36,5.3-0.36,13.37-1.63,21.34c-0.28,1.73,0.19,2.13,1.75,1.74c8.88-2.22,12.96-2.7,23.76-4.9",
        "M38.72,42.05c0.64,0.38,1.28,2.84,1.28,3.61c0,5.96-1.38,16.48-1.38,26.13",
        "M49.67,16.18c0.28,0.53,0.78,1.89,0.93,2.54c1.23,5.38,2.27,18.05,2.98,27.93",
        "M50.97,17.51c9.81-0.34,30.08-2.62,37.41-2.99c2.69-0.14,3.29,1.55,3.07,4.8c-0.36,5.28-1.65,14.9-3.26,25.19",
        "M52.83,30.92c3.2-0.19,34.5-2.6,37.14-2.55",
        "M53.73,44.61c10.03-1.09,24.8-2.33,34.56-2.27"
    )
        .map { SvgCommandParser.parse(it) }
        .map { SvgPathCreator.convert(it) }

    fun randomKanji() = Random.nextInt(0x4E00, 0x4FFF).toChar().toString()

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun KanjiPreview() {
    AppTheme {
        Column {
            Kanji(
                modifier = Modifier
                    .size(200.dp)
                    .background(MaterialTheme.colorScheme.background),
                strokes = PreviewKanji.strokes
            )
        }

    }
}
