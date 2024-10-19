package com.mucheng.qingyuan.wallpaper.ui.route.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Forest
import androidx.compose.material.icons.twotone.Home
import androidx.compose.material.icons.twotone.MoreVert
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mucheng.qingyuan.wallpaper.R
import com.mucheng.qingyuan.wallpaper.ui.route.main.page.HomePage
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private enum class BottomNavigationItemTypes(
    @StringRes val labelResId: Int,
    val icon: ImageVector,
    val content: @Composable () -> Unit
) {
    Home(
        labelResId = R.string.home,
        icon = Icons.TwoTone.Home,
        content = { HomePage() }
    ),
//    Popular(
//        labelResId = R.string.popular,
//        icon = Icons.TwoTone.LocalFireDepartment,
//        content = { PopularPage() }
//    ),
//    Collections(
//        labelResId = R.string.collections,
//        icon = Icons.TwoTone.Star,
//        content = { CollectionsPage() }
//    ),
//    Settings(
//        labelResId = R.string.settings,
//        icon = Icons.TwoTone.Settings,
//        content = { SettingsPage() }
//    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val pagerState = rememberPagerState { BottomNavigationItemTypes.entries.size }
    var showMoreDropdownMenu by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    Scaffold(
        Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(stringResource(R.string.app_name))
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            showAboutDialog = true
                        },
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.TwoTone.Forest,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF79BD9A), CircleShape)
                                .scale(0.6f)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showMoreDropdownMenu = true }
                    ) {
                        Icon(Icons.TwoTone.MoreVert, contentDescription = null)
                        MoreDropdownMenu(
                            expanded = showMoreDropdownMenu,
                            onDismissRequest = { showMoreDropdownMenu = false },
                            onShowAboutDialog = { showAboutDialog = true }
                        )
                    }

                },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            NavigationBar {
                repeat(pagerState.pageCount) {
                    val bottomNavigationItemType = BottomNavigationItemTypes.entries[it]
                    NavigationBarItem(
                        pagerState.targetPage == it,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(it)
                            }
                        },
                        label = {
                            Text(stringResource(bottomNavigationItemType.labelResId))
                        },
                        icon = {
                            Icon(bottomNavigationItemType.icon, contentDescription = null)
                        }
                    )
                }
            }
        }
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) { page ->
            BottomNavigationItemTypes.entries[page].content()
        }
    }

    if (showAboutDialog) {
        AboutDialog { showAboutDialog = false }
    }
}

@Composable
private fun MoreDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onShowAboutDialog: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        shape = MaterialTheme.shapes.large,
        shadowElevation = 6.dp
    ) {
        DropdownMenuItem(
            text = {
                Text(stringResource(R.string.about))
            },
            onClick = {
                onShowAboutDialog()
                onDismissRequest()
            }
        )
    }
}

@Suppress("DEPRECATION")
@Composable
private fun AboutDialog(
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(
                Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.TwoTone.Forest,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .background(Color(0xFF79BD9A), CircleShape)
                                .size(50.dp)
                                .scale(0.5f)
                        )
                        Spacer(Modifier.width(24.dp))
                        Column {
                            Text(
                                stringResource(R.string.app_name),
                                style = MaterialTheme.typography.titleMedium
                            )
                            val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                packageInfo.longVersionCode
                            } else {
                                packageInfo.versionCode
                            }
                            Text(
                                "${packageInfo.versionName} ($versionCode)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                    Text(
                        stringResource(R.string.about_introduction),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(12.dp))
                    val year =
                        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year
                    Text(
                        buildAnnotatedString {
                            withStyle(SpanStyle()) {
                                append("Copyright © 2024")
                                if (year > 2024) {
                                    append("-$year")
                                }
                                append(" ")
                            }
                            val link = LinkAnnotation.Clickable(
                                "",
                                TextLinkStyles(
                                    SpanStyle(
                                        color = MaterialTheme.colorScheme.primary,
                                        textDecoration = TextDecoration.Underline,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            ) {
                                context.contractAuthor()
                            }
                            withLink(link) {
                                append("苏沐橙好菜")
                            }
                        },
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(24.dp))
                    TextButton(
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        onClick = {
                            context.joinQQGroup()
                        }
                    ) {
                        Text(stringResource(R.string.join_our_qq_group))
                    }
                }
            }
        }
    }
}

@Suppress("SpellCheckingInspection")
private const val key = "2eZMJ9qmPUx7cm-6MPTh8SVB1rSSYfnc"

private const val qq = "3578557729"

@Suppress("SpellCheckingInspection")
@SuppressLint("IntentWithNullActionLaunch")
private fun Context.joinQQGroup() {
    val intent = Intent()
    intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D$key"))
    try {
        startActivity(intent)
    } catch (_: Exception) {
    }
}

@SuppressLint("IntentWithNullActionLaunch")
@Suppress("SpellCheckingInspection")
private fun Context.contractAuthor() {
    val intent = Intent()
    intent.setData(Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin=$qq"))
    try {
        startActivity(intent)
    } catch (_: Exception) {
    }
}