package com.erickjuarez.rickandmorty.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import com.erickjuarez.rickandmorty.R
import com.erickjuarez.rickandmorty.domain.model.Character
import com.erickjuarez.rickandmorty.ui.theme.PortalGreen
import com.erickjuarez.rickandmorty.ui.theme.PortalTurquoise
import com.erickjuarez.rickandmorty.ui.theme.RickNavy
import com.erickjuarez.rickandmorty.ui.theme.RickSurface

private val IdentityCardShape = GenericShape { size, _ ->
    val cut = size.minDimension * 0.075f
    moveTo(cut, 0f)
    lineTo(size.width - cut * 1.8f, 0f)
    lineTo(size.width, cut * 1.8f)
    lineTo(size.width, size.height - cut)
    lineTo(size.width - cut, size.height)
    lineTo(cut * 0.7f, size.height)
    lineTo(0f, size.height - cut * 0.7f)
    lineTo(0f, cut)
    close()
}

@Composable
fun CharacterIdentityDialog(
    character: Character,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .widthIn(max = 380.dp)
                .shadow(
                    elevation = 30.dp,
                    shape = IdentityCardShape,
                    ambientColor = PortalTurquoise.copy(alpha = 0.45f),
                    spotColor = PortalGreen.copy(alpha = 0.55f)
                )
                .clip(IdentityCardShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            RickSurface,
                            RickNavy,
                            Color(0xFF0B2630)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            PortalTurquoise,
                            PortalGreen,
                            PortalTurquoise.copy(alpha = 0.35f)
                        )
                    ),
                    shape = IdentityCardShape
                )
                .drawBehind {
                    val step = 24.dp.toPx()
                    var x = 0f
                    while (x <= size.width) {
                        drawLine(
                            color = gridColor,
                            start = Offset(x, 0f),
                            end = Offset(x, size.height),
                            strokeWidth = 0.5.dp.toPx()
                        )
                        x += step
                    }
                    var y = 0f
                    while (y <= size.height) {
                        drawLine(
                            color = gridColor,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 0.5.dp.toPx()
                        )
                        y += step
                    }
                }
                .padding(20.dp)
        ) {
            Column {
                IdentityHeader(
                    characterId = character.id,
                    onDismiss = onDismiss
                )

                FuturisticPortrait(
                    character = character,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                        .padding(top = 12.dp)
                )

                Text(
                    text = character.name.uppercase(),
                    modifier = Modifier.padding(top = 18.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                PortalDivider(modifier = Modifier.padding(vertical = 12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IdentityField(
                        label = stringResource(R.string.character_identity_status),
                        value = character.status,
                        valueColor = statusColor(character.status),
                        modifier = Modifier.weight(0.8f)
                    )
                    IdentityField(
                        label = stringResource(R.string.character_identity_origin),
                        value = character.originName,
                        valueColor = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1.2f)
                    )
                }

                IdentityFooter(modifier = Modifier.padding(top = 18.dp))
            }
        }
    }
}

@Composable
private fun IdentityHeader(
    characterId: Int,
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.character_identity_title),
                color = PortalGreen,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            )
            Text(
                text = stringResource(R.string.character_identity_number, characterId),
                modifier = Modifier.padding(top = 2.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.5.sp
                )
            )
        }

        DecorativeBars()

        IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = stringResource(R.string.character_identity_close),
                tint = PortalTurquoise
            )
        }
    }
}

@Composable
private fun FuturisticPortrait(
    character: Character,
    modifier: Modifier = Modifier
) {
    val frameShape = RoundedCornerShape(
        topStart = 4.dp,
        topEnd = 28.dp,
        bottomEnd = 4.dp,
        bottomStart = 28.dp
    )

    Box(
        modifier = modifier
            .clip(frameShape)
            .background(RickNavy)
            .border(1.dp, PortalTurquoise.copy(alpha = 0.8f), frameShape)
    ) {
        AsyncImage(
            model = character.imageUrl,
            contentDescription = stringResource(
                R.string.character_image_description,
                character.name
            ),
            modifier = Modifier.fillMaxSize(),
            placeholder = ColorPainter(RickSurface),
            error = ColorPainter(MaterialTheme.colorScheme.errorContainer),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            PortalTurquoise.copy(alpha = 0.16f),
                            Color.Transparent,
                            RickNavy.copy(alpha = 0.58f)
                        )
                    )
                )
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val scanColor = PortalTurquoise.copy(alpha = 0.2f)
            val scanStep = 7.dp.toPx()
            var y = 0f
            while (y < size.height) {
                drawLine(
                    color = scanColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 0.5.dp.toPx()
                )
                y += scanStep
            }

            val bracketLength = 26.dp.toPx()
            val inset = 12.dp.toPx()
            val bracketPath = Path().apply {
                moveTo(inset, inset + bracketLength)
                lineTo(inset, inset)
                lineTo(inset + bracketLength, inset)

                moveTo(size.width - inset - bracketLength, inset)
                lineTo(size.width - inset, inset)
                lineTo(size.width - inset, inset + bracketLength)

                moveTo(inset, size.height - inset - bracketLength)
                lineTo(inset, size.height - inset)
                lineTo(inset + bracketLength, size.height - inset)

                moveTo(size.width - inset - bracketLength, size.height - inset)
                lineTo(size.width - inset, size.height - inset)
                lineTo(size.width - inset, size.height - inset - bracketLength)
            }
            drawPath(
                path = bracketPath,
                color = PortalGreen,
                style = Stroke(width = 2.dp.toPx())
            )

            drawLine(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        PortalGreen,
                        Color.White,
                        PortalGreen,
                        Color.Transparent
                    )
                ),
                start = Offset(0f, size.height * 0.52f),
                end = Offset(size.width, size.height * 0.52f),
                strokeWidth = 1.5.dp.toPx()
            )
        }
    }
}

@Composable
private fun IdentityField(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label.uppercase(),
            color = PortalTurquoise,
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.5.sp
            )
        )
        Text(
            text = value.uppercase(),
            modifier = Modifier.padding(top = 4.dp),
            color = valueColor,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun IdentityFooter(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Canvas(
            modifier = Modifier
                .width(88.dp)
                .height(22.dp)
        ) {
            var x = 0f
            var index = 0
            while (x < size.width) {
                val width = if (index % 3 == 0) 3.dp.toPx() else 1.dp.toPx()
                drawRect(
                    color = if (index % 2 == 0) PortalTurquoise else PortalGreen,
                    topLeft = Offset(x, 0f),
                    size = androidx.compose.ui.geometry.Size(width, size.height)
                )
                x += width + 3.dp.toPx()
                index++
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = stringResource(R.string.character_identity_verified),
                color = PortalGreen,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            )
            Text(
                text = stringResource(R.string.character_identity_database),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace
                )
            )
        }
    }
}

@Composable
private fun DecorativeBars() {
    Canvas(
        modifier = Modifier
            .width(54.dp)
            .height(18.dp)
    ) {
        repeat(5) { index ->
            val startX = index * 10.dp.toPx()
            drawLine(
                color = PortalTurquoise.copy(alpha = 0.8f),
                start = Offset(startX, size.height),
                end = Offset(startX + 10.dp.toPx(), 0f),
                strokeWidth = 2.dp.toPx()
            )
        }
    }
}

@Composable
private fun PortalDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        PortalTurquoise,
                        PortalGreen,
                        Color.Transparent
                    )
                )
            )
    )
}

@Composable
private fun statusColor(status: String): Color = when (status.lowercase()) {
    "alive" -> PortalGreen
    "dead" -> MaterialTheme.colorScheme.error
    else -> Color(0xFFFABD21)
}
