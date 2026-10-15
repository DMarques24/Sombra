package com.dmm.presentation.profile.component

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmm.core.components.SombraSkeleton
import com.dmm.core.designsystem.SombraColors
import com.dmm.core.designsystem.SombraTheme

// O Perfil enquanto carrega: as mesmas caixas, nos mesmos sítios, a brilhar
@Composable
fun ProfileSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SombraColors.Surface)
            .statusBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            SombraSkeleton(Modifier.align(Alignment.End).size(40.dp), CircleShape)     // definições
            SombraSkeleton(Modifier.size(120.dp), CircleShape)                         // avatar
            Spacer(Modifier.height(12.dp))
            SombraSkeleton(Modifier.size(width = 150.dp, height = 30.dp), CircleShape) // nome
            Spacer(Modifier.height(8.dp))
            SombraSkeleton(Modifier.size(width = 200.dp, height = 16.dp), CircleShape) // pele · cidade
        }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {                    // nível e XP
            Row(Modifier.fillMaxWidth()) {
                SombraSkeleton(Modifier.size(width = 140.dp, height = 18.dp), CircleShape)
                Spacer(Modifier.weight(1f))
                SombraSkeleton(Modifier.size(width = 90.dp, height = 16.dp), CircleShape)
            }
            SombraSkeleton(Modifier.fillMaxWidth().height(14.dp), CircleShape)
            SombraSkeleton(Modifier.size(width = 220.dp, height = 14.dp), CircleShape)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {                     // estatísticas
            repeat(3) { SombraSkeleton(Modifier.weight(1f).height(96.dp), RoundedCornerShape(20.dp)) }
        }
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {                    // patches
            Row(Modifier.fillMaxWidth()) {
                SombraSkeleton(Modifier.size(width = 90.dp, height = 22.dp), CircleShape)
                Spacer(Modifier.weight(1f))
                SombraSkeleton(Modifier.size(width = 50.dp, height = 16.dp), CircleShape)
            }
            repeat(2) {
                Row(Modifier.fillMaxWidth()) {
                    repeat(4) {
                        Box(Modifier.weight(1f), contentAlignment = Alignment.TopCenter) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                SombraSkeleton(Modifier.size(76.dp), CircleShape)
                                SombraSkeleton(Modifier.size(width = 60.dp, height = 12.dp), CircleShape)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun ProfileSkeletonPreview() {
    SombraTheme { ProfileSkeleton() }
}
