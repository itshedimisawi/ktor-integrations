package com.example.plugins

import com.example.data.repositories.AuthenticationRepo
import com.example.data.repositories.AuthenticationRepoImpl
import com.example.services.*
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.ses.SesClient
import software.amazon.awssdk.services.sns.SnsClient

fun Application.configureFrameworks() {
    install(Koin) {
        slf4jLogger()
        modules(module {
            single<DynamoDbClient> {
                val accessKey = environment.config.property("aws.prod.dynamodb.access_key").getString()
                val secretKey = environment.config.property("aws.prod.dynamodb.secret_key").getString()
                val region = environment.config.property("aws.prod.dynamodb.region").getString()

                val credentialsProvider = StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        accessKey,
                        secretKey
                    )
                )

                DynamoDbClient.builder().region(Region.of(region)).credentialsProvider(credentialsProvider).build()
            }

            // Services
            single<S3Service> {
                val accessKey = environment.config.property("aws.prod.s3.access_key").getString()
                val secretKey = environment.config.property("aws.prod.s3.secret_key").getString()
                val region = environment.config.property("aws.prod.s3.region").getString()

                val credentialsProvider = StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        accessKey,
                        secretKey
                    )
                )

                val s3Client = S3Client.builder().region(Region.of(region)).credentialsProvider(credentialsProvider).build()

                S3ServiceImpl(
                    s3Client = s3Client,
                    bucketNameOriginal = environment.config.property("aws.prod.s3.bucket_name_original").getString(),
                    bucketName = environment.config.property("aws.prod.s3.bucket_name").getString(),
                )
            }

            single<SnsService> {
                val accessKey = environment.config.property("aws.prod.sns.access_key").getString()
                val secretKey = environment.config.property("aws.prod.sns.secret_key").getString()
                val region = environment.config.property("aws.prod.sns.region").getString()

                val credentialsProvider = StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        accessKey,
                        secretKey
                    )
                )

                val snsClient = SnsClient.builder().region(Region.of(region)).credentialsProvider(credentialsProvider).build()

                SnsServiceImpl(
                    snsClient = snsClient,
                )
            }

            single<SesService> {
                val accessKey = environment.config.property("aws.prod.ses.access_key").getString()
                val secretKey = environment.config.property("aws.prod.ses.secret_key").getString()
                val region = environment.config.property("aws.prod.ses.region").getString()

                val credentialsProvider = StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        accessKey,
                        secretKey
                    )
                )

                val sesClient = SesClient.builder().region(Region.of(region)).credentialsProvider(credentialsProvider).build()

                SesServiceImpl(
                    sesClient = sesClient,
                    senderEmail = environment.config.property("aws.prod.ses.sender").getString(),
                    configurationSet = environment.config.property("aws.prod.ses.config_set").getString(),
                )
            }

            // Repositories
            single<AuthenticationRepo>{
                AuthenticationRepoImpl(
                    dynamoDbClient = inject<DynamoDbClient>().value,
                    sesService = inject<SesService>().value,
                    passwordResetCodeTable = environment.config.property("aws.prod.dynamodb.password_reset_code_table").getString(),
                    passwordResetCodeTableKey = environment.config.property("aws.prod.dynamodb.password_reset_code_table_key").getString(),
                    passwordRecoveryTemplate = environment.config.property("aws.prod.ses.password_recovery_template").getString(),
                    passwordResetBaseUrl = environment.config.property("aws.prod.ses.password_reset_base_url").getString(),
                )
            }


        })
    }
}
