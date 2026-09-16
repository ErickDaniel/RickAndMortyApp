package com.erickjuarez.rickandmorty.ui.assistant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.erickjuarez.rickandmorty.R
import com.erickjuarez.rickandmorty.domain.model.AssistantPersona

private val AssistantTopBarBackground = Color(0xFF07141D)
private val AssistantSubtitleColor = Color(0xFF9EADB7)
private val AssistantDividerColor = Color(0xFF1D303B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistantTopBar(
    persona: AssistantPersona,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(AssistantTopBarBackground)
    ) {
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AssistantAvatar(
                        persona = persona,
                        contentDescription = stringResource(
                            R.string.assistant_avatar_description,
                            persona.displayName
                        ),
                        modifier = Modifier.size(42.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(12.dp)
                    )

                    Column {
                        Text(
                            text = persona.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )

                        Text(
                            text = stringResource(
                                R.string.assistant_status
                            ),
                            style = MaterialTheme.typography.labelMedium,
                            color = AssistantSubtitleColor
                        )
                    }
                }
            },
            navigationIcon = {
                IconButton(
                    onClick = onBackClick
                ) {
                    Icon(
                        imageVector =
                            Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(
                            R.string.navigate_back
                        ),
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = AssistantTopBarBackground,
                navigationIconContentColor = Color.White,
                titleContentColor = Color.White,
                actionIconContentColor = Color.White
            )
        )

        HorizontalDivider(
            color = AssistantDividerColor,
            thickness = 1.dp
        )
    }
}
