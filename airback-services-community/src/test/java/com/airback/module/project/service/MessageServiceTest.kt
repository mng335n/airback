package com.airback.module.project.service

import com.airback.db.arguments.BasicSearchRequest
import com.airback.db.arguments.SetSearchField
import com.airback.module.project.domain.criteria.MessageSearchCriteria
import com.airback.test.DataSet
import com.airback.test.rule.DbUnitInitializerRule
import com.airback.test.spring.IntegrationServiceTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired

@ExtendWith(DbUnitInitializerRule::class)
class MessageServiceTest(@Autowired val messageService: MessageService) : IntegrationServiceTest() {

    @Test
    @DataSet
    fun testFindMessages() {
        val criteria = MessageSearchCriteria()
        criteria.projectIds = SetSearchField(1, 2)
        assertThat(messageService.getTotalCount(criteria)).isEqualTo(3)
        assertThat(messageService.findPageableListByCriteria(BasicSearchRequest(criteria)).size).isEqualTo(3)
    }
}