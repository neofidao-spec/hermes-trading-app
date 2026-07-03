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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hermes.trading.ui.theme.BrandGreen
import com.hermes.trading.ui.theme.BrandRed
import com.hermes.trading.ui.theme.DarkSurfaceVariant
import com.hermes.trading.ui.theme.DarkOnSurfaceVariant

@Composable
fun PositionItem(
    symbol: String,
    side: String, // "LONG" or "SHORT"
    leverage: Int,
    entryPrice: Double,
    markPrice: Double,
    unrealizedPnl: Double,
    roe: Double // Return on Equity percentage
) {
    val isLong = side.uppercase() == "LONG"
    val sideColor = if (isLong) BrandGreen else BrandRed
    
    val isProfit = unrealizedPnl >= 0
    val pnlColor = if (isProfit) BrandGreen else BrandRed
    val pnlPrefix = if (isProfit) "+" else ""

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurfaceVariant)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side (Symbol & Side)
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = symbol,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(sideColor.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${side.uppercase()} ${leverage}x",
                        color = sideColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Entry: ${String.format("%.4f", entryPrice)}",
                color = DarkOnSurfaceVariant,
                fontSize = 12.sp
            )
        }

        // Right side (PnL)
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "$pnlPrefix$${String.format("%.2f", unrealizedPnl)}",
                color = pnlColor,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "$pnlPrefix${String.format("%.2f", roe)}%",
                color = pnlColor,
                fontSize = 12.sp
            )
        }
    }
}