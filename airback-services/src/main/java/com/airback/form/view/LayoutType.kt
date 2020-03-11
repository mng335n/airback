package com.airback.form.view

import com.airback.core.airbackException

/**
 * @author airback Ltd
 * @since 7.0.0
 */
enum class LayoutType {
    ONE_COLUMN, TWO_COLUMN;


    companion object {

        fun fromVal(value: Int): LayoutType = when (value) {
            1 -> ONE_COLUMN
            2 -> TWO_COLUMN
            else -> throw airbackException("Do not convert layout type from value $value")
        }

        fun toVal(type: LayoutType): Int = if (ONE_COLUMN == type) 1 else 2
    }
}