package com.assessment.mstrxhistsvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MsTrxHistSvcApplication {

  public static void main(String[] args) {
    SpringApplication.run(MsTrxHistSvcApplication.class, args);
  }

}
