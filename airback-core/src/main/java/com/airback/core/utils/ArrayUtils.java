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
package com.airback.core.utils;

import com.airback.core.airbackException;
import org.apache.commons.beanutils.PropertyUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author airback Ltd
 * @since 5.0.9
 */
public class ArrayUtils {
    public static <T> boolean isNotEmpty(T[] array) {
        return array != null && array.length != 0;
    }

    public static List<Integer> extractIds(List items) {
        try {
            List<Integer> keys = new ArrayList<>(items.size());
            for (Object item : items) {
                Integer key = (Integer) PropertyUtils.getProperty(item, "id");
                keys.add(key);
            }
            return keys;
        } catch (Exception e) {
            throw new airbackException(e);
        }
    }
}
