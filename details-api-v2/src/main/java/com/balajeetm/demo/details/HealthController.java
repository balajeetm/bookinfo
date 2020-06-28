package com.balajeetm.demo.details;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = {"/health"})
public class HealthController {

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

  @GetMapping
  public ResponseEntity<?> health(@RequestHeader HttpHeaders headers) {
    return new ResponseEntity<>("Healthy", getHeaders(headers), HttpStatus.OK);
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
}
