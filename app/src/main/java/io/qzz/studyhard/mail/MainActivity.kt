package io.qzz.studyhard.mail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import java.util.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HelloWorldScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HelloWorldScreen(
    weatherViewModel: WeatherViewModel = viewModel(),
    emailViewModel: EmailViewModel = viewModel()
) {
    val context = LocalContext.current
    var visible by remember { mutableStateOf(false) }
    val weatherUiState by weatherViewModel.uiState.collectAsState()
    val emailUiState by emailViewModel.uiState.collectAsState()
    var cityName by remember { mutableStateOf("Beijing") }
    var emailAddress by remember { mutableStateOf("") }
    var showEmailDialog by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    
    // 创建渐变背景
    val gradientColors = listOf(
        Color(0xFF1A237E), // 深蓝色
        Color(0xFF3949AB), // 蓝色
        Color(0xFF1E88E5)  // 亮蓝色
    )
    
    // 背景粒子动画
    val particles = remember { List(50) { ParticleState() } }
    
    // 启动时获取天气数据
    LaunchedEffect(key1 = true) {
        delay(300)
        visible = true
        weatherViewModel.fetchWeather(cityName)
    }
    
    // 处理邮件发送后的提示
    LaunchedEffect(emailUiState) {
        when (emailUiState) {
            is EmailUiState.Success -> {
                // 显示成功消息
                Toast.makeText(
                    context,
                    (emailUiState as EmailUiState.Success).message,
                    Toast.LENGTH_LONG
                ).show()
                // 重置状态
                delay(3000)
                emailViewModel.resetState()
            }
            is EmailUiState.Error -> {
                // 显示错误消息
                Toast.makeText(
                    context,
                    (emailUiState as EmailUiState.Error).message,
                    Toast.LENGTH_LONG
                ).show()
                // 重置状态
                delay(3000)
                emailViewModel.resetState()
            }
            else -> { /* 不处理其他状态 */ }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(colors = gradientColors)
            ),
        contentAlignment = Alignment.Center
    ) {
        // 粒子背景效果
        particles.forEach { particle ->
            Box(
                modifier = Modifier
                    .offset(particle.x.dp, particle.y.dp)
                    .size(particle.size.dp)
                    .alpha(particle.alpha)
                    .clip(CircleShape)
                    .background(Color.White)
            )
        }
        
        // 半透明云朵装饰效果
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp)
                .alpha(0.15f)
        ) {
            CloudDecoration(
                modifier = Modifier
                    .size(120.dp)
                    .offset((-20).dp, 20.dp)
                    .blur(radius = 8.dp)
            )
            CloudDecoration(
                modifier = Modifier
                    .size(90.dp)
                    .offset(250.dp, 150.dp)
                    .blur(radius = 6.dp)
            )
            CloudDecoration(
                modifier = Modifier
                    .size(70.dp)
                    .offset(50.dp, 300.dp)
                    .blur(radius = 4.dp)
            )
        }
        
        // 电子邮件输入对话框
        if (showEmailDialog) {
            val currentWeather = (weatherUiState as? WeatherUiState.Success)?.data
            
            AlertDialog(
                onDismissRequest = { showEmailDialog = false },
                title = { Text("发送天气信息") },
                text = {
                    Column {
                        Text("将当前天气信息发送到您的邮箱")
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = emailAddress,
                            onValueChange = { emailAddress = it },
                            label = { Text("邮箱地址") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done,
                                keyboardType = KeyboardType.Email
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { keyboardController?.hide() }
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (emailAddress.isNotBlank()) {
                                if (isValidEmail(emailAddress)) {
                                    currentWeather?.let {
                                        emailViewModel.sendWeatherEmail(emailAddress, it)
                                        showEmailDialog = false
                                    } ?: run {
                                        Toast.makeText(
                                            context,
                                            "无法获取天气信息",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "请输入有效的邮箱地址",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "请输入邮箱地址",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        enabled = emailUiState !is EmailUiState.Sending
                    ) {
                        if (emailUiState is EmailUiState.Sending) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Text("发送")
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEmailDialog = false }) {
                        Text("取消")
                    }
                }
            )
        }
        
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + slideInVertically { it / 2 }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                // 应用标题
                Text(
                    text = "天气预报",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                
                // 城市搜索输入框
                OutlinedTextField(
                    value = cityName,
                    onValueChange = { cityName = it },
                    label = { Text("输入城市名称") },
                    singleLine = true,
                    shape = RoundedCornerShape(50),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = Color.White,
                        cursorColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                        containerColor = Color.White.copy(alpha = 0.1f)
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            weatherViewModel.fetchWeather(cityName)
                            keyboardController?.hide()
                        }
                    ),
                    trailingIcon = {
                        IconButton(onClick = {
                            weatherViewModel.fetchWeather(cityName)
                            keyboardController?.hide()
                        }) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "搜索",
                                tint = Color.White
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 天气卡片
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.studyhard.qzz.io"))
                            context.startActivity(intent)
                        },
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "Hello World",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A237E),
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        when (val state = weatherUiState) {
                            is WeatherUiState.Loading -> {
                                CircularProgressIndicator(
                                    color = Color(0xFF1A237E),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "正在获取天气数据...",
                                    color = Color.Gray
                                )
                            }
                            is WeatherUiState.Success -> {
                                val weather = state.data
                                
                                // 天气图标 - 使用带动画的自定义图标
                                AnimatedWeatherIcon(
                                    weatherType = weather.weather.firstOrNull()?.main ?: "Clear",
                                    modifier = Modifier.size(80.dp)
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                // 城市名称
                                Text(
                                    text = "${weather.name}, ${weather.sys.country}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1A237E)
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // 温度 - 带有动画效果
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${weather.main.temp.toInt()}",
                                        fontSize = 64.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF1A237E)
                                    )
                                    Text(
                                        text = "°C",
                                        fontSize = 36.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1A237E),
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                                
                                // 天气描述
                                Text(
                                    text = weather.weather.firstOrNull()?.description?.replaceFirstChar {
                                        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                                    } ?: "",
                                    fontSize = 18.sp,
                                    color = Color(0xFF3949AB)
                                )
                                
                                Spacer(modifier = Modifier.height(24.dp))
                                
                                Divider(color = Color.LightGray.copy(alpha = 0.5f))
                                
                                Spacer(modifier = Modifier.height(24.dp))
                                
                                // 额外信息
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    WeatherInfoItem(
                                        icon = Icons.Default.Water,
                                        value = "${weather.main.humidity}%",
                                        label = "湿度"
                                    )
                                    
                                    WeatherInfoItem(
                                        icon = Icons.Default.Air,
                                        value = "${weather.wind.speed} m/s",
                                        label = "风速"
                                    )
                                    
                                    WeatherInfoItem(
                                        icon = Icons.Default.Visibility,
                                        value = "${weather.visibility / 1000} km",
                                        label = "能见度"
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(24.dp))
                                
                                // 日出日落信息
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                                    val sunriseTime = sdf.format(Date(weather.sys.sunrise * 1000))
                                    val sunsetTime = sdf.format(Date(weather.sys.sunset * 1000))
                                    
                                    WeatherInfoItem(
                                        icon = Icons.Default.LightMode,
                                        value = sunriseTime,
                                        label = "日出"
                                    )
                                    
                                    WeatherInfoItem(
                                        icon = Icons.Default.DarkMode,
                                        value = sunsetTime,
                                        label = "日落"
                                    )
                                }
                                
                                // 添加在 Success 状态时显示分享按钮
                                // 在日出日落信息下方添加
                                Spacer(modifier = Modifier.height(24.dp))
                                
                                Button(
                                    onClick = { showEmailDialog = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF1A237E)
                                    )
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Email,
                                            contentDescription = "发送邮件",
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("发送天气信息到邮箱")
                                    }
                                }
                            }
                            is WeatherUiState.Error -> {
                                Icon(
                                    Icons.Default.Error,
                                    contentDescription = "错误",
                                    tint = Color.Red,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "获取天气失败: ${state.message}",
                                    color = Color.Red
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "点击跳转到网站",
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier.alpha(0.8f)
                )
            }
        }
    }
}

@Composable
fun WeatherInfoItem(icon: ImageVector, value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color(0xFF1A237E)
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A237E)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun AnimatedWeatherIcon(weatherType: String, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "weather animation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), 
        label = "sun rotation"
    )
    
    val bounceValue by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )
    
    val icon = getWeatherIcon(weatherType)
    
    when (weatherType.lowercase()) {
        "clear" -> {
            Icon(
                imageVector = icon,
                contentDescription = weatherType,
                tint = Color(0xFFFFC107),
                modifier = modifier
                    .rotate(rotation)
            )
        }
        "clouds", "cloudy" -> {
            Icon(
                imageVector = icon,
                contentDescription = weatherType,
                tint = Color(0xFF78909C),
                modifier = modifier
                    .offset(y = bounceValue.dp / 3)
            )
        }
        "rain", "drizzle" -> {
            Icon(
                imageVector = icon,
                contentDescription = weatherType,
                tint = Color(0xFF0288D1),
                modifier = modifier
                    .offset(y = bounceValue.dp / 2)
            )
        }
        else -> {
            Icon(
                imageVector = icon,
                contentDescription = weatherType,
                tint = Color(0xFF1A237E),
                modifier = modifier
            )
        }
    }
}

@Composable
fun CloudDecoration(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color.White)
    )
}

// 粒子动画状态
class ParticleState {
    private val random = java.util.Random()
    
    var x by mutableStateOf(random.nextInt(401).toFloat())
    var y by mutableStateOf(random.nextInt(801).toFloat())
    var size by mutableStateOf((random.nextInt(4) + 2).toFloat())
    var alpha by mutableStateOf(0.1f + random.nextFloat() * 0.2f)
}

// 改进邮箱验证函数
private fun isValidEmail(email: String): Boolean {
    if (email.isBlank()) return false
    val pattern = Patterns.EMAIL_ADDRESS
    return pattern.matcher(email).matches()
}

fun getWeatherIcon(weatherCondition: String): ImageVector {
    return when (weatherCondition.lowercase()) {
        "clear" -> Icons.Default.LightMode
        "clouds", "cloudy" -> Icons.Default.Cloud
        "rain", "drizzle" -> Icons.Default.Water
        "thunderstorm" -> Icons.Default.ElectricBolt
        "snow" -> Icons.Default.AcUnit
        "mist", "fog", "haze" -> Icons.Default.Cloud
        else -> Icons.Default.LightMode
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MaterialTheme {
        HelloWorldScreen()
    }
} 