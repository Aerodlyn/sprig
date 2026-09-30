package io.aerodlyn.sprig.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.aerodlyn.sprig.data.model.CareType
import io.aerodlyn.sprig.ui.theme.DueContainer
import io.aerodlyn.sprig.ui.theme.DueText
import io.aerodlyn.sprig.ui.theme.OverdueContainer
import io.aerodlyn.sprig.ui.theme.OverdueText

@Composable
fun DuePlantCard(
    item: DuePlantItem,
    onWaterClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.plantName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                val careLabel = when (item.careType) {
                    CareType.WATERING -> "Water"
                }
                Text(
                    text = "${item.location} • $careLabel every ${item.intervalDays} days",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                val (badgeBg, badgeTextColor) = when (item.overdueLevel) {
                    OverdueLevel.OVERDUE -> OverdueContainer to OverdueText
                    OverdueLevel.DUE_TODAY -> DueContainer to DueText
                }

                Surface(
                    modifier = Modifier.clip(RoundedCornerShape(8.dp)),
                    color = badgeBg
                ) {
                    Text(
                        text = item.overdueText,
                        style = MaterialTheme.typography.labelMedium,
                        color = badgeTextColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Button(
                onClick = { onWaterClick(item.plantId) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(text = "Water")
            }
        }
    }
}
