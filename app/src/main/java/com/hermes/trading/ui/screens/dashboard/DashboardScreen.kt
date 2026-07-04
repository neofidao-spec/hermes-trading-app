package com.hermes.trading.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hermes.trading.api.PositionData
import com.hermes.trading.ui.components.PnlSummaryCard
import com.hermes.trading.ui.components.PositionItem
import com.hermes.trading.ui.theme.BrandGreen
import com.hermes.trading.ui.theme.BrandRed
import com.hermes.trading.ui.theme.DarkBackground
import com.hermes.trading.viewmodel.DashboardUiState
import com.hermes.trading.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val balance by viewModel.balance.collectAsStateWithLifecycle()
    val positions by viewModel.positions.collectAsStateWithLifecycle()
    val isEngineRunning by viewModel.isEngineRunning.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 80.dp)
    ) {
        // --- Top Bar ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hermes Auto-Trader",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (uiState is DashboardUiState.Success) BrandGreen else BrandRed)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (uiState is DashboardUiState.Success) "Connected" else "Disconnected",
                        color = if (uiState is DashboardUiState.Success) BrandGreen else BrandRed,
                        fontSize = 12.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // --- Summary Card ---
        item {
            PnlSummaryCard(
                dailyPnl = balance,
                winrate = 81.8,
                totalTrades = positions.size
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // --- Loading / Error feedback ---
        when (val state = uiState) {
            is DashboardUiState.Loading -> {
                item {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = BrandGreen
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            is DashboardUiState.Error -> {
                item {
                    Text(
                        text = "⚠️ ${state.message}",
                        color = BrandRed,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            else -> Unit
        }

        // --- Engine Control ---
        item {
            EngineControlCard(
                isRunning = isEngineRunning,
                onToggle = { viewModel.toggleEngine() }
            )
            Spacer(modifier = Modifier.height(32.dp))
        }

        // --- Active Positions Header ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Active Positions",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (positions.isNotEmpty()) {
                    TextButton(onClick = { viewModel.refresh() }) {
                        Text("Refresh", color = BrandGreen, fontSize = 12.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // --- Real Positions from Bitget API ---
        if (positions.isEmpty() && uiState is DashboardUiState.Success) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No active positions — engine idle",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            items(items = positions, key = { it.symbol + it.holdSide }) { pos ->
                PositionRow(pos = pos)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun PositionRow(pos: PositionData) {
    val side = pos.holdSide.uppercase() // "LONG" or "SHORT"
    val leverage = pos.leverage.toIntOrNull() ?: 1
    val entry = pos.averageOpenPrice.toDoubleOrNull() ?: 0.0
    val mark = pos.marketPrice.toDoubleOrNull() ?: 0.0
    val pnl = pos.unrealizedPL.toDoubleOrNull() ?: 0.0
    val size = pos.total.toDoubleOrNull() ?: 0.0
    val margin = pos.marginSize.toDoubleOrNull() ?: 0.0
    val roe = if (margin > 0) (pnl / margin) * 100.0 else 0.0

    PositionItem(
        symbol = pos.symbol,
        side = side,
        leverage = leverage,
        entryPrice = entry,
        markPrice = mark,
        unrealizedPnl = pnl,
        roe = roe
    )
}

@Composable
private fun EngineControlCard(
    isRunning: Boolean,
    onToggle: () -> Unit
) {
    val containerColor = if (isRunning) BrandRed.copy(alpha = 0.15f) else BrandGreen.copy(alpha = 0.15f)
    val contentColor = if (isRunning) BrandRed else BrandGreen
    val icon = if (isRunning) Icons.Default.Stop else Icons.Default.PlayArrow
    val text = if (isRunning) "STOP TRADING ENGINE" else "START TRADING ENGINE"
    val statusText = if (isRunning) "Engine v6t is running actively" else "Engine is currently paused"

    Button(
        onClick = onToggle,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = text,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = statusText,
                    color = contentColor.copy(alpha = 0.8f),
                    fontSize = 11.sp
                )
            }
        }
    }
}
