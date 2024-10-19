package com.mucheng.qingyuan.wallpaper.ui.route.main.page

import android.Manifest
import android.graphics.Bitmap
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateZoomBy
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ArrowUpward
import androidx.compose.material.icons.twotone.Download
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.bumptech.glide.integration.compose.CrossFade
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.mucheng.qingyuan.wallpaper.R
import com.mucheng.qingyuan.wallpaper.viewmodel.main.page.HomePageIntent
import com.mucheng.qingyuan.wallpaper.viewmodel.main.page.HomePageViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

enum class PictureCategory(
    @StringRes val labelResId: Int,
    val url: String
) {
    Bing(
        labelResId = R.string.bing,
        url = "https://bing.img.run/rand_m.php"
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    homePageViewModel: HomePageViewModel = viewModel()
) {
    val context = LocalContext.current
    val homePageState by homePageViewModel.state.collectAsStateWithLifecycle()
    val homePageIntent = homePageViewModel.intent
    val selectedCategory = homePageState.selectedPictureCategory
    val homePictureModelLazyPagingItems =
        homePageState.homePictureModelFlow.collectAsLazyPagingItems()
    val lazyStaggeredGridState = homePageState.scrollState
    val displayPicture = homePageState.displayPicture
    val coroutineScope = rememberCoroutineScope()
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                Toast.makeText(context, R.string.permission_denied, Toast.LENGTH_SHORT)
                    .show()
                return@rememberLauncherForActivityResult
            }
            if (displayPicture != null) {
                homePageIntent.trySend(HomePageIntent.DownloadPicture(displayPicture))
            }
        }

    Column(
        Modifier.fillMaxSize()
    ) {
        LazyRow(
            contentPadding = PaddingValues(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            items(PictureCategory.entries.size) {
                val pictureCategory = PictureCategory.entries[it]
                val isSelected = pictureCategory == selectedCategory
                Text(
                    stringResource(pictureCategory.labelResId),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            homePageIntent.trySend(HomePageIntent.SelectCategory(pictureCategory))
                        }
                )
            }
        }
        PullToRefreshBox(
            modifier = Modifier.fillMaxSize(),
            isRefreshing = homePictureModelLazyPagingItems.loadState.refresh is LoadState.Loading,
            onRefresh = {
                homePageIntent.trySend(HomePageIntent.Refresh)
            }
        ) {
            if (homePictureModelLazyPagingItems.loadState.refresh is LoadState.Error &&
                homePictureModelLazyPagingItems.itemCount == 0
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        stringResource(R.string.network_error),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(count = 2),
                    contentPadding = PaddingValues(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalItemSpacing = 10.dp,
                    state = lazyStaggeredGridState,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    items(homePictureModelLazyPagingItems.itemCount, key = { it }) { index ->
                        val homePictureModel = homePictureModelLazyPagingItems[index]
                        ElevatedCard(
                            shape = MaterialTheme.shapes.medium,
                            onClick = {
                                homePageIntent.trySend(
                                    HomePageIntent.DisplayPicture(
                                        homePictureModel?.originalBitmap
                                    )
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem()
                        ) {
                            if (homePictureModel?.zippedBitmap != null) {
                                Image(
                                    bitmap = homePictureModel.zippedBitmap.asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    contentScale = ContentScale.FillWidth
                                )
                            }
                        }
                    }
                    item(span = StaggeredGridItemSpan.FullLine) {
                        if (homePictureModelLazyPagingItems.loadState.append is LoadState.Loading) {
                            Column(
                                Modifier
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                LinearProgressIndicator()
                            }
                        }
                    }
                }
                androidx.compose.animation.AnimatedVisibility(
                    visible = lazyStaggeredGridState.firstVisibleItemScrollOffset > 0,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomEnd),
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    FloatingActionButton(
                        onClick = {
                            coroutineScope.launch {
                                lazyStaggeredGridState.animateScrollToItem(0)
                            }
                        }
                    ) {
                        Icon(Icons.TwoTone.ArrowUpward, contentDescription = null)
                    }
                }
            }
        }
    }

    if (displayPicture != null) {
        DisposableEffect(key1 = null) {
            if (context is ComponentActivity) {
                context.window.statusBarColor = Color.Black.toArgb()
            }
            onDispose {
                if (context is ComponentActivity) {
                    context.enableEdgeToEdge()
                }
            }
        }
        DisplayPictureDialog(
            displayPicture = displayPicture,
            onDownloadPicture = {
                launcher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        ) {
            homePageIntent.trySend(HomePageIntent.DisplayPicture(null))
        }
    }

}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun DisplayPictureDialog(
    displayPicture: Bitmap,
    onDownloadPicture: () -> Unit,
    onDismissRequest: () -> Unit
) {
    var offset by remember { mutableStateOf(Offset.Zero) }
    var scale by remember { mutableFloatStateOf(1f) }
    var size by remember { mutableStateOf(IntSize.Zero) }
    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        scale *= zoomChange

        if (abs(offset.x + panChange.x * scale) <= (displayPicture.width * scale - displayPicture.width) / 2) {
            offset += Offset(panChange.x * scale, 0f)
        }

        if (abs(offset.y + panChange.y * scale) <= (displayPicture.height * scale - size.height) / 2) {
            offset += Offset(0f, panChange.y * scale)
        }
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize(),
            shape = RectangleShape,
            colors = CardDefaults.cardColors(
                containerColor = Color.Black
            )
        ) {
            Box(
                Modifier
                    .fillMaxSize()
            ) {
                GlideImage(
                    model = displayPicture,
                    contentDescription = null,
                    transition = CrossFade(tween()),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                        .offset {
                            IntOffset(offset.x.roundToInt(), offset.y.roundToInt())
                        }
                        .scale(scale)
                        .transformable(
                            state = transformableState,
                            lockRotationOnZoomPan = false
                        )
                        .onGloballyPositioned {
                            size = it.size
                        }
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    if (event.type == PointerEventType.Release) {
                                        if (abs(offset.x) > (displayPicture.width * scale - displayPicture.width) / 2) {
                                            val sign = if (offset.x > 0) 1 else -1
                                            offset = Offset(
                                                (displayPicture.width * scale - displayPicture.width) / 2 * sign,
                                                offset.y
                                            )
                                        }

                                        if (abs(offset.y) > (displayPicture.height * scale - size.height) / 2) {
                                            val sign = if (offset.y > 0) 1 else -1
                                            offset = Offset(
                                                offset.x,
                                                (max(
                                                    displayPicture.height * scale - size.height,
                                                    0f
                                                )) / 2 * sign
                                            )
                                        }

                                        if (scale < 1f) {
                                            scale = 1f
                                            offset = Offset.Zero
                                        }
                                    }
                                }
                            }
                        }
                )
                CompositionLocalProvider(LocalContentColor provides Color.White) {
                    Column(
                        Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(
                            onClick = {
                                onDownloadPicture()
                            }
                        ) {
                            Icon(Icons.TwoTone.Download, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}