package com.services.billingservice.service.placement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.services.billingservice.dto.placement.credittransfer.CreditTransferRequest;
import com.services.billingservice.dto.placement.credittransfer.CreditTransferResponse;
import com.services.billingservice.dto.placement.inquiryaccount.InquiryAccountRequest;
import com.services.billingservice.dto.placement.inquiryaccount.InquiryAccountResponse;
import com.services.billingservice.dto.placement.overbookingcasa.OverbookingCasaRequest;
import com.services.billingservice.dto.placement.overbookingcasa.OverbookingCasaResponse;
import com.services.billingservice.dto.placement.transfersknrtgs.TransferSknRtgsRequest;
import com.services.billingservice.dto.placement.transfersknrtgs.TransferSknRtgsResponse;
import com.services.billingservice.utils.placement.GenerateUniqueKeyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebClientService {

    private static final String BDI_KEY = "BDI-Key";
    private static final String BDI_EXTERNAL_ID = "BDI-External-ID";
    private static final String BDI_TIMESTAMP = "BDI-Timestamp";
    private static final String BDI_CHANNEL = "BDI-Channel";
    private static final String BDI_SERVICE_CODE = "BDI-Service-Code";
    private static final String BDI_SIGNATURE = "BDI-Signature";

    @Value("${mock.server.url}")
    private String mockServerUrl;

    // **General Header
    @Value("${api.secret-key}")
    private String secretKey;
    @Value("${api.bdi-key}")
    private String apiBDIKey;

    // **Inquiry Account Header
    @Value("${api.inquiry-account.bdi-channel}")
    private String inquiryAccountBDIChannel;
    @Value("${api.inquiry-account.bdi-service-code}")
    private String inquiryAccountBDIServiceCode;
    @Value("${api.inquiry-account-url}")
    private String inquiryAccountUrl;

    // **Overbooking Casa Header
    @Value("${api.overbooking-casa.bdi-channel}")
    private String overbookingCasaBDIChannel;
    @Value("${api.overbooking-casa.bdi-service-code}")
    private String overbookingCasaBDIServiceCode;
    @Value("${api.overbooking-csa-url}")
    private String overbookingCasaUrl;

    // **Transfer SKN RGS Header
    @Value("${api.transfer-skn-rtgs.bdi-channel}")
    private String transferSknRtgsBDIChannel;
    @Value("${api.transfer-skn-rtgs.bdi-service-code}")
    private String transferSknRtgsBDIServiceCode;
    @Value("${api.transfer-skn-rtgs-url}")
    private String transferSknRtgsUrl;

    // **Credit Transfer Header
    @Value("${api.credit-transfer.bdi-channel}")
    private String creditTransferBDIChannel;
    @Value("${api.credit-transfer.bdi-service-code}")
    private String creditTransferBDIServiceCode;
    @Value("${api.credit-transfer-url}")
    private String creditTransferUrl;

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Mono<InquiryAccountResponse> inquiryAccount(InquiryAccountRequest inquiryAccountRequest) throws JsonProcessingException, NoSuchAlgorithmException, InvalidKeyException {
        String jsonRequestBody = objectMapper.writeValueAsString(inquiryAccountRequest);
        String processedRequestBody = jsonRequestBody.replaceAll("\\s", "");
        String currentTimestamp = getCurrentTimestamp();
        String dataToSign = currentTimestamp + processedRequestBody;
        String signature = computeHMACSHA512(dataToSign, secretKey);
        String externalId = GenerateUniqueKeyUtil.generateUniqueKeyUUID();

        log.info("[Inquiry Account] URL: {}, BDI-Key: {}, BDI-External-ID: {}, BDI-Channel: {}, BDI-Service-Code: {}, ",
                inquiryAccountUrl, apiBDIKey, externalId, inquiryAccountBDIChannel, inquiryAccountBDIServiceCode);
        log.info("[Inquiry Account] JSON Request body: {}, Processed Request body: {}, Current timestamp: {}, Data to sign: {}, Signature: {}",
                jsonRequestBody, processedRequestBody, currentTimestamp, dataToSign, signature);
        log.info("[Inquiry Account] Data to sign: {}", dataToSign);
        log.info("[Inquiry Account] Signature: {}", signature);

        return webClient.post()
                .uri("http://localhost:8081/inquiry-account-timeout")
                .contentType(MediaType.APPLICATION_JSON)
                .header(BDI_KEY, apiBDIKey)
                .header(BDI_EXTERNAL_ID, externalId)
                .header(BDI_TIMESTAMP, currentTimestamp)
                .header(BDI_CHANNEL, inquiryAccountBDIChannel)
                .header(BDI_SERVICE_CODE, inquiryAccountBDIServiceCode)
                .header(BDI_SIGNATURE, signature)
                .bodyValue(inquiryAccountRequest)
                .exchangeToMono(response -> {
                    response.headers().asHttpHeaders().forEach((key, value) ->
                            log.info("[Inquiry Account] Header Response: {}, = {}", key, value)
                    );
                    log.info("[Inquiry Account] Status Code Response: {}", response.statusCode());

                    return response.bodyToMono(InquiryAccountResponse.class);
                })
                .timeout(Duration.ofSeconds(20)) // ✅ Timeout lebih besar dari WireMock (20 detik)
                .doOnError(e -> log.error("[Inquiry Account] Error occurred: {}", e.getMessage()));
    }

    public Mono<CreditTransferResponse> creditTransferBiFast(CreditTransferRequest creditTransferRequest) throws JsonProcessingException, NoSuchAlgorithmException, InvalidKeyException {
        String jsonRequestBody = objectMapper.writeValueAsString(creditTransferRequest);
        String processedRequestBody = jsonRequestBody.replaceAll("\\s", "");
        String currentTimestamp = getCurrentTimestamp();
        String dataToSign = currentTimestamp + processedRequestBody;
        String signature = computeHMACSHA512(dataToSign, secretKey);
        String externalId = GenerateUniqueKeyUtil.generateUniqueKeyUUID();

        log.info("[Credit Transfer BiFast] URL: {}, BDI-Key: {}, BDI-External-ID: {}, BDI-Channel: {}, BDI-Service-Code: {}, ",
                creditTransferUrl, apiBDIKey, externalId, creditTransferBDIChannel, creditTransferBDIServiceCode);
        log.info("[Credit Transfer BiFast] JSON Request body: {}, Processed Request body: {}, Current timestamp: {}, Data to sign: {}, Signature: {}",
                jsonRequestBody, processedRequestBody, currentTimestamp, dataToSign, signature);
        log.info("[Credit Transfer BiFast] Data to sign: {}", dataToSign);
        log.info("[Credit Transfer BiFast] Signature: {}", signature);

        return webClient.post()
                .uri("http://localhost:8081/credit-transfer-duplicate-ref-number")
                .contentType(MediaType.APPLICATION_JSON)
                .header(BDI_KEY, apiBDIKey)
                .header(BDI_EXTERNAL_ID, "DUPLICATE_REF_12345")
                .header(BDI_TIMESTAMP, currentTimestamp)
                .header(BDI_CHANNEL, creditTransferBDIChannel)
                .header(BDI_SERVICE_CODE, creditTransferBDIServiceCode)
                .header(BDI_SIGNATURE, signature)
                .bodyValue(creditTransferRequest)
                .exchangeToMono(response -> {
                    response.headers().asHttpHeaders().forEach((key, value) ->
                            log.info("[Credit Transfer] Header Response: {}, = {}", key, value)
                    );
                    log.info("[Credit Transfer] Status Code Response: {}", response.statusCode());
                    return response.bodyToMono(CreditTransferResponse.class);
                })
                .timeout(Duration.ofSeconds(20))
                .doOnError(e -> log.error("[Credit Transfer] Error occurred: {}", e.getMessage()))
                ;
    }

    public Mono<OverbookingCasaResponse> overbookingCasa(OverbookingCasaRequest overbookingCasaRequest) throws JsonProcessingException, NoSuchAlgorithmException, InvalidKeyException {
        String jsonRequestBody = objectMapper.writeValueAsString(overbookingCasaRequest);
        String processedRequestBody = jsonRequestBody.replaceAll("\\s", "");
        String currentTimestamp = getCurrentTimestamp();
        String dataToSign = currentTimestamp + processedRequestBody;
        String signature = computeHMACSHA512(dataToSign, secretKey);
        String externalId = GenerateUniqueKeyUtil.generateUniqueKeyUUID();

        log.info("[Overbooking Casa] URL: {}, BDI-Key: {}, BDI-External-ID: {}, BDI-Channel: {}, BDI-Service-Code: {}, ",
                overbookingCasaUrl, apiBDIKey, externalId, overbookingCasaBDIChannel, overbookingCasaBDIServiceCode);
        log.info("[Overbooking Casa] JSON Request body: {}, Processed Request body: {}, Current timestamp: {}, Data to sign: {}, Signature: {}",
                jsonRequestBody, processedRequestBody, currentTimestamp, dataToSign, signature);
        log.info("[Overbooking Casa] Data to sign: {}", dataToSign);
        log.info("[Overbooking Casa] Signature: {}", signature);

        return webClient.post()
                .uri(overbookingCasaUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .header(BDI_KEY, apiBDIKey)
                .header(BDI_EXTERNAL_ID, externalId)
                .header(BDI_TIMESTAMP, currentTimestamp)
                .header(BDI_CHANNEL, overbookingCasaBDIChannel)
                .header(BDI_SERVICE_CODE, overbookingCasaBDIServiceCode)
                .header(BDI_SIGNATURE, signature)
                .bodyValue(overbookingCasaRequest)
                .exchangeToMono(response -> {
                    response.headers().asHttpHeaders().forEach((key, value) ->
                            log.info("[Overbooking Casa] Header Response: {}, = {}", key, value)
                    );
                    log.info("[Overbooking Casa] Status Code Response: {}", response.statusCode());
                    return response.bodyToMono(OverbookingCasaResponse.class);
                });
    }

    public Mono<TransferSknRtgsResponse> transferSknRtgs(TransferSknRtgsRequest transferSknRtgsRequest) throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        String jsonRequestBody = objectMapper.writeValueAsString(transferSknRtgsRequest);
        String processedRequestBody = jsonRequestBody.replaceAll("\\s", "");
        String currentTimestamp = getCurrentTimestamp();
        String dataToSign = currentTimestamp + processedRequestBody;
        String signature = computeHMACSHA512(dataToSign, secretKey);
        String externalId = GenerateUniqueKeyUtil.generateUniqueKeyUUID();

        log.info("[Transfer SKN RTGS] URL: {}, BDI-Key: {}, BDI-External-ID: {}, BDI-Channel: {}, BDI-Service-Code: {}, ",
                transferSknRtgsUrl, apiBDIKey, externalId, transferSknRtgsBDIChannel, transferSknRtgsBDIServiceCode);
        log.info("[Transfer SKN RTGS] JSON Request body: {}, Processed Request body: {}, Current timestamp: {}, Data to sign: {}, Signature: {}",
                jsonRequestBody, processedRequestBody, currentTimestamp, dataToSign, signature);
        log.info("[Transfer SKN RTGS] Data to sign: {}", dataToSign);
        log.info("[Transfer SKN RTGS] Signature: {}", signature);

        return webClient.post()
                .uri(transferSknRtgsUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .header(BDI_KEY, apiBDIKey)
                .header(BDI_EXTERNAL_ID, externalId)
                .header(BDI_TIMESTAMP, currentTimestamp)
                .header(BDI_CHANNEL, transferSknRtgsBDIChannel)
                .header(BDI_SERVICE_CODE, transferSknRtgsBDIServiceCode)
                .header(BDI_SIGNATURE, signature)
                .bodyValue(transferSknRtgsRequest)
                .exchangeToMono(response -> {
                    response.headers().asHttpHeaders().forEach((key, value) ->
                            log.info("[Transfer SKN RTGS] Header Response: {}, = {}", key, value)
                    );
                    log.info("[Transfer SKN RTGS] Status Code Response: {}", response.statusCode());
                    return response.bodyToMono(TransferSknRtgsResponse.class);
                });
    }



    public static String getCurrentTimestamp() {
        String currentTimestamp = ZonedDateTime.now(ZoneId.of("Asia/Jakarta"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        log.info("Get current timestamp: {}", currentTimestamp);
        return currentTimestamp;
    }

    public static String computeHMACSHA512(String data, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        mac.init(secretKeySpec);
        byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hmacBytes);
    }

}
