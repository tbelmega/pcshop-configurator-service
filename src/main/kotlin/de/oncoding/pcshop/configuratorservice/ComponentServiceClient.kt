package de.oncoding.pcshop.configuratorservice

import org.springframework.web.client.RestTemplate

class ComponentServiceClient(
        private val restTemplate: RestTemplate,
        private val baseUrl: String) {

    fun getCpuCoolersForSocket(socket: String): Set<CpuCooler> {
        val url = "$baseUrl/api/v1/cpucoolers?socket=$socket"
        val response =
                restTemplate.getForEntity(
                        url,
                        Array<CpuCooler>::class.java
                )
        return response.body?.toSet()
                ?: throw Exception()
    }
}

data class CpuCooler(
        val id: String,
        val model: String,
        val manufacturer: String,
        val supportedCpuSockets: Set<String>,
        val airFlowInCFM: CFM
)
data class CFM (val cfm: Float)

