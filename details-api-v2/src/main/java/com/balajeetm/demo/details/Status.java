package com.balajeetm.demo.details;

import java.util.Random;
import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Status {

  @Getter private Boolean status;
  @Getter private String instanceId;

  public Status() {
    status = new Random().nextBoolean();
    instanceId = UUID.randomUUID().toString();
    log.info("Instance Id {} Running with status {}.", instanceId, status);
  }
}
