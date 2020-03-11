package com.airback.common.service.impl

import com.airback.aspect.ClassInfo
import com.airback.aspect.ClassInfoMap
import com.airback.aspect.Traceable
import com.airback.common.ModuleNameConstants
import com.airback.common.dao.ClientMapper
import com.airback.common.dao.ClientMapperExt
import com.airback.common.domain.Client
import com.airback.common.domain.SimpleClient
import com.airback.common.domain.criteria.ClientSearchCriteria
import com.airback.common.service.ClientService
import com.airback.db.persistence.ICrudGenericDAO
import com.airback.db.persistence.ISearchableDAO
import com.airback.db.persistence.service.DefaultService
import com.airback.module.project.ProjectTypeConstants
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
@Traceable(nameField = "name")
class ClientServiceImpl(private val accountMapper: ClientMapper,
                        private val accountMapperExt: ClientMapperExt) : DefaultService<Int, Client, ClientSearchCriteria>(), ClientService {

    override val crudMapper: ICrudGenericDAO<Int, Client>
        get() = accountMapper as ICrudGenericDAO<Int, Client>

    override val searchMapper: ISearchableDAO<ClientSearchCriteria>
        get() = accountMapperExt

    override fun findById(id: Int, sAccountId: Int): SimpleClient? = accountMapperExt.findById(id)

    companion object {
        init {
            ClassInfoMap.put(ClientServiceImpl::class.java, ClassInfo(ModuleNameConstants.PRJ, ProjectTypeConstants.CLIENT))
        }
    }
}