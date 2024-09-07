
# <img src="./repo_assets/ktor.png" width=28 height=28 alt="Ktor"> Ktor Integrations

A boilerplate Ktor project providing common cloud APIs integrations, including authentication, static content handling with async image resizing and thumbnail generation, mailing services, databases, payment gateways and more.

<p>
  <br>
  <img alt="Kotlin" src="./repo_assets/kotlin.png" height="24"/>
  <img alt="Ktor" src="./repo_assets/kotlinktor.png" height="24"/>
  <img alt="AWS" src="./repo_assets/aws.png" height="24"/>
  <img alt="Stripe" src="./repo_assets/stripe.png" height="24"/>
</p>

## Features
### Authentication

- **Token-based Authentication**.
- **Password recovery**: Requesting a password recovery code / link via email using AWS Simple Email Service (SES).
- **Temporary password recovery codes**: Stored in AWS DynamoDB with a Time-To-Live (TTL) value, so they expire after a set period.

### Handling static content

- **Async Image resizing and thumbnail generation using AWS Lambda**: This required 2 S3 buckets configuration. Follow the guide in this [repo](https://github.com/itshedimisawi/aws-lambda-resize-s3).
- **Defining image paths**: Images are uploaded to S3 buckets and image and thumbnail sizes specified for each path by `StaticPath` objects.

#### StaticPath Configuration
When uploading an image, use the `StaticPath` class to specify the image path, the desired size for the image, and the thumbnail size:
`constants/S3.kt`:
```kotlin
sealed class StaticPath(
    val path: String,
    val resizeTo: S3ImageSize? = null, // Pass this to resize images
    val thumbnailSize: S3ImageSize? = null, // Pass this to create a thumbnail
) {
    object STATIC_PROFILE_PHOTO : StaticPath("cdn/profile_photo/", S3ImageSize(512, 512), thumbnailSize = S3ImageSize(128, 128))
    object STATIC_COVER_PHOTO : StaticPath("cdn/cover_photo/", S3ImageSize(1400, 800), thumbnailSize = S3ImageSize(350, 200))
}
```

### Email service

- **Email Sending**: Uses AWS SES to send transactional and recovery emails.
- **Bounce, complaint and unsubscribe events Handling**: handled through SNS Webhooks to comply with SES policy.

#### Email Templates

Templates can be [managed](https://docs.aws.amazon.com/ses/latest/dg/send-personalized-email-api.html) using the AWS CLI.
```sh
aws ses create-template --cli-input-json file://template.json
```
`template.json`:
```json
{
  "Template": {
    "TemplateName": "password_recovery_template",
    "SubjectPart": "Reset Your Password",
    "TextPart": "Hello {{name}},\n\nTo reset your password, please use the following code: {{code}}.\n\nBest regards,\nYour Team",
    "HtmlPart": "<html><body><h1>Password Recovery</h1><p>Hello {{name}},</p><p>To reset your password, please use the following code: <strong>{{code}}</strong>.</p><p>Best regards,<br>Your Team</p></body></html>"
  }
}
```


# Project configuration
All project configuration and credentials can be managed for different environments through the `resources/application.yaml` file.

```text
prod
├── RDS
│   ├── driverClassName
│   ├── jdbcURL
│   ├── user
│   └── password
├── DynamoDB
│   ├── access_key
│   ├── secret_key
│   ├── region
│   ├── confirm_password_code_table
│   └── confirm_password_code_table_key
├── SES
│   ├── access_key
│   ├── secret_key
│   ├── config_set
│   ├── sender
│   └── password_recovery_template
├── S3
│   ├── access_key
│   ├── secret_key
│   ├── bucket_name
│   └── bucket_name_original
├── SNS
│   ├── topic_arn_bounce
│   ├── topic_arn_complaint
│   └── topic_arn_unsubscribe
└── Stripe
    └── webhook_secret

Development (dev)
└── ...
```

## 📫 Contact me
<p>
<a href="https://www.linkedin.com/in/hedimissaoui/">
<img src="./repo_assets/linkedin.png" alt="Linkedin" height="40"/>
</a> 
<br>

## Find this repository useful? :heart:
Support it by joining __[stargazers](https://github.com/itshedimisawi/ktor-integrations/stargazers)__ for this repository. :star:

## Contributing

Contributions are welcome from everyone! Whether it's a bug fix, new feature, or improvements to the documentation, your help is appreciated.