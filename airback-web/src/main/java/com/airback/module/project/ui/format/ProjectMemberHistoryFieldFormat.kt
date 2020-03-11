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
package com.airback.module.project.ui.format

import com.hp.gagawa.java.elements.A
import com.hp.gagawa.java.elements.Img
import com.airback.core.utils.StringUtils
import com.airback.html.DivLessFormatter
import com.airback.module.file.service.AbstractStorageService
import com.airback.module.user.domain.SimpleUser
import com.airback.module.user.service.UserService
import com.airback.spring.AppContextUtil
import com.airback.vaadin.AppUI
import com.airback.vaadin.TooltipHelper
import com.airback.vaadin.TooltipHelper.TOOLTIP_ID
import com.airback.vaadin.UserUIContext
import com.airback.vaadin.ui.formatter.HistoryFieldFormat
import com.airback.vaadin.web.ui.WebThemes
import org.slf4j.LoggerFactory

/**
 * @author airback Ltd.
 * @since 4.0
 */
class ProjectMemberHistoryFieldFormat : HistoryFieldFormat {

    override fun toString(value: String): String = toString(UserUIContext.getUser(), value, true, "")

    override fun toString(currentViewUser:SimpleUser, value: String, displayAsHtml: Boolean, msgIfBlank: String): String {
        if (StringUtils.isBlank(value)) {
            return msgIfBlank
        }

        try {
            val userService = AppContextUtil.getSpringBean(UserService::class.java)
            val user = userService.findUserByUserNameInAccount(value, AppUI.accountId)
            if (user != null) {
                return if (displayAsHtml) {
                    val userAvatar = Img("", AppContextUtil.getSpringBean(AbstractStorageService::class.java)
                            .getAvatarPath(user.avatarid, 16)).setCSSClass(WebThemes.CIRCLE_BOX)
                    val link = A().setId("tag" + TOOLTIP_ID).appendText(StringUtils.trim(user.displayName, 30, true))
                    link.setAttribute("onmouseover", TooltipHelper.userHoverJsFunction(user.username))
                    link.setAttribute("onmouseleave", TooltipHelper.itemMouseLeaveJsFunction())
                    DivLessFormatter().appendChild(userAvatar, DivLessFormatter.EMPTY_SPACE, link).write()
                } else user.displayName!!
            }
        } catch (e: Exception) {
            LOG.error("Error", e)
        }

        return value
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(ProjectMemberHistoryFieldFormat::class.java)
    }
}
