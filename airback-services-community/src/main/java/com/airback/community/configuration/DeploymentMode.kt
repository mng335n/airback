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
package com.airback.community.configuration

import com.airback.configuration.IDeploymentMode
import com.airback.configuration.ServerConfiguration
import com.airback.configuration.SiteConfiguration
import com.airback.db.persistence.service.IService
import org.springframework.context.annotation.Profile
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

/**
 * @author airback Ltd
 * @since 5.2.2
 */
@Component
@Order(value = 1)
class DeploymentMode(private val serverConfiguration: ServerConfiguration) : IDeploymentMode, IService {

    override val isDemandEdition: Boolean
        get() = false

    override val isCommunityEdition: Boolean
        get() = true

    override val isPremiumEdition: Boolean
        get() = false

    override fun getSiteUrl(subDomain: String?) = String.format(serverConfiguration.siteUrl, serverConfiguration.address, serverConfiguration.port)

    override fun getResourceDownloadUrl() = String.format(serverConfiguration.resourceDownloadUrl, serverConfiguration.address, serverConfiguration.port)

    override fun getCdnUrl() = String.format(serverConfiguration.cdnUrl, serverConfiguration.address, serverConfiguration.port)
}
