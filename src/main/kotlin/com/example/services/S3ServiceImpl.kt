package com.example.services

import com.example.constants.StaticPath
import com.example.constants.supportedImageFileTypes
import com.example.utils.generateRandomFilename
import io.ktor.http.content.*
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest


class S3ServiceImpl(
    private val s3Client: S3Client,
    private val bucketNameOriginal: String,
    private val bucketName: String,
) : S3Service {
    override suspend fun PartData.FileItem.uploadToS3(path: StaticPath): String {
        val fileName = generateRandomFilename()

        val s3Path = "${path.path}$fileName"

        val shouldResize =
            (path.resizeTo != null || path.thumbnailSize != null) && supportedImageFileTypes.any { fileName.endsWith(it) }

        val putObjectRequest = PutObjectRequest.builder()
            .bucket(
                when (shouldResize) {
                    true -> bucketNameOriginal // this bucket is processed by AWS Lambda to resize images and create thumbnails if needed
                    false -> bucketName // skip resizing and upload to final bucket
                }
            ).run {
                val metadataMap = HashMap<String, String>()
                // Check if images uploaded to that path require resizing or thumbnail creation
                path.thumbnailSize?.let {
                    metadataMap["thumbnail-width"] = it.first.toString()
                    metadataMap["thumbnail-height"] = it.second.toString()
                }
                path.resizeTo?.let {
                    metadataMap["resize-width"] = it.first.toString()
                    metadataMap["resize-height"] = it.second.toString()
                }
                metadata(metadataMap)
                // this metadata will be then be read by the AWS Lambda to resize the image and create a thumbnail
            }
            .key(s3Path)
            .build()

        val data = this.streamProvider().readBytes()
        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(data.inputStream(), data.size.toLong()))

        return s3Path
    }
}