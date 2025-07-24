package io.qzz.studyhard.markdown.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.qzz.studyhard.markdown.model.ExportFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorToolbar(
    onImportClick: () -> Unit,
    onExportClick: (ExportFormat) -> Unit,
    onSaveClick: () -> Unit
) {
    TopAppBar(
        title = { 
            Text(
                text = stringResource(android.R.string.untitled),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
            titleContentColor = MaterialTheme.colorScheme.primary,
            actionIconContentColor = MaterialTheme.colorScheme.primary
        ),
        actions = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                // 导入按钮
                FilledTonalButton(
                    onClick = onImportClick,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FileOpen,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("打开")
                }

                // 导出按钮
                var showExportMenu by remember { mutableStateOf(false) }
                Box {
                    FilledTonalButton(
                        onClick = { showExportMenu = true },
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.SaveAs,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("导出")
                    }
                    DropdownMenu(
                        expanded = showExportMenu,
                        onDismissRequest = { showExportMenu = false }
                    ) {
                        ExportFormat.values().forEach { format ->
                            DropdownMenuItem(
                                text = { Text(format.name) },
                                onClick = {
                                    onExportClick(format)
                                    showExportMenu = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = when(format) {
                                            ExportFormat.MARKDOWN -> Icons.Outlined.Article
                                            ExportFormat.HTML -> Icons.Outlined.Code
                                            ExportFormat.PDF -> Icons.Outlined.PictureAsPdf
                                            ExportFormat.TEXT -> Icons.Outlined.TextSnippet
                                        },
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            )
                        }
                    }
                }

                // 保存按钮
                Button(
                    onClick = onSaveClick,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Save,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("保存")
                }
            }
        }
    )
}
