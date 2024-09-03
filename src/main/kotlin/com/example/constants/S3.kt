package com.example.constants


/*
* This class is used to define the path of the static files in the S3 bucket
* resizeTo and thumbnailSize will be included in the image metadata to be then used by the AWS Lambda to
* resize the image and create a thumbnail if needed
*
* path: the path of the file in the S3 bucket
* resizeTo: the size to resize the image to when uploaded
* thumbnailSize: the size to create the thumbnail
*/

sealed class StaticPath(
    val path: String,
    val resizeTo: Pair<Int, Int>? = null, // pass this to resize images
    val thumbnailSize: Pair<Int, Int>? = null, // pass this to create thumbnail
) {
    object STATIC_PROFILE_PHOTO : StaticPath("cdn/profile_photo/", Pair(512, 512), thumbnailSize = Pair(128, 128))
    object STATIC_COVER_PHOTO : StaticPath("cdn/cover_photo/", Pair(1400, 800), thumbnailSize = Pair(350, 200))
}

// types that support resizing and thumbnails by the AWS Lambda
val supportedImageFileTypes = listOf(".png", ".jpg", ".jpeg")