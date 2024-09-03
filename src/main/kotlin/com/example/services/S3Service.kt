package com.example.services

import com.example.constants.StaticPath
import io.ktor.http.content.*

interface S3Service {
    // uploads Ktor multipart file to S3 bucket and returns the URL
    suspend fun PartData.FileItem.uploadToS3(path: StaticPath): String
}