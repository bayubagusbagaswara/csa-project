package com.services.billingservice.service.placement.mock;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
@Slf4j
public class MockWireService {

    private WireMockServer wireMockServer;

    @PostConstruct
    public void startMockServer() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(8081));
        wireMockServer.start();

        // **1. Inquiry Account Success /
        wireMockServer.stubFor(WireMock.post("/inquiry-account/success")
                .willReturn(WireMock.aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                        .withBody("{"
                                + "\"responseCode\": \"200R000000\","
                                + "\"responseMessage\": \"Successful\","
                                + "\"subStatusProvider\": {"
                                + "    \"providerSystem\": \"CIHUB\","
                                + "    \"statusCode\": \"U000\","
                                + "    \"statusMessage\": \"null\""
                                + "},"
                                + "\"data\": {"
                                + "    \"sttlmDate\": \"2025-03-05\","
                                + "    \"benefAcctName\": \"BIFAST 2\","
                                + "    \"benefAcctNo\": \"703169938200\","
                                + "    \"benefAcctType\": \"SVGS\","
                                + "    \"benefType\": \"01\","
                                + "    \"benefId\": \"3023122312345\","
                                + "    \"benefResidentStatus\": \"null\","
                                + "    \"benefCityCode\": \"0294\","
                                + "    \"userRefNoBi\": \"20250305BDINIDJA510O9900197041\""
                                + "}"
                                + "}")));

        // **2. Inquiry Account Timeout (delay 10 second)
        wireMockServer.stubFor(WireMock.post("/inquiry-account-timeout")
                .willReturn(WireMock.aResponse()
                        .withFixedDelay(10_000) // Delay 10 detik (10_000 ms)
                        .withStatus(504) // Gateway Timeout
                        .withHeader("Content-Type", "application/json")
                        .withBody("{"
                                + "\"responseCode\": \"504R006001\","
                                + "\"responseMessage\": \"Online Authorization Exception Timeout\","
                                + "\"subStatusProvider\": {"
                                + "    \"providerSystem\": \"CIHUB\","
                                + "    \"statusCode\": \"U173\","
                                + "    \"statusMessage\": \"null\""
                                + "},"
                                + "\"data\": {"
                                + "    \"sttlmDate\": \"2025-03-06\","
                                + "    \"benefAcctName\": \"null\","
                                + "    \"benefAcctNo\": \"58982978\","
                                + "    \"benefAcctType\": \"null\","
                                + "    \"benefType\": \"null\","
                                + "    \"benefId\": \"null\","
                                + "    \"benefResidentStatus\": \"null\","
                                + "    \"benefCityCode\": \"null\","
                                + "    \"userRefNoBi\": \"20250306BDINIDJA510O9900197072\""
                                + "}"
                                + "}")));


        // **3 Credit Transfer BI-FAST Success
        wireMockServer.stubFor(WireMock.post("/credit-transfer-success")
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{"
                                + "\"responseCode\": \"200R000000\","
                                + "\"responseMessage\": \"Successful\","
                                + "\"subStatusProvider\": {"
                                + "    \"providerSystem\": \"CIHUB\","
                                + "    \"statusCode\": \"U000\","
                                + "    \"statusMessage\": \"null\""
                                + "},"
                                + "\"data\": {"
                                + "    \"trxType\": \"CREDIT_TRANSFER\","
                                + "    \"payUserRefNo\": \"20250305BDINIDJA010O0100922024\","
                                + "    \"infoStatus\": \"null\","
                                + "    \"sttlmDate\": \"2025-03-05\","
                                + "    \"benefName\": \"BIFAST 2\","
                                + "    \"benefType\": \"null\","
                                + "    \"benefId\": \"3023122312345\","
                                + "    \"benefResidentStatus\": \"01\","
                                + "    \"benefCityCode\": \"0294\","
                                + "    \"cardNo\": \"\","
                                + "    \"feeAmt\": \"2500.00\","
                                + "    \"chargeBearerCode\": \"DEBT\","
                                + "    \"userRefNoBi\": \"20250305BDINIDJA510O9900197041\""
                                + "}"
                                + "}")));

        // **4: Credit Transfer Duplicate Ref Number
        wireMockServer.stubFor(WireMock.post("/credit-transfer-duplicate-ref-number")
                .withHeader("BDI-External-ID", WireMock.equalTo("DUPLICATE_REF_12345"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{"
                                + "\"responseCode\": \"403R000000\","
                                + "\"responseMessage\": \"Duplicate Ref Number\","
                                + "\"subStatusProvider\": {"
                                + "    \"providerSystem\": \"CIHUB\","
                                + "    \"statusCode\": \"U000\","
                                + "    \"statusMessage\": \"null\""
                                + "},"
                                + "\"data\": {"
                                + "    \"trxType\": \"CREDIT_TRANSFER\","
                                + "    \"payUserRefNo\": \"20250305BDINIDJA010O0100922024\","
                                + "    \"infoStatus\": \"null\","
                                + "    \"sttlmDate\": \"2025-03-05\","
                                + "    \"benefName\": \"BIFAST 2\","
                                + "    \"benefType\": \"null\","
                                + "    \"benefId\": \"3023122312345\","
                                + "    \"benefResidentStatus\": \"01\","
                                + "    \"benefCityCode\": \"0294\","
                                + "    \"cardNo\": \"\","
                                + "    \"feeAmt\": \"2500.00\","
                                + "    \"chargeBearerCode\": \"DEBT\","
                                + "    \"userRefNoBi\": \"20250305BDINIDJA510O9900197041\""
                                + "}"
                                + "}")));


        log.info("WireMock Server running at: http://localhost:8081");
    }

    public String getMockUrl() {
        return "http://localhost:8081";
    }

    @PreDestroy
    public void stopMockServer() {
        wireMockServer.stop();
        log.info("WireMock Server stopped.");
    }

}
