package com.zhihu.matisse.internal.loader

import android.provider.MediaStore
import com.zhihu.matisse.internal.entity.Album
import com.zhihu.matisse.internal.entity.SelectionSpec

/**
 *
 * chen.si
 * liaction
 * chen.si@ustcsoft.com
 *
 */
object LoaderHelper {
    val QUERY_URI = MediaStore.Files.getContentUri("external")

    val BUCKET_ORDER_BY = MediaStore.Images.Media.DATE_TAKEN + " DESC"

    fun getSelectionArgsForSingleMediaType(mediaType: Int): Array<String> {
        return arrayOf(mediaType.toString())
    }

    val SELECTION_ARGS = arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(), MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString())

    private val CUSTOM_FOLDERS = SelectionSpec.getInstance().customFolders
    private val CUSTOM_MIMETYPES = SelectionSpec.getInstance().mimeTypeSet

    fun getSelection(album: Album? = null) =
            """
                |(
                |${when {
                SelectionSpec.getInstance().onlyShowImages() || SelectionSpec.getInstance().onlyShowVideos() -> {
                    " ${MediaStore.Files.FileColumns.MEDIA_TYPE} =? )"
                }
                else -> {
                    " ${MediaStore.Files.FileColumns.MEDIA_TYPE} =? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE} =?)"
                }
            }
            }
                |${if (CUSTOM_FOLDERS != null && CUSTOM_FOLDERS.isNotEmpty()) {
                buildString {
                    append(" AND bucket_display_name in ( ")
                    CUSTOM_FOLDERS.forEach {
                        append("'$it',")
                    }
                }.dropLast(1).plus(" ) ")
            } else ""
            }
            |${if (CUSTOM_MIMETYPES != null && CUSTOM_MIMETYPES.isNotEmpty()) {
                buildString {
                    append(" AND ( ")
                    CUSTOM_MIMETYPES.forEach {
                        it.mExtensions.forEach {
                            append(" ${MediaStore.MediaColumns.DATA} like ('%$it') OR")
                        }
                    }
                }.dropLast(2).plus(" ) ")
            } else ""
            }
            |${if (album != null && !album.isAll) {
                buildString {
                    append(" AND  bucket_id = ? ")
                }
            } else ""
            }
                |AND ${MediaStore.MediaColumns.SIZE} > 0
                |${if (album == null) {
                buildString {
                    append(")GROUP BY (bucket_id")
                }
            } else ""}
                |""".trimMargin()
}