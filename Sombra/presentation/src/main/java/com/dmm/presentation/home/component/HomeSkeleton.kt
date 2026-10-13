package com.dmm.presentation.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.dmm.core.components.SombraSkeleton

private val SkyBase = Color.White.copy(alpha = 0.25f)
private val SkyHighlight = Color.White.copy(alpha = 0.35f)

@Composable
fun HomeSkySkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SkySkeleton(Modifier.size(width = 120.dp, height = 24.dp), CircleShape)  // cidade
                SkySkeleton(Modifier.size(width = 80.dp, height = 14.dp), CircleShape)   // data
            }
            SkySkeleton(Modifier.size(48.dp), CircleShape)                               // notificações
        }
        Box(Modifier.size(230.dp), contentAlignment = Alignment.Center) {
            SkySkeleton(Modifier.size(140.dp), CircleShape)                              // sol
        }
        Box(Modifier.offset(y = (-24).dp).height(96.dp), contentAlignment = Alignment.Center) {
            SkySkeleton(Modifier.size(width = 130.dp, height = 64.dp), RoundedCornerShape(16.dp)) // 27°
        }
        Box(Modifier.offset(y = (-16).dp).height(24.dp), contentAlignment = Alignment.Center) {
            SkySkeleton(Modifier.size(width = 220.dp, height = 14.dp), CircleShape)      // céu limpo · máx…
        }
        Spacer(Modifier.height(4.dp))
        SkySkeleton(Modifier.size(width = 200.dp, height = 44.dp), CircleShape)          // UV 8 · Muito alto…
    }
}

@Composable
fun HomeCardSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        SombraSkeleton(Modifier.fillMaxWidth().height(84.dp), RoundedCornerShape(20.dp))  // aviso
        Row(
            modifier = Modifier.padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SombraSkeleton(Modifier.size(56.dp), CircleShape)                            // anel
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SombraSkeleton(Modifier.size(width = 150.dp, height = 18.dp), CircleShape)
                SombraSkeleton(Modifier.size(width = 110.dp, height = 14.dp), CircleShape)
            }
        }
    }
}

@Composable
private fun SkySkeleton(modifier: Modifier, shape: Shape) {
    SombraSkeleton(modifier = modifier, shape = shape, color = SkyBase, highlight = SkyHighlight)
}
