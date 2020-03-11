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
 * along with this program.  If not, see <http:></http:>//www.gnu.org/licenses/>.
 */
package com.airback.common.service.impl

import com.airback.common.dao.DriveInfoMapper
import com.airback.common.domain.DriveInfo
import com.airback.common.domain.DriveInfoExample
import com.airback.common.service.DriveInfoService
import com.airback.concurrent.DistributionLockUtil
import com.airback.core.cache.CacheKey
import com.airback.core.utils.BeanUtility
import com.airback.db.persistence.ICrudGenericDAO
import com.airback.db.persistence.service.DefaultCrudService
import org.apache.commons.collections.CollectionUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class DriveInfoServiceImpl(private val driveInfoMapper: DriveInfoMapper) : DefaultCrudService<Int, DriveInfo>(), DriveInfoService {

    override val crudMapper: ICrudGenericDAO<Int, DriveInfo>
        get() = driveInfoMapper as ICrudGenericDAO<Int, DriveInfo>

    override fun saveOrUpdateDriveInfo(@CacheKey driveInfo: DriveInfo) {
        val sAccountId = driveInfo.saccountid
        val ex = DriveInfoExample()
        ex.createCriteria().andSaccountidEqualTo(sAccountId)
        val lock = DistributionLockUtil.getLock("ecm-service$sAccountId")
        try {
            if (lock.tryLock(15, TimeUnit.SECONDS)) {
                if (driveInfoMapper.countByExample(ex) > 0) {
                    driveInfo.id = null
                    driveInfoMapper.updateByExampleSelective(driveInfo, ex)
                } else {
                    driveInfoMapper.insert(driveInfo)
                }
            }
        } catch (e: Exception) {
            LOG.error("Error while save drive info ${BeanUtility.printBeanObj(driveInfo)}", e)
        } finally {
            DistributionLockUtil.removeLock("ecm-service$sAccountId")
            lock.unlock()
        }
    }

    override fun getDriveInfo(@CacheKey sAccountId: Int?): DriveInfo {
        val ex = DriveInfoExample()
        ex.createCriteria().andSaccountidEqualTo(sAccountId)
        val driveInfos = driveInfoMapper.selectByExample(ex)
        if (CollectionUtils.isNotEmpty(driveInfos)) {
            return driveInfos[0]
        } else {
            val driveInfo = DriveInfo()
            driveInfo.usedvolume = 0L
            driveInfo.saccountid = sAccountId
            return driveInfo
        }
    }

    override fun getUsedStorageVolume(@CacheKey sAccountId: Int?): Long? {
        val driveInfo = getDriveInfo(sAccountId)
        return if (driveInfo.usedvolume == null) java.lang.Long.valueOf(0L) else driveInfo.usedvolume
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(DriveInfoServiceImpl::class.java)
    }
}
