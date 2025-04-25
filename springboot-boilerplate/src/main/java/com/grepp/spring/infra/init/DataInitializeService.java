package com.grepp.spring.infra.init;

import com.grepp.spring.app.model.busstop.BusStopRepository;
import com.grepp.spring.app.model.busstop.entity.BusStop;
import com.grepp.spring.app.model.student.StudentRepository;
import com.grepp.spring.app.model.student.entity.Student;
import com.grepp.spring.infra.feign.client.busstop.BusStopApi;
import com.grepp.spring.infra.feign.client.busstop.dto.BusStopDto;
import com.grepp.spring.infra.feign.client.busstop.dto.BusStopResponse;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
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
    private final EmbeddingStore<TextSegment> embeddingStore;
    
    private final BusStopApi busStopApi;
    private final ModelMapper mapper;
    
    @Value("${bus-stop.apikey}")
    private String apiKey;
    
    @Transactional
    public void initialize(){
        if(busStopRepository.count() > 0) return;
        BusStopResponse response = busStopApi.getBusStop(apiKey,1,5);
        List<BusStopDto> dtos = response.document().row();
        List<BusStop> busStops = dtos.stream().map(e -> mapper.map(e, BusStop.class)).toList();
        busStopRepository.saveAll(busStops);
    }
    
    @Transactional
    public void initializeVector(){
        
        List<Student> students = studentRepository.findAll();
        
        ArrayList<Document> docs = new ArrayList<>();
        
        students.forEach( e -> {
            docs.add(new Document().append("text", e.toString()).append("metadata", new Metadata(Map.of("id", "userId"))));
        });
        
        System.out.println("Persisting document embeddings...");
        
        for (Document doc : docs) {
            TextSegment segment = TextSegment.from(
                doc.getString("text"),
                doc.get("metadata", Metadata.class)
            );
            Embedding embedding = embeddingModel.embed(segment).content();
            embeddingStore.add(embedding, segment);
        }
    }
}
