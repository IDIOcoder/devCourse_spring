package com.grepp.spring.app.model.student;

import com.grepp.spring.app.model.student.code.Sentimental;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface StudentAiService {

    @SystemMessage("너는 다정하고 친절한 상담사야.")
    String chat(String message);
    Sentimental classify(String message);
}
