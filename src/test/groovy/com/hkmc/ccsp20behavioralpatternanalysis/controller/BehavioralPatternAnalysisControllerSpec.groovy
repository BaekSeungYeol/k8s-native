package com.hkmc.ccsp20behavioralpatternanalysis.controller

import com.google.gson.Gson
import com.hkmc.behavioralpatternanalysis.model.BehavioralPatternService
import com.hkmc.behavioralpatternanalysis.model.dto.UbiSafetyReqDTO
import com.hkmc.ccsp20behavioralpatternanalysis.Starter
import com.hkmc.ccsp20behavioralpatternanalysis.api.BehavioralPatternAnalysisController
import lombok.extern.slf4j.Slf4j
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Starter.class)
@DirtiesContext
@EnableAutoConfiguration(exclude = [
    DataSourceAutoConfiguration.class,
    R2dbcAutoConfiguration.class,
    MongoAutoConfiguration.class
])
@ActiveProfiles("local")
@AutoConfigureMockMvc
@Slf4j
class BehavioralPatternAnalysisControllerSpec extends Specification {

  @MockBean
  BehavioralPatternService behavioralPatternService;

  private MockMvc client

  private String vin;
  private HttpHeaders httpHeaders;

  def setup() {
    client = MockMvcBuilders.standaloneSetup(new BehavioralPatternAnalysisController(behavioralPatternService)).build()
    vin = "KMOSTEST020052701"

    httpHeaders = new HttpHeaders() {
      {
        add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
      }
    }
  }

  def "안전운전 점수 조회"() {
    given:
    UbiSafetyReqDTO ubiSafetyReq = UbiSafetyReqDTO.builder()
        .ServiceNo("A26")
        .CCID("43318BC2-6DE2-4F87-8396-4DFDBAB66B9A_BLU")
        .carID("411e9a8a-f48f-4c82-8b9b-ef3e96ea1b06")
        .mtsNo("01229168761")
        .build()

    when:
    def response = client.perform(post("/behavioralpatternanalysis/v1/ubiscore/{vinPath}", vin)
        .contentType(MediaType.APPLICATION_JSON)
        .headers(httpHeaders)
        .content(new Gson().toJson(ubiSafetyReq)))

    then:
    response.andExpect(status().isOk())
  }

}
