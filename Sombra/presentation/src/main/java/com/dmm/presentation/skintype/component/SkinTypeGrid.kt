package com.dmm.presentation.skintype.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmm.core.designsystem.SombraTheme
import com.dmm.presentation.R
import com.dmm.presentation.skintype.SkinType

// Os 6 tipos em 2 colunas; os dois cartões de cada linha ficam com a mesma altura
@Composable
fun SkinTypeGrid(
    selected: SkinType?,
    onSelect: (SkinType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().selectableGroup(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SkinType.entries.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                row.forEach { type ->
                    SkinTypeCard(
                        title = stringResource(R.string.skin_type_name, type.roman),
                        description = stringResource(type.description),
                        swatch = type.color,
                        selected = type == selected,
                        onClick = { onSelect(type) },
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SkinTypeGridPreview() {
    SombraTheme {
        SkinTypeGrid(selected = SkinType.II, onSelect = {}, modifier = Modifier.padding(16.dp))
    }
}
