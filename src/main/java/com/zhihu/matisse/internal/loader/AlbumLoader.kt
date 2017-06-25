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
import com.zhihu.matisse.internal.entity.SelectionSpec

/**
 * Load all albums (grouped by bucket_id) into a single cursor.
 */
class AlbumLoader private constructor(context: Context, selection: String, selectionArgs: Array<String>) : CursorLoader(context, LoaderHelper.QUERY_URI, PROJECTION, selection, selectionArgs, LoaderHelper.BUCKET_ORDER_BY) {

    override fun loadInBackground(): Cursor {
        val albums = super.loadInBackground()
        val allAlbum = MatrixCursor(COLUMNS)
        var totalCount = 0
        var allAlbumCoverPath = ""
        if (albums != null) {
            while (albums.moveToNext()) {
                totalCount += albums.getInt(albums.getColumnIndex(COLUMN_COUNT))
            }
            if (albums.moveToFirst()) {
                allAlbumCoverPath = albums.getString(albums.getColumnIndex(MediaStore.MediaColumns.DATA))
            }
        }
        allAlbum.addRow(arrayOf(Album.ALBUM_ID_ALL, Album.ALBUM_ID_ALL, Album.ALBUM_NAME_ALL, allAlbumCoverPath, totalCount.toString()))

        return MergeCursor(arrayOf(allAlbum, albums))
    }

    override fun onContentChanged() {
        // FIXME a dirty way to fix loading multiple times
    }

    companion object {
        val COLUMN_COUNT = "count"
        private val COLUMNS = arrayOf(
                MediaStore.Files.FileColumns._ID,
                "bucket_id",
                "bucket_display_name",
                MediaStore.MediaColumns.DATA, COLUMN_COUNT
        )
        private val PROJECTION = arrayOf(
                MediaStore.Files.FileColumns._ID,
                "bucket_id",
                "bucket_display_name",
                MediaStore.MediaColumns.DATA,
                "COUNT(*) AS " + COLUMN_COUNT
        )


        fun newInstance(context: Context): CursorLoader {
            val selectionArgs: Array<String>
            if (SelectionSpec.getInstance().onlyShowImages()) {
                selectionArgs = LoaderHelper.getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)
            } else if (SelectionSpec.getInstance().onlyShowVideos()) {
                selectionArgs = LoaderHelper.getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
            } else {
                selectionArgs = LoaderHelper.SELECTION_ARGS
            }
            return AlbumLoader(context, LoaderHelper.getSelection(), selectionArgs)
        }
    }
}