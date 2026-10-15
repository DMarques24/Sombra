package com.dmm.presentation.timer.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dmm.core.components.SombraSkeleton

// O ecrã do Protetor enquanto carrega. Fica dentro do TimerLayout, como o conteúdo verdadeiro.
@Composable
fun TimerContentSkeleton() {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SombraSkeleton(Modifier.size(width = 160.dp, height = 34.dp), CircleShape)    // "Protetor"
        SombraSkeleton(Modifier.size(width = 240.dp, height = 16.dp), CircleShape)    // subtítulo
    }
    SombraSkeleton(Modifier.size(180.dp), CircleShape)                                // anel
    SombraSkeleton(Modifier.fillMaxWidth().height(84.dp), RoundedCornerShape(20.dp)) // aviso do UV
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SombraSkeleton(Modifier.size(width = 140.dp, height = 14.dp), CircleShape)    // "Fator que vais usar"
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            repeat(4) { SombraSkeleton(Modifier.weight(1f).height(52.dp), RoundedCornerShape(16.dp)) }
        }
    }
    SombraSkeleton(Modifier.fillMaxWidth().height(76.dp), RoundedCornerShape(20.dp)) // "Vou para a água"
}

@Composable
fun TimerButtonSkeleton() {
    SombraSkeleton(Modifier.fillMaxWidth().height(64.dp), CircleShape)
}
