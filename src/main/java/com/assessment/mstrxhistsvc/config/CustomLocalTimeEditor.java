package com.assessment.mstrxhistsvc.config;

import java.beans.PropertyEditorSupport;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CustomLocalTimeEditor extends PropertyEditorSupport {

  private final DateTimeFormatter formatter;

  public CustomLocalTimeEditor(String pattern) {
    formatter = DateTimeFormatter.ofPattern(pattern);
  }

  @Override
  public void setAsText(String text) throws IllegalArgumentException {
    if (text == null || text.trim().isEmpty()) {
      setValue(null);
    } else {
      LocalTime parsedValue = LocalTime.parse(text, formatter);
      setValue(parsedValue);
    }
  }

  @Override
  public String getAsText() {
    LocalTime value = (LocalTime) getValue();
    if (value == null) {
      return "";
    } else {
      return value.format(formatter);
    }
  }
}
