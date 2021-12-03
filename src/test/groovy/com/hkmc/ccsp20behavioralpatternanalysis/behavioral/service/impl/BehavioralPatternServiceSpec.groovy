cccpackage com.hkmc.ccsp20behavioralpatternanalysis.behavioral.service.impl

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.hkmc.behavioralpatternanalysis.external.client.DspServerBLClient
import com.hkmc.behavioralpatternanalysis.external.client.DspServerGCClient
import com.hkmc.behavioralpatternanalysis.external.client.DspServerUVOClient
import com.hkmc.behavioralpatternanalysis.model.dto.UbiSafetyReqDTO
import com.hkmc.behavioralpatternanalysis.model.dto.UbiSafetyResDTO
import com.hkmc.behavioralpatternanalysis.model.dto.UbiSafetyVO
import com.hkmc.ccsp20behavioralpatternanalysis.service.BehavioralPatternServiceImpl
import com.hkmc.ccsp20behavioralpatternanalysis.service.dto.SpaResponseCodeEnum
import com.hkmc.ccsp20behavioralpatternanalysis.setup.GlobalExternalException
import feign.FeignException
import feign.Request
import feign.RequestTemplate
import feign.Response
import org.apache.commons.lang3.StringUtils
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

import org.springframework.core.env.Environment
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import spock.lang.Specification

import java.nio.charset.StandardCharsets

import static org.assertj.core.api.Assertions.assertThat
import static org.junit.jupiter.api.Assertions.assertThrows
import static org.junit.jupiter.api.Assertions.assertTrue
import static org.mockito.ArgumentMatchers.any
import static org.mockito.ArgumentMatchers.anyMap
import static org.mockito.ArgumentMatchers.anyString
import static org.mockito.Mockito.when


class BehavioralPatternServiceSpec extends Specification {


  private Map<String, String> header
  private String vin
  private String nadid
  private String ccid
  private String carid


  static def writeValueAsString(o) {
    return new ObjectMapper().writeValueAsString(o)
  }

  def setup() {
    vin = "KMOSTEST020052701"
    nadid = "01229168761"
    ccid = "43318BC2-6DE2-4F87-8396-4DFDBAB66B9A_BLU"
    carid = "411e9a8a-f48f-4c82-8b9b-ef3e96ea1b06"

    header = new HashMap() {
      {
        put("vin", vin)
        put("nadid", nadid)
        put("from", "PHONE")
        put("xtid", "87d058d2-3971-4fa2-8d63-625e12a945ee")
      }
    }
  }


  def "안전운전 점수 조회 성공"() {

    given:
    UbiSafetyReqDTO ubiSafetyReq = UbiSafetyReqDTO.builder()
        .ServiceNo("A26")
        .CCID(ccid)
        .carID(carid)
        .mtsNo(nadid)
        .build()

    def env = Stub(Environment)
    env.getProperty(_) >> "/dspath" >> "header-auth"

    when:
    def dspServerBLClient = Stub(DspServerBLClient)
    def dspServerUVOClient = Stub(DspServerUVOClient)
    def dspServerGCClient = Stub(DspServerGCClient)

    if (StringUtils.equals(arg, "BLU")) {
      dspServerBLClient.requestCallGet(_, _, _) >> ResponseEntity.ok().body(getDspResData())
    } else if (StringUtils.equals(arg, "UVO")) {
      dspServerBLClient.requestCallGet(_, _, _) >> ResponseEntity.ok().body(getDspResData())
    } else if (StringUtils.equals(arg, "GEN")) {
      dspServerGCClient.requestCallGet(_, _, _) >> ResponseEntity.ok().body(getDspResData())
    } else {
      ubiSafetyReq.setCCID(ubiSafetyReq.getCCID() + "BLU")
      dspServerBLClient.requestCallGet(_, _, _) >> ResponseEntity.ok().body(null)
    }

    def behavioralPatternService = Spy(BehavioralPatternServiceImpl, constructorArgs: [dspServerUVOClient, dspServerGCClient, dspServerBLClient, env])


    UbiSafetyResDTO ubiSafetyRes = behavioralPatternService.ubiSafetyDrivingScore(UbiSafetyVO.builder()
        .header(header)
        .vinPath(vin)
        .ubiSafetyReqDTO(ubiSafetyReq)
        .build())


    then:
    assertThat(ubiSafetyRes.getRetCode()).isEqualTo(retCode)
    assertThat(ubiSafetyRes.getResCode()).isEqualTo(resCode)

    where:
    arg   | retCode                                  | resCode
    "BLU" | SpaResponseCodeEnum.SUCCESS.getRetCode() | SpaResponseCodeEnum.SUCCESS.getResCode()
    "UVO" | SpaResponseCodeEnum.SUCCESS.getRetCode() | SpaResponseCodeEnum.SUCCESS.getResCode()
    "GEN" | SpaResponseCodeEnum.SUCCESS.getRetCode() | SpaResponseCodeEnum.SUCCESS.getResCode()
    ""    | SpaResponseCodeEnum.SUCCESS.getRetCode() | SpaResponseCodeEnum.SUCCESS.getResCode()

  }


  def "안전운전 점수 조회 오류 EX01"() {

    given:
    UbiSafetyReqDTO ubiSafetyReq = UbiSafetyReqDTO.builder()
        .ServiceNo("A26")
        .CCID("")
        .carID(carid)
        .mtsNo(nadid)
        .build()


    HashMap<String, Collection<String>> feignHeader = new HashMap<>()
    feignHeader.put(HttpHeaders.CONTENT_TYPE, Collections.singletonList(MediaType.APPLICATION_JSON_VALUE))

    def mockedFeignRequest = Request.create(
        Request.HttpMethod.GET, "foo", feignHeader, null, StandardCharsets.UTF_8, new RequestTemplate()
    )


    def env = Stub(Environment)
    env.getProperty(_) >> "/dspath" >> "header-auth"


    def dspServerBLClient = Stub(DspServerBLClient)
    def dspServerUVOClient = Stub(DspServerUVOClient)
    def dspServerGCClient = Stub(DspServerGCClient)

    dspServerBLClient.requestCallGet(_, _, _) >> FeignException.errorStatus("requestCallGet",
        Response.builder()
            .status(500)
            .body("{\"errCode\":\"5003\"}".getBytes(StandardCharsets.UTF_8))
            .reason("Initial server error")
            .request(mockedFeignRequest)
            .headers(feignHeader)
            .build())

    def behavioralPatternService = Spy(BehavioralPatternServiceImpl, constructorArgs: [dspServerUVOClient, dspServerGCClient, dspServerBLClient, env])

    when:
    behavioralPatternService.ubiSafetyDrivingScore(UbiSafetyVO.builder()
        .header(header)
        .vinPath(vin)
        .ubiSafetyReqDTO(ubiSafetyReq)
        .build())

    then:
    GlobalExternalException exception = thrown()
    exception.getBody().contains(SpaResponseCodeEnum.ERROR_EX01.getResCode())

  }


  private Map<String, Object> getDspResData() throws JsonProcessingException {

    String data = "{\"safety_drv_score\":9,\"ins_discount_yn\":\"N\",\"score_date\":\"20210510\",\"range_drv_dist\":487," +
        "\"brst_acc_grade\":\"WARNING\",\"brst_dec_grade\":\"WARNING\",\"night_drv_grade\":\"EXCELLENCE\"}"

    return new ObjectMapper().readValue(data, new TypeReference<Map<String, Object>>() {
    })
  }
}