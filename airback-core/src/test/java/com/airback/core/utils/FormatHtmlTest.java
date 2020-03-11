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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author airback Ltd
 * @since 5.0.8
 */
public class FormatHtmlTest {
    @Test
    public void testFormatHtml() {
        String mixTextAndHtml = StringUtils.formatRichText("Hello world https://community.airback.com <b>Anh Minh</b>");
        Assertions.assertEquals("Hello world \n" +
                "<a href=\"https://community.airback.com\" target=\"_blank\">https://community.airback.com</a> \n" +
                "<b>Anh Minh</b>", mixTextAndHtml);

        String pureHtml = StringUtils.formatRichText("https://airback.com");
        Assertions.assertEquals("<a href=\"https://airback.com\" target=\"_blank\">https://airback.com</a>", pureHtml);
    }
}
