package com.example.services

interface SesService {
    // send email using SES
    // to: email address
    // template: SES template name uploaded to SES using AWS CLI
    // templateData: JSON string to be used in the template
    suspend fun sendEmail(to: String, template: String, templateData: String)
}