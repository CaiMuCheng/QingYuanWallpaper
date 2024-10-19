package com.mucheng.qingyuan.wallpaper.paging.page

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.mucheng.qingyuan.wallpaper.application.AppContext
import com.mucheng.qingyuan.wallpaper.model.page.HomePictureModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import okhttp3.Request

class HomePagingSource(
    private val url: String
) : PagingSource<Int, HomePictureModel>() {

    override fun getRefreshKey(state: PagingState<Int, HomePictureModel>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, HomePictureModel> {
        val data = fetchDataForever(count = params.loadSize)
        val page = params.key ?: 0
        val prevKey = if (page > 0) page - 1 else null
        val nextKey = if (data.isNotEmpty()) page + 1 else null

        return LoadResult.Page(
            data = data,
            prevKey = prevKey,
            nextKey = nextKey
        )
    }

    private suspend fun fetchDataForever(count: Int): List<HomePictureModel> {
        while (true) {
            val result = fetchData(count)
            if (result.isNotEmpty()) {
                return result
            }
            delay(200L)
        }
    }

    private suspend fun fetchData(count: Int): List<HomePictureModel> = coroutineScope {
        List(count) {
            async(Dispatchers.IO) {
                try {
                    val response = AppContext.okHttpClient
                        .newCall(
                            Request.Builder()
                                .url(url)
                                .get()
                                .build()
                        )
                        .execute()
                    val body = response.body ?: return@async null
                    body.use {
                        if (!response.isSuccessful) return@async null
                        it.byteStream().use { stream ->
                            val originalBitmap = Glide
                                .with(AppContext.instance)
                                .asBitmap()
                                .load(stream.readBytes())
                                .submit()
                                .get()

                            // val originalBitmap = BitmapFactory.decodeStream(stream)
                            if (originalBitmap != null) {
                                val zippedBitmap = Bitmap.createScaledBitmap(
                                    originalBitmap,
                                    originalBitmap.width / 2,
                                    originalBitmap.height / 2,
                                    true
                                )
                                HomePictureModel(
                                    zippedBitmap = zippedBitmap,
                                    originalBitmap = originalBitmap
                                )
                            } else {
                                null
                            }
                        }
                    }
                } catch (e: Exception) {
                    if (e is GlideException) {
                        e.logRootCauses("")
                    }
                    // e.printStackTrace()
                    null
                }
            }
        }.awaitAll().filterNotNull()
    }

}