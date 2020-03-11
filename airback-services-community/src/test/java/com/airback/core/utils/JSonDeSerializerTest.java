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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.airback.security.PermissionMap;

public class JSonDeSerializerTest {
	@Test
	public void testSerializeArray() {
		String[][] twoArr = {{"Anh", "Minh"}, {"airback", "airback"}};
		String json = JsonDeSerializer.toJson(twoArr);

		String[][] newVal = JsonDeSerializer.fromJson(json, String[][].class);
		assertThat(newVal.length).isEqualTo(2);
		assertThat(newVal[0][0]).isEqualTo("Anh");

	}

	@Test
	public void testSerializePermissionMap() {
		PermissionMap map = new PermissionMap();
		map.addPath("a", 1);
		map.addPath("b", 2);

		String json = JsonDeSerializer.toJson(map);

		PermissionMap permissionMap = JsonDeSerializer.fromJson(json, PermissionMap.class);
		assertThat(permissionMap.get("a")).isEqualTo(new Integer(1));
	}
}
