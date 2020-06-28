package com.balajeetm.demo.details.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BookDetail {

  Integer id;

  String author;

  Integer year;

  String type;

  Integer pages;

  String publisher;

  String language;

  @JsonProperty("ISBN-10")
  String isbn10;

  @JsonProperty("ISBN-13")
  String isbn13;
}
