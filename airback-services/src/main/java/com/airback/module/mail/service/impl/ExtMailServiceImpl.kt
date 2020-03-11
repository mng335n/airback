/**
 * Copyright © airback
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.airback.module.mail.service.impl

import com.airback.common.domain.MailRecipientField
import com.airback.configuration.EmailConfiguration
import com.airback.core.utils.StringUtils
import com.airback.module.mail.AttachmentSource
import com.airback.module.mail.DefaultMailer
import com.airback.module.mail.IMailer
import com.airback.module.mail.NullMailer
import com.airback.module.mail.service.ExtMailService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * @author airback Ltd.
 * @since 1.0
 */
@Service
class ExtMailServiceImpl : ExtMailService {
    @Autowired
    private lateinit var emailConfiguration: EmailConfiguration

    override val isMailSetupValid: Boolean
        get() = (StringUtils.isNotBlank(emailConfiguration.smtphost) && StringUtils.isNotBlank(emailConfiguration.username)
                && emailConfiguration.port > -1)

    private val mailer: IMailer
        get() = if (!isMailSetupValid) {
            NullMailer()
        } else DefaultMailer(emailConfiguration)

    override fun sendHTMLMail(fromEmail: String, fromName: String, toEmail: List<MailRecipientField>, subject: String, html: String) {
        mailer.sendHTMLMail(fromEmail, fromName, toEmail, null, null, subject, html, null)
    }

    override fun sendHTMLMail(fromEmail: String, fromName: String, toEmail: List<MailRecipientField>, ccEmail: List<MailRecipientField>?,
                              bccEmail: List<MailRecipientField>?, subject: String, html: String) {
        mailer.sendHTMLMail(fromEmail, fromName, toEmail, ccEmail, bccEmail, subject, html, null)
    }

    override fun sendHTMLMail(fromEmail: String, fromName: String,
                              toEmail: List<MailRecipientField>, ccEmail: List<MailRecipientField>?,
                              bccEmail: List<MailRecipientField>?, subject: String, html: String,
                              attachments: List<AttachmentSource>?, canRetry: Boolean) {
        mailer.sendHTMLMail(fromEmail, fromName, toEmail, ccEmail, bccEmail, subject, html, attachments)
    }
}
