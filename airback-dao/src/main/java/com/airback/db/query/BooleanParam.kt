package com.airback.db.query

import com.airback.core.airbackException
import com.airback.db.arguments.OneValueSearchField
import com.airback.db.arguments.SearchField
import com.airback.common.i18n.QueryI18nEnum.*

/**
 * @author airback Ltd
 * @since 6.0.0
 */
class BooleanParam(id: String, table: String, column: String) : ColumnParam(id, table, column) {
    fun buildSearchField(prefixOper: String, compareOper: String, value: String): SearchField {
        val compareValue = valueOf(compareOper)
        return when (compareValue) {
            IS -> OneValueSearchField(prefixOper, "$table.$column = ", convertValueToBoolean(value))
            else -> throw airbackException("Not support yet")
        }
    }
    companion object {

        @JvmField
        val OPTIONS = arrayOf(IS)

        fun convertValueToBoolean(value: String): Int = if (value == "ACTION_YES" || value == "true") 1 else 0
    }
}