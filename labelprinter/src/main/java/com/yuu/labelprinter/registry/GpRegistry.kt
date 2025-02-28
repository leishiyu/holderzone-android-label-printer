package com.yuu.labelprinter.registry

import kotlinx.coroutines.flow.Flow

/**
 * @author Created by leisiyu
 * @email:
 * @Date: 2025/2/26
 * @Project: com.yuu.labelprinter.registry
 * @Description:
 * @Version:
 */
interface GpRegistry {
    /**
     * 将USB设备注册到GpLink
     */
    fun register(): Flow<Int>
}