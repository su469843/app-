package io.qzz.studyhard.mail

import com.google.gson.annotations.SerializedName

// 邮件请求模型
data class EmailRequest(
    val from: String? = "Weather App <weather@studyhard.qzz.io>",
    val to: String,
    val subject: String,
    val html: String? = null,
    val text: String? = null
)

// 带附件的邮件请求
data class AttachmentEmailRequest(
    val from: String? = "Weather App <weather@studyhard.qzz.io>",
    val to: String,
    val subject: String,
    val html: String? = null,
    val text: String? = null,
    val attachments: List<EmailAttachment>
)

// 邮件附件
data class EmailAttachment(
    val filename: String,
    val content: String  // base64编码内容
)

// 定时邮件请求
data class ScheduledEmailRequest(
    val from: String? = "Weather App <weather@studyhard.qzz.io>",
    val to: String,
    val subject: String,
    val html: String? = null,
    val text: String? = null,
    val scheduledAt: String  // 格式: YYYY-MM-DD HH:MM
)

// 批量邮件请求
data class BulkEmailRequest(
    val from: String? = "Weather App <weather@studyhard.qzz.io>",
    val recipients: List<String>,
    val subject: String,
    val html: String? = null,
    val text: String? = null
)

// 邮件响应模型
data class EmailResponse(
    val data: EmailResponseData
)

data class EmailResponseData(
    val id: String,
    val from: String,
    val to: Any, // 可能是String或List<String>
    val subject: String
)

// 定时邮件响应
data class ScheduledEmailResponse(
    val data: ScheduledEmailData,
    val message: String
)

data class ScheduledEmailData(
    val id: String,
    val scheduledTime: String
)

// 健康检查响应
data class HealthResponse(
    val status: String,
    val pendingScheduledEmails: Int,
    val serverTime: String,
    val storage: String
) 