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
 * along with this program.  If not, see <http:></http:>//www.gnu.org/licenses/>.
 */
package com.airback.community.module.user.service.impl

import com.airback.core.UnsupportedFeatureException
import com.airback.core.cache.CacheKey
import com.airback.db.persistence.ICrudGenericDAO
import com.airback.db.persistence.service.DefaultCrudService
import com.airback.module.user.dao.AccountThemeMapper
import com.airback.module.user.domain.AccountTheme
import com.airback.module.user.domain.AccountThemeExample
import com.airback.module.user.service.AccountThemeService
import org.springframework.stereotype.Service

/**
 * @author airback Ltd.
 * @since 4.1
 */
@Service
class AccountThemeServiceImpl(private val accountThemeMapper: AccountThemeMapper) : DefaultCrudService<Int, AccountTheme>(), AccountThemeService {

    override val crudMapper: ICrudGenericDAO<Int, AccountTheme>
        get() = accountThemeMapper as ICrudGenericDAO<Int, AccountTheme>

    override fun findTheme(@CacheKey sAccountId: Int): AccountTheme? = null

    override fun findDefaultTheme(@CacheKey sAccountId: Int): AccountTheme? {
        val ex = AccountThemeExample()
        ex.createCriteria().andIsdefaultEqualTo(java.lang.Boolean.TRUE)
        val accountThemes = accountThemeMapper.selectByExample(ex)
        return if (accountThemes.size > 0) {
            accountThemes[0]
        } else null

    }

    override fun removeTheme(@CacheKey sAccountId: Int) {
        throw UnsupportedFeatureException("Do not support this feature in the community edition")
    }
}
