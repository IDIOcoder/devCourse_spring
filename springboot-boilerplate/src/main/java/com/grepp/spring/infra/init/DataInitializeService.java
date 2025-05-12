package com.grepp.spring.infra.init;

import com.grepp.spring.app.model.busstop.BusStopRepository;
import com.grepp.spring.app.model.busstop.entity.BusStop;
import com.grepp.spring.app.model.student.document.StudentEmbedding;
import com.grepp.spring.app.model.student.entity.Student;
import com.grepp.spring.app.model.student.repository.StudentEmbeddingRepository;
import com.grepp.spring.app.model.student.repository.StudentRepository;
import com.grepp.spring.infra.feign.client.busstop.BusStopApi;
import com.grepp.spring.infra.feign.client.busstop.dto.BusStopDto;
import com.grepp.spring.infra.feign.client.busstop.dto.BusStopResponse;
import dev.langchain4j.model.embedding.EmbeddingModel;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DataInitializeService {
    
    private final BusStopRepository busStopRepository;
    private final StudentRepository studentRepository;
    private final EmbeddingModel embeddingModel;
    private final StudentEmbeddingRepository studentEmbeddingRepository;
    
    private final BusStopApi busStopApi;
    private final ModelMapper mapper;
    
    @Value("${bus-stop.apikey}")
    private String apiKey;
    
    @Transactional
    public void initialize() {
        if (busStopRepository.count() > 0) {
            return;
        }
        BusStopResponse response = busStopApi.getBusStop(apiKey, 1, 5);
        List<BusStopDto> dtos = response.document().row();
        List<BusStop> busStops = dtos.stream().map(e -> mapper.map(e, BusStop.class)).toList();
        busStopRepository.saveAll(busStops);
    }
    
    @Transactional
    public void initializeVector() {
        
        if (studentEmbeddingRepository.count() > 0)  return;
        List<Student> students = studentRepository.findAll();
        List<StudentEmbedding> embeddings = students
                                                .stream()
                                                .map(entity -> StudentEmbedding.fromEntity(entity,
                                                    embeddingModel))
                                                .toList();
        
        studentEmbeddingRepository.saveAll(embeddings);
        System.out.println("Persisting document embeddings...");
    }
}
