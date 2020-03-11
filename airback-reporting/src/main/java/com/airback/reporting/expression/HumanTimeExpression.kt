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
package com.airback.reporting.expression

import com.airback.core.utils.HumanTime
import net.sf.dynamicreports.report.definition.ReportParameters

/**
 * @author airback Ltd
 * @since 5.1.4
 */
class HumanTimeExpression(field: String) : SimpleFieldExpression<String>(field) {

    override fun evaluate(reportParameters: ReportParameters): String {
        val longValue = reportParameters.getFieldValue<Double>(field)
        return if (longValue != null) {
            val milis = longValue * 1000.0 * 60.0 * 60.0
            HumanTime.exactly(milis.toLong())
        } else "O d"
    }
}
