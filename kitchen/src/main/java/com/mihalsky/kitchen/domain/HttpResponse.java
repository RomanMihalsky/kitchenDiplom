package com.mihalsky.kitchen.domain;

import java.util.Date;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class HttpResponse {
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss", timezone = "Europe/Moscow" )
  private Date timeStamp;
  private int httpStatusCode;
  private HttpStatus httpStatus;
  private String reason;
  private String message;
  
  public HttpResponse() {}
  
  public HttpResponse(int httpStatusCode, HttpStatus httpStatus, String reason, String message) {
    this.timeStamp = new Date();
    this.httpStatusCode = httpStatusCode;
    this.httpStatus = httpStatus;
    this.reason = reason;
    this.message = message;
}
}