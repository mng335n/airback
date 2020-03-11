/**
 * Copyright © airback
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.airback.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Controller;

/**
 * @author airback Ltd.
 * @since 4.6.0
 */
@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = {
        "com.airback.aspect",
        "com.airback.**.esb",
        "com.airback.**.service",
        "com.airback.schedule",
        "com.airback.spring",
        "com.airback.vaadin.ui.registry",
        "com.airback.**.configuration"},
        excludeFilters = {@ComponentScan.Filter(classes = {Controller.class})})
public class RootConfiguration {
}
