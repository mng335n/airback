package com.airback.common.dao

import com.airback.common.domain.Client
import com.airback.common.domain.SimpleClient
import com.airback.common.domain.criteria.ClientSearchCriteria
import com.airback.db.persistence.IMassUpdateDAO
import com.airback.db.persistence.ISearchableDAO

interface ClientMapperExt : ISearchableDAO<ClientSearchCriteria>, IMassUpdateDAO<Client, ClientSearchCriteria> {

    fun findById(accountId: Int): SimpleClient?
}