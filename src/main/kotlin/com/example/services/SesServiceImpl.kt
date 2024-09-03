package com.example.services

import software.amazon.awssdk.services.ses.SesClient
import software.amazon.awssdk.services.ses.model.Destination
import software.amazon.awssdk.services.ses.model.SendTemplatedEmailRequest

class SesServiceImpl(
    private val sesClient: SesClient,
    private val senderEmail: String,
    private val configurationSet: String,
): SesService {
    override suspend fun sendEmail(to: String, template: String, templateData: String) {
        val email = SendTemplatedEmailRequest.builder()
            .destination(
                Destination.builder()
                    .toAddresses(to).build()
            ).source(senderEmail)
            .configurationSetName(configurationSet)
            .template(template)
            .templateData(templateData)
            .build()
        sesClient.sendTemplatedEmail(email)
    }
}