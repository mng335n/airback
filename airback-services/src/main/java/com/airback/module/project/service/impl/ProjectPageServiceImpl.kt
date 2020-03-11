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
package com.airback.module.project.service.impl

import com.airback.common.ActivityStreamConstants
import com.airback.common.ModuleNameConstants
import com.airback.common.domain.ActivityStreamWithBLOBs
import com.airback.common.service.ActivityStreamService
import com.airback.module.page.domain.Page
import com.airback.module.page.service.PageService
import com.airback.module.project.ProjectTypeConstants
import com.airback.module.project.service.ProjectPageService
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ProjectPageServiceImpl(private val pageService: PageService,
                             private val activityStreamService: ActivityStreamService) : ProjectPageService {

    override fun savePage(page: Page, createdUser: String, projectId: Int, accountId: Int) {
        pageService.savePage(page, createdUser)

        val activityStream = ActivityStreamWithBLOBs()
        activityStream.action = ActivityStreamConstants.ACTION_CREATE
        activityStream.createduser = createdUser
        activityStream.createdtime = LocalDateTime.now()
        activityStream.module = ModuleNameConstants.PRJ
        activityStream.namefield = page.subject
        activityStream.saccountid = accountId
        activityStream.type = ProjectTypeConstants.PAGE
        activityStream.typeid = page.path
        activityStream.extratypeid = projectId
        activityStreamService.save(activityStream)
    }

    override fun getPage(path: String, requestedUser: String) = pageService.getPage(path, requestedUser)
}