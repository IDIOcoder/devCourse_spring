package com.grepp.spring.infra.mail;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Data;

@Data
public class SmtpDto {
    private String templatePath;
    private String to;
    private String from;
    private String subject;
    private Map<String, Object> properties = new LinkedHashMap<>();
}
