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
package com.airback.vaadin.ui.formatter

import com.hp.gagawa.java.elements.Span
import com.airback.common.i18n.GenericI18Enum
import com.airback.core.utils.DateTimeUtils
import com.airback.core.utils.StringUtils
import com.airback.core.utils.TimezoneVal
import com.airback.module.user.domain.SimpleUser
import com.airback.vaadin.UserUIContext

/**
 * @author airback Ltd
 * @since 5.3.4
 */
class PrettyDateTimeHistoryFieldFormat : HistoryFieldFormat {

    override fun toString(value: String): String =
            toString(UserUIContext.getUser(), value, true, UserUIContext.getMessage(GenericI18Enum.FORM_EMPTY))

    override fun toString(currentViewUser: SimpleUser, value: String, displayAsHtml: Boolean, msgIfBlank: String): String =
            if (StringUtils.isNotBlank(value)) {
                val formatDate = DateTimeUtils.parseDateTimeWithMilisByW3C(value)
                if (displayAsHtml) {
                    val lbl = Span().appendText(DateTimeUtils.getPrettyDateValue(formatDate, TimezoneVal.valueOf(currentViewUser.timezone), currentViewUser.locale))
                    lbl.title = value
                    lbl.write()
                } else {
                    UserUIContext.formatDateTime(formatDate)
                }
            } else {
                msgIfBlank
            }
}
