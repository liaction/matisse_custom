/*
 * Copyright (C) 2014 nohana, Inc.
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhihu.matisse.internal.loader

import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.database.MergeCursor
import android.provider.MediaStore
import android.support.v4.content.CursorLoader
import com.zhihu.matisse.internal.entity.Album
import com.zhihu.matisse.internal.entity.Item
import com.zhihu.matisse.internal.entity.SelectionSpec
import com.zhihu.matisse.internal.utils.MediaStoreCompat

/**
 * Load images and videos into a single cursor.
 */
class AlbumMediaLoader private constructor(context: Context, selection: String, selectionArgs: Array<String>, private val mEnableCapture: Boolean) : CursorLoader(context, LoaderHelper.QUERY_URI, PROJECTION, selection, selectionArgs, LoaderHelper.BUCKET_ORDER_BY) {

    override fun loadInBackground(): Cursor {
        val result = super.loadInBackground()
        if (!mEnableCapture || !MediaStoreCompat.hasCameraFeature(context)) {
            return result
        }
        val dummy = MatrixCursor(PROJECTION)
        dummy.addRow(arrayOf(Item.ITEM_ID_CAPTURE, Item.ITEM_DISPLAY_NAME_CAPTURE, "", 0, 0))
        return MergeCursor(arrayOf(dummy, result))
    }

    override fun onContentChanged() {
        // FIXME a dirty way to fix loading multiple times
    }

    companion object {
        private val PROJECTION = arrayOf(MediaStore.Files.FileColumns._ID, MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.MIME_TYPE, MediaStore.MediaColumns.SIZE, "duration")

        private fun getSelectionAlbumArgs(albumId: String): Array<String> {
            return arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(), MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString(), albumId)
        }

        private fun getSelectionAlbumArgsForSingleMediaType(mediaType: Int, albumId: String): Array<String> {
            return arrayOf(mediaType.toString(), albumId)
        }

        fun newInstance(context: Context, album: Album, capture: Boolean): CursorLoader {
            val selectionArgs: Array<String>
            val enableCapture: Boolean
            if (album.isAll) {
                if (SelectionSpec.getInstance().onlyShowImages()) {
                    selectionArgs = LoaderHelper.getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)
                } else if (SelectionSpec.getInstance().onlyShowVideos()) {
                    selectionArgs = LoaderHelper.getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
                } else {
                    selectionArgs = LoaderHelper.SELECTION_ARGS
                }
                enableCapture = capture
            } else {
                if (SelectionSpec.getInstance().onlyShowImages()) {
                    selectionArgs = getSelectionAlbumArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE,
                            album.id)
                } else if (SelectionSpec.getInstance().onlyShowVideos()) {
                    selectionArgs = getSelectionAlbumArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO,
                            album.id)
                } else {
                    selectionArgs = getSelectionAlbumArgs(album.id)
                }
                enableCapture = false
            }

            return AlbumMediaLoader(context, LoaderHelper.getSelection(album = album), selectionArgs, enableCapture)
        }
    }
}
