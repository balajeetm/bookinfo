package com.balajeetm.demo.details;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.balajeetm.demo.details.model.BookDetail;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = {"/details"})
@Slf4j
public class DetailsController {

  @Value("${SERVICE_VERSION:}")
  String serviceVersion;

  @Value("${SLEEP_TIMEOUT:30}")
  Integer sleepTimeOut;

  @Autowired Status status;

  private List<String> requestIds = new ArrayList<>();

  private List<String> TRAIL_HEADERS =
      Arrays.asList(
          "x-request-id",
          "x-b3-traceid",
          "x-b3-spanid",
          "x-b3-parentspanid",
          "x-b3-sampled",
          "x-b3-flags",
          "x-ot-span-context",
          "x-datadog-trace-id",
          "x-datadog-parent-id",
          "x-datadog-sampled");

  @GetMapping(path = {"/{id}"})
  public ResponseEntity<?> getBookDetail(
      @PathVariable(value = "id") Integer id,
      @RequestHeader(name = "x-request-id", required = false) String requestId,
      @RequestHeader HttpHeaders headers) {

    String instanceId = status.getInstanceId();

    log.info("Instance Id {} for request", instanceId);

    if (StringUtils.equals(serviceVersion, "v-unavailable")) {
      return new ResponseEntity<>("v-unavailable", HttpStatus.SERVICE_UNAVAILABLE);
    }
    if (StringUtils.equals(serviceVersion, "v-timeout")) {
      sleep();
      return new ResponseEntity<>("v-timeout", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    if (StringUtils.equals(serviceVersion, "v-timeout-first-call")) {
      if (!requestIds.contains(requestId)) {
        log.info(String.format("New Request ID : %s: Will timeout", requestId));
        requestIds.add(requestId);
        sleep();
        return new ResponseEntity<>("v-timeout-first-call", HttpStatus.INTERNAL_SERVER_ERROR);
      }
      log.info(String.format("Repeat Request ID : %s: Will respond", requestId));
    }

    if (StringUtils.equals(serviceVersion, "v-50-50-unhealthy")) {
      if (!status.getStatus()) {
        HttpStatus code =
            new Random().nextBoolean()
                ? HttpStatus.INTERNAL_SERVER_ERROR
                : HttpStatus.SERVICE_UNAVAILABLE;
        log.error("Instance Id {} Unhealthy & returning error {}.", instanceId, code.value());
        return new ResponseEntity<>("v-50-50-unhealthy", code);
      }
    }

    BookDetail detail =
        new BookDetail()
            .setId(id)
            .setAuthor("William Shakespeare")
            .setYear(1595)
            .setType("paperback")
            .setPages(200)
            .setPublisher("PublisherA")
            .setLanguage("English")
            .setIsbn10("1234567890")
            .setIsbn13("123-1234567890");

    log.info("Instance Id {} Returning success", instanceId);
    return new ResponseEntity<>(detail, getHeaders(headers), HttpStatus.OK);
  }

  private HttpHeaders getHeaders(HttpHeaders headers) {
    HttpHeaders responseHeaders = new HttpHeaders();
    for (String header : TRAIL_HEADERS) {
      if (headers.containsKey(header)) {
        responseHeaders.addAll(header, headers.get(header));
      }
    }
    return responseHeaders;
  }

  private void sleep() {
    try {
      Thread.sleep(sleepTimeOut * 1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
