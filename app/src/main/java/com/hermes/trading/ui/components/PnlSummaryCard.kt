package com.hermes.trading.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hermes.trading.ui.theme.BrandGreen
import com.hermes.trading.ui.theme.BrandRed
import com.hermes.trading.ui.theme.DarkSurfaceVariant
import com.hermes.trading.ui.theme.DarkOnSurfaceVariant

@Composable
fun PnlSummaryCard(
    dailyPnl: Double,
    winrate: Double,
    totalTrades: Int
) {
    val isProfit = dailyPnl >= 0
    val pnlColor = if (isProfit) BrandGreen else BrandRed
    val pnlPrefix = if (isProfit) "+" else ""

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurfaceVariant)
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = "Today's PnL",
                color = DarkOnSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "$pnlPrefix$${String.format("%.2f", dailyPnl)}",
                color = pnlColor,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(label = "Winrate", value = "${String.format("%.1f", winrate)}%")
                StatItem(label = "Trades", value = "$totalTrades")
                StatItem(label = "Engine", value = "v6t", valueColor = BrandGreen)
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = label,
            color = DarkOnSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = value,
            color = valueColor,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}