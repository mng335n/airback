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
package com.airback.vaadin.web.ui;

import com.airback.common.UrlEncodeDecoder;
import com.airback.common.json.QueryAnalyzer;
import com.airback.db.arguments.SearchCriteria;
import com.airback.db.query.SearchFieldInfo;
import com.airback.core.utils.StringUtils;
import com.airback.vaadin.ApplicationEventListener;
import com.airback.shell.event.ShellEvent;
import com.google.common.eventbus.Subscribe;
import com.vaadin.server.Page;

import java.util.List;

/**
 * @author airback Ltd
 * @since 5.3.2
 */
public class QueryParamHandler {
    public static <S extends SearchCriteria> ApplicationEventListener<ShellEvent.AddQueryParam> queryParamHandler() {
        return new ApplicationEventListener<ShellEvent.AddQueryParam>() {
            @Subscribe
            @Override
            public void handle(ShellEvent.AddQueryParam event) {
                List<SearchFieldInfo<S>> searchFieldInfos = (List<SearchFieldInfo<S>>) event.getData();
                String query = QueryAnalyzer.toQueryParams(searchFieldInfos);
                String fragment = Page.getCurrent().getUriFragment();
                int index = fragment.indexOf("?");
                if (index > 0) {
                    fragment = fragment.substring(0, index);
                }

                if (StringUtils.isNotBlank(query)) {
                    fragment += "?" + UrlEncodeDecoder.encode(query);
                    Page.getCurrent().setUriFragment(fragment, false);
                }
            }
        };
    }
}
