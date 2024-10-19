package com.mucheng.qingyuan.wallpaper.viewmodel.main.page

import android.graphics.Bitmap
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mucheng.qingyuan.wallpaper.R
import com.mucheng.qingyuan.wallpaper.application.AppContext
import com.mucheng.qingyuan.wallpaper.model.page.HomePictureModel
import com.mucheng.qingyuan.wallpaper.paging.page.HomePagingSource
import com.mucheng.qingyuan.wallpaper.ui.route.main.page.PictureCategory
import com.mucheng.qingyuan.wallpaper.viewmodel.MVIViewModel
import com.mucheng.qingyuan.wallpaper.viewmodel.UIIntent
import com.mucheng.qingyuan.wallpaper.viewmodel.UIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.EnumMap

data class HomePageState(
    val selectedPictureCategory: PictureCategory,
    val scrollState: LazyStaggeredGridState,
    val homePictureModelFlow: Flow<PagingData<HomePictureModel>>,
    val displayPicture: Bitmap?,
) : UIState

sealed class HomePageIntent : UIIntent {
    data class SelectCategory(val targetPictureCategory: PictureCategory) :
        HomePageIntent()

    data object Refresh : HomePageIntent()

    data class DownloadPicture(val bitmap: Bitmap) : HomePageIntent()

    data class DisplayPicture(val displayPicture: Bitmap?) : HomePageIntent()
}

class HomePageViewModel : MVIViewModel<HomePageState, HomePageIntent>() {

    private lateinit var homePictureModelFlowMap: MutableMap<PictureCategory, Flow<PagingData<HomePictureModel>>>

    private lateinit var scrollStateMap: MutableMap<PictureCategory, LazyStaggeredGridState>

    override val initialState: HomePageState
        get() {
            homePictureModelFlowMap = EnumMap(PictureCategory::class.java)
            scrollStateMap = EnumMap(PictureCategory::class.java)

            val pictureCategory = PictureCategory.Bing
            val scrollState = fetchScrollState(pictureCategory)
            val homePictureModelFlow = fetchHomePictureModelFlow(pictureCategory)
            return HomePageState(
                selectedPictureCategory = pictureCategory,
                scrollState = scrollState,
                homePictureModelFlow = homePictureModelFlow,
                displayPicture = null
            )
        }

    init {
        viewModelScope.launch {
            intent.send(HomePageIntent.Refresh)
        }
    }

    override suspend fun HomePageIntent.onReceiveIntent() {
        when (this) {
            is HomePageIntent.SelectCategory -> selectCategory(targetPictureCategory)
            HomePageIntent.Refresh -> refresh()
            is HomePageIntent.DisplayPicture -> displayPicture(displayPicture)
            is HomePageIntent.DownloadPicture -> downloadPicture(bitmap)
        }
    }

    private fun downloadPicture(bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            val context = AppContext.instance
            val downloadFolder =
                File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOWNLOADS)
            val folder = File(downloadFolder, "QingYuan")
            folder.mkdirs()

            try {
                val file = File(folder, "${bitmap.toString().substringAfter("@")}.png")
                file.outputStream().use {
                    bitmap.compress(
                        Bitmap.CompressFormat.PNG,
                        100,
                        it
                    )
                }
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.save_successfully, file.absolutePath),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.save_failed, e.message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun displayPicture(displayPicture: Bitmap?) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    displayPicture = displayPicture
                )
            }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    scrollState = fetchScrollState(it.selectedPictureCategory, forceRefresh = true),
                    homePictureModelFlow = fetchHomePictureModelFlow(
                        it.selectedPictureCategory,
                        forceRefresh = true
                    )
                )
            }
        }
    }

    private fun selectCategory(targetPictureCategory: PictureCategory) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    selectedPictureCategory = targetPictureCategory,
                    scrollState = fetchScrollState(targetPictureCategory),
                    homePictureModelFlow = fetchHomePictureModelFlow(targetPictureCategory)
                )
            }
        }
    }

    private fun fetchHomePictureModelFlow(
        pictureCategory: PictureCategory,
        forceRefresh: Boolean = false
    ): Flow<PagingData<HomePictureModel>> {
        if (homePictureModelFlowMap.containsKey(pictureCategory) && !forceRefresh) {
            return homePictureModelFlowMap[pictureCategory]!!
        }

        val pager = generatePager(pictureCategory.url)
        val homePictureModelFlow = pager.flow.cachedIn(viewModelScope)
        homePictureModelFlowMap[pictureCategory] = homePictureModelFlow
        return homePictureModelFlow
    }

    private fun fetchScrollState(
        pictureCategory: PictureCategory,
        forceRefresh: Boolean = false
    ): LazyStaggeredGridState {
        if (scrollStateMap.containsKey(pictureCategory) && !forceRefresh) {
            return scrollStateMap[pictureCategory]!!
        }

        val scrollState = generateScrollState()
        scrollStateMap[pictureCategory] = scrollState
        return scrollState
    }

    private fun generatePager(url: String): Pager<Int, HomePictureModel> {
        return Pager(
            config = PagingConfig(
                pageSize = 2,
                prefetchDistance = 2
            ),
            pagingSourceFactory = {
                HomePagingSource(url)
            }
        )
    }

    private fun generateScrollState(): LazyStaggeredGridState {
        return LazyStaggeredGridState()
    }

}