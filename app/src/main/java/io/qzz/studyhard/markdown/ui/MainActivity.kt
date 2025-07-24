package io.qzz.studyhard.markdown.ui

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import io.qzz.studyhard.markdown.ui.components.*
import io.qzz.studyhard.markdown.viewmodel.MarkdownEditorViewModel
import io.qzz.studyhard.markdown.model.ExportFormat
import io.qzz.studyhard.markdown.utils.FileUtils

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MarkdownEditorScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkdownEditorScreen(
    viewModel: MarkdownEditorViewModel = viewModel()
) {
    val context = LocalContext.current
    val document by viewModel.document.collectAsState()
    val previewHtml by viewModel.previewHtml.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    // 导入文件启动器
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { viewModel.importDocument(it) }
    }
    
    // 导出文件启动器
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            val format = remember { mutableStateOf(ExportFormat.MARKDOWN) }
            viewModel.exportDocument(selectedUri, format.value)
        }
    }
    
    // 错误提示
    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }
    
    Scaffold(
        topBar = {
            EditorToolbar(
                onImportClick = { 
                    importLauncher.launch(arrayOf("text/*", "text/markdown", "text/html"))
                },
                onExportClick = { format -> 
                    val extension = when(format) {
                        ExportFormat.MARKDOWN -> "md"
                        ExportFormat.HTML -> "html"
                        ExportFormat.PDF -> "pdf"
                        ExportFormat.TEXT -> "txt"
                    }
                    exportLauncher.launch(FileUtils.getDefaultFileName(extension))
                },
                onSaveClick = viewModel::saveDocument
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 编辑器
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.large,
                    tonalElevation = 1.dp
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // 编辑器标题
                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                            shape = MaterialTheme.shapes.large,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Markdown",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        
                        MarkdownEditor(
                            content = document.content,
                            onContentChange = viewModel::updateContent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(8.dp)
                        )
                    }
                }
                
                // 预览
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.large,
                    tonalElevation = 1.dp
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // 预览标题
                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                            shape = MaterialTheme.shapes.large,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "预览",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        
                        MarkdownPreview(
                            htmlContent = previewHtml,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(8.dp)
                        )
                    }
                }
            }
            
            // 加载指示器
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(androidx.compose.ui.Alignment.Center)
                )
            }
        }
    }
}
