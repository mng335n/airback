package com.airback.common.service

import com.airback.common.domain.Client
import com.airback.common.domain.SimpleClient
import com.airback.common.domain.criteria.ClientSearchCriteria
import com.airback.core.cache.CacheKey
import com.airback.core.cache.Cacheable
import com.airback.db.persistence.service.IDefaultService

/**
 * @author airback Ltd
 * @since 7.0.0
 */
interface ClientService : IDefaultService<Int, Client, ClientSearchCriteria> {

    @Cacheable
    fun findById(id: Int, @CacheKey sAccountId: Int): SimpleClient?
}