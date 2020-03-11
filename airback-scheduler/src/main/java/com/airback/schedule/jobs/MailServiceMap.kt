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
package com.airback.schedule.jobs

import com.airback.core.airbackException
import com.airback.module.project.ProjectTypeConstants
import com.airback.schedule.email.SendingRelayEmailNotificationAction
import com.airback.schedule.email.project.*

/**
 * @author airback Ltd
 * @since 6.0.0
 */
object MailServiceMap {
    private val serviceMap2 = mapOf(
            ProjectTypeConstants.BUG to BugRelayEmailNotificationAction::class.java,
            ProjectTypeConstants.COMPONENT to ComponentRelayEmailNotificationAction::class.java,
            ProjectTypeConstants.VERSION to VersionRelayEmailNotificationAction::class.java,
            ProjectTypeConstants.MESSAGE to MessageRelayEmailNotificationAction::class.java,
            ProjectTypeConstants.MILESTONE to ProjectMilestoneRelayEmailNotificationAction::class.java,
            ProjectTypeConstants.PAGE to ProjectPageRelayEmailNotificationAction::class.java,
            ProjectTypeConstants.PROJECT to ProjectRelayEmailNotificationAction::class.java,
            ProjectTypeConstants.RISK to ProjectRiskRelayEmailNotificationAction::class.java,
            ProjectTypeConstants.STANDUP to StandupRelayEmailNotificationAction::class.java,
            ProjectTypeConstants.TASK to ProjectTaskRelayEmailNotificationAction::class.java)

    fun service(typeVal: String): Class<out SendingRelayEmailNotificationAction> =
            serviceMap2[typeVal] ?: throw  airbackException("Can not find associate email action for $typeVal")

}