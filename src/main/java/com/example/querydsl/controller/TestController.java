package com.example.querydsl.controller;

import com.example.querydsl.repo.QuerydslRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {
  private final QuerydslRepo querydslRepo;

  // http://localhost:8080

  @GetMapping
  // QuerydslQTypeTests에 TestCode 작성
  public String test(){
    querydslRepo.helloQuerydsl();
    return "done";
  }
}
