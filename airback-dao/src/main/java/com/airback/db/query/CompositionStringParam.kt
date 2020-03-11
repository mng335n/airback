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
package com.airback.db.query

import com.airback.db.arguments.CompositionSearchField
import com.airback.db.arguments.SearchField

/**
 * @author airback Ltd.
 * @since 4.0
 */
class CompositionStringParam(id: String, vararg val params: StringParam) : Param(id) {

    fun buildSearchField(prefixOper: String, compareOper: String, value: String): SearchField {
        val searchField = CompositionSearchField(prefixOper)
        params.map { it.buildSearchField("", compareOper, value) }
                .forEach { searchField.addField(it) }
        return searchField
    }
}
