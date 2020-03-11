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
package com.airback.test.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;

/**
 * @author airback Ltd.
 * @since 4.6.0
 */
@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = {"com.airback.**.service", "com.airback.**.spring",
        "com.airback.**.jobs", "com.airback.**.aspect", "com.airback.**.esb"},
        excludeFilters = {@ComponentScan.Filter(classes = {Controller.class})})
@Profile("test")
public class RootConfigurationTest {

}
