package com.dmm.presentation.forecast.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmm.core.components.SombraSkeleton
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme

// Alturas das barras do esqueleto: a forma de um dia de sol (sobe até ao meio-dia e desce)
private val BarHeights = listOf(16, 28, 48, 72, 96, 112, 112, 88, 64, 40, 24, 16)

// O ecrã da Previsão enquanto carrega: as mesmas caixas, nos mesmos sítios, a brilhar
@Composable
fun ForecastSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 24.dp),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            SombraSkeleton(Modifier.size(width = 190.dp, height = 34.dp), CircleShape)  // "Planear o dia"
            Spacer(Modifier.weight(1f))
            SombraSkeleton(Modifier.size(width = 104.dp, height = 40.dp), CircleShape)  // "Hoje ▾"
        }
        Spacer(Modifier.height(20.dp))
        HourlyCardSkeleton()
        Spacer(Modifier.height(16.dp))
        SombraSkeleton(Modifier.fillMaxWidth().height(84.dp), RoundedCornerShape(24.dp))  // melhor hora
        Spacer(Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(28.dp))
                .padding(horizontal = 20.dp, vertical = 6.dp),
        ) {
            repeat(3) { index ->
                if (index > 0) HorizontalDivider(color = SombraColors.Line)
                DayRowSkeleton()
            }
        }
    }
}

@Composable
private fun HourlyCardSkeleton() {
    val shape = RoundedCornerShape(28.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp, shape = shape)
            .background(MaterialTheme.colorScheme.surface, shape)
            .padding(20.dp),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            SombraSkeleton(Modifier.size(width = 150.dp, height = 18.dp), CircleShape)  // título
            Spacer(Modifier.weight(1f))
            SombraSkeleton(Modifier.size(width = 64.dp, height = 14.dp), CircleShape)   // agora: 8
        }
        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth().height(120.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            BarHeights.forEach { height ->
                SombraSkeleton(Modifier.weight(1f).height(height.dp), RoundedCornerShape(6.dp))
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {                      // legenda
            repeat(4) { SombraSkeleton(Modifier.size(width = 60.dp, height = 12.dp), CircleShape) }
        }
    }
}

@Composable
private fun DayRowSkeleton() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SombraSkeleton(Modifier.width(96.dp).padding(end = 24.dp).height(18.dp), CircleShape)  // dia
        SombraSkeleton(Modifier.size(22.dp), CircleShape)                                      // ícone
        SombraSkeleton(Modifier.weight(1f).padding(horizontal = 20.dp).height(10.dp), CircleShape)
        SombraSkeleton(Modifier.size(width = 40.dp, height = 28.dp), CircleShape)              // UV
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF6F4EE)
@Composable
private fun ForecastSkeletonPreview() {
    SombraTheme { ForecastSkeleton() }
}
