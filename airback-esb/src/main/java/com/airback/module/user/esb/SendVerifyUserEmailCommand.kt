package com.airback.module.user.esb

import com.google.common.eventbus.AllowConcurrentEvents
import com.google.common.eventbus.Subscribe
import com.airback.common.GenericLinkUtils
import com.airback.common.domain.MailRecipientField
import com.airback.common.i18n.MailI18nEnum
import com.airback.configuration.ApplicationConfiguration
import com.airback.configuration.IDeploymentMode
import com.airback.core.utils.DateTimeUtils
import com.airback.i18n.LocalizationHelper
import com.airback.module.billing.UserStatusConstants
import com.airback.module.esb.GenericCommand
import com.airback.module.mail.service.ExtMailService
import com.airback.module.mail.service.IContentGenerator
import com.airback.module.user.accountsettings.localization.UserI18nEnum
import com.airback.module.user.domain.User
import com.airback.module.user.service.BillingAccountService
import com.airback.module.user.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import java.util.*

/**
 * @author airback Ltd
 * @since 6.0.0
 */
@Component
class SendVerifyUserEmailCommand(private val deploymentMode: IDeploymentMode,
                                 private val billingAccountService: BillingAccountService,
                                 private val userService: UserService,
                                 private val extMailService: ExtMailService,
                                 private val contentGenerator: IContentGenerator,
                                 private val applicationConfiguration: ApplicationConfiguration) : GenericCommand() {

    @AllowConcurrentEvents
    @Subscribe
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun sendVerifyEmailRequest(event: SendUserEmailVerifyRequestEvent) {
        Thread.sleep(5000)
        sendConfirmEmailToUser(event.sAccountId, event.user)
        event.user.status = UserStatusConstants.EMAIL_VERIFIED_REQUEST
        userService.updateWithSession(event.user, event.user.username)
    }

    fun sendConfirmEmailToUser(sAccountId:Int, user: User) {
        val account = billingAccountService.getAccountById(sAccountId)
        if (account != null) {
            contentGenerator.putVariable("user", user)
            val siteUrl = deploymentMode.getSiteUrl(account.subdomain)
            contentGenerator.putVariable("siteUrl", siteUrl)
            val confirmLink = GenericLinkUtils.generateConfirmEmailLink(siteUrl, user.username)
            contentGenerator.putVariable("linkConfirm", confirmLink)
            contentGenerator.putVariable("copyRight", LocalizationHelper.getMessage(Locale.US, MailI18nEnum.Copyright,
                    DateTimeUtils.getCurrentYear()))
            extMailService.sendHTMLMail(applicationConfiguration.notifyEmail, applicationConfiguration.siteName,
                    listOf(MailRecipientField(user.email, "${user.firstname} ${user.lastname}")),
                    LocalizationHelper.getMessage(Locale.US, UserI18nEnum.MAIL_CONFIRM_EMAIL_SUBJECT),
                    contentGenerator.parseFile("mailVerifyEmailUser.ftl", Locale.US))
        } else {
            LOG.error("Can not find account with id $sAccountId then can not send the verification email")
        }
    }

    companion object {
        val LOG = LoggerFactory.getLogger(SendVerifyUserEmailCommand::class.java)
    }
}