package de.oncoding.pcshop.configuratorservice

import au.com.dius.pact.consumer.Pact
import au.com.dius.pact.consumer.PactProviderRuleMk2
import au.com.dius.pact.consumer.PactVerification
import au.com.dius.pact.consumer.dsl.*
import au.com.dius.pact.model.RequestResponsePact
import bodyWithCollectionOfObjectsLike
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestTemplate

class ComponentServiceClientTest {

    @Rule
    @JvmField
    val mockProvider = PactProviderRuleMk2(
            "component-service",
            "localhost",
            8080,
            this
    )

    @Pact(provider = "component-service", consumer = "configurator-service")
    fun getPcCoolers_noCoolers(
            builder: PactDslWithProvider
    ): RequestResponsePact {
        return builder
                .given("no coolers in the database")
                .uponReceiving("a request to get all am4 compatible coolers (empty)")
                .path("/api/v1/cpucoolers")
                .query("socket=am4")
                .method(HttpMethod.GET.name)
                .willRespondWith()
                .body("[]")
                .headers(mapOf("Content-Type" to "application/json;charset=UTF-8"))
                .status(HttpStatus.OK.value())
                .toPact()
    }

    @Test
    @PactVerification(
            value = ["component-service"],
            fragment = "getPcCoolers_noCoolers")
    fun `get cpu coolers - 200 OK - empty set`() {
        // given
        val expectedResponse = emptySet<CpuCooler>()

        // when
        val result = ComponentServiceClient(
                RestTemplate(),
                mockProvider.url
        ).getCpuCoolersForSocket("am4")

        //then
        assertThat(result).isEqualTo(expectedResponse)
    }

    @Pact(provider = "component-service", consumer = "configurator-service")
    fun getPcCoolers_coolers_json(builder: PactDslWithProvider): RequestResponsePact {
        return builder
                .given("coolers in the database")
                .uponReceiving("a request to get all am4 compatible coolers (json)")
                .path("/api/v1/cpucoolers")
                .query("socket=am4")
                .method(HttpMethod.GET.name)
                .willRespondWith()
                .body("""[{
                    |"id":"12345",
                    |"model":"MasterAir Pro 4",
                    |"manufacturer":"Cooler Master",
                    |"supportedCpuSockets":["AM4"],
                    |"airFlowInCFM":{"cfm":66.7}
                    |}]""".trimMargin())
                .headers(mapOf("Content-Type" to "application/json;charset=UTF-8"))
                .status(HttpStatus.OK.value())
                .toPact()
    }

    @Test
    @PactVerification(
            value = ["component-service"],
            fragment = "getPcCoolers_coolers_json"
    )
    fun `get cpu coolers - 200 OK - json response`() {
        // given
        val expectedResponse = setOf(CpuCooler(
                id = "12345",
                supportedCpuSockets = setOf("AM4"),
                manufacturer = "Cooler Master",
                model = "MasterAir Pro 4",
                airFlowInCFM = CFM(66.7f)
        ))

        // when
        val result = ComponentServiceClient(
                RestTemplate(),
                mockProvider.url
        ).getCpuCoolersForSocket("am4")

        assertThat(result).isEqualTo(expectedResponse)
    }

    @Pact(provider = "component-service", consumer = "configurator-service")
    fun getPcCoolers_coolers_dsl(builder: PactDslWithProvider): RequestResponsePact {
        return builder
                .given("coolers in the database")
                .uponReceiving("a request to get all am4 compatible coolers (dsl)")
                .path("/api/v1/cpucoolers")
                .query("socket=am4")
                .method(HttpMethod.GET.name)
                .willRespondWith()
                .body(PactDslJsonArray.arrayMinLike(1)
                        .stringType("id", "12345")
                        .stringType("model", "MasterAir Pro 4")
                        .stringType("manufacturer", "Cooler Master")
                        .supportedCpuSockets("AM4")
                        .airFlow(66.7)
                        .closeObject())
                .headers(mapOf("Content-Type" to "application/json;charset=UTF-8"))
                .status(HttpStatus.OK.value())
                .toPact()
    }

    fun DslPart.airFlow(cfm: Number): DslPart {
        return this
                .`object`("airFlowInCFM")
                .numberType("cfm", cfm)
                .closeObject()
    }

    fun PactDslJsonBody.supportedCpuSockets(socket: String): DslPart {
        return this
                .minArrayLike(
                        "supportedCpuSockets",
                        0,
                        PactDslJsonRootValue.stringType(socket),
                        2)
    }

    @Test
    @PactVerification(value = ["component-service"], fragment = "getPcCoolers_coolers_dsl")
    fun `get cpu coolers - 200 OK - dsl response`() {
        // given
        val expectedResponse = setOf(CpuCooler(
                id = "12345",
                supportedCpuSockets = setOf("AM4"),
                manufacturer = "Cooler Master",
                model = "MasterAir Pro 4",
                airFlowInCFM = CFM(66.7f)
        ))

        // when
        val result = ComponentServiceClient(
                RestTemplate(),
                mockProvider.url
        ).getCpuCoolersForSocket("am4")

        assertThat(result).isEqualTo(expectedResponse)
    }

    @Pact(provider = "component-service", consumer = "configurator-service")
    fun getPcCoolers_pactUtils(builder: PactDslWithProvider): RequestResponsePact {

        return builder
                .given("coolers in the database")
                .uponReceiving("a request to get all am4 compatible coolers (pact utils)")
                .path("/api/v1/cpucoolers")
                .query("socket=am4")
                .method(HttpMethod.GET.name)
                .willRespondWith()
                .bodyWithCollectionOfObjectsLike(CpuCooler(
                        id = "12345",
                        supportedCpuSockets = setOf("AM4"),
                        manufacturer = "Cooler Master",
                        model = "MasterAir Pro 4",
                        airFlowInCFM = CFM(66.7f)
                ))
                .headers(mapOf("Content-Type" to "application/json;charset=UTF-8"))
                .status(HttpStatus.OK.value())
                .toPact()
    }

    @Test
    @PactVerification(value = ["component-service"], fragment = "getPcCoolers_pactUtils")
    fun `get cpu coolers - 200 OK - pact utils response`() {
        // given
        val expectedResponse = setOf(CpuCooler(
                id = "12345",
                supportedCpuSockets = setOf("AM4"),
                manufacturer = "Cooler Master",
                model = "MasterAir Pro 4",
                airFlowInCFM = CFM(66.7f)
        ))

        // when
        val result = ComponentServiceClient(
                RestTemplate(),
                mockProvider.url
        ).getCpuCoolersForSocket("am4")

        assertThat(result).isEqualTo(expectedResponse)
    }
}
