package com.onion.backend.board.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onion.backend.board.domain.Article;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ElasticSearchArticleService {

    private final WebClient esWebClient;
    private final ObjectMapper objectMapper;


    /**
     * 지정한 인덱스에서 주어진 필드에 대해 match 쿼리를 수행하고,
     * _source 부분만 추출하여 Map 리스트로 반환합니다.
     *
     * @param index 검색할 인덱스 이름
     * @param field match할 필드 이름
     * @param text  검색어
     * @return Mono<List < Map < String, Object>>> 검색 결과 리스트
     */
    public Mono<List<Map<String, Object>>> searchByMatch(String index,
                                                         String field,
                                                         String text) {
        // Elasticsearch DSL body
        Map<String, Object> dsl = Map.of(
                "query", Map.of(
                        "match", Map.of(field, text)
                )
        );

        return esWebClient.post()
                .uri("/{index}/_search", index)
                .bodyValue(dsl)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(this::extractSources);
    }

    private List<Map<String, Object>> extractSources(JsonNode root) {
        List<Map<String, Object>> result = new ArrayList<>();
        JsonNode hits = root.path("hits").path("hits");
        for (JsonNode hit : hits) {
            JsonNode src = hit.path("_source");
            // JsonNode → Map 변환
            Map<String, Object> map = objectMapper.convertValue(
                    src, new TypeReference<>() {
                    }
            );
            result.add(map);
        }
        return result;
    }

    /**
     * @param index doc을 추가할 index
     * @return
     */
    public String indexArticleDocument(String index, Article article) throws JsonProcessingException {
        String doc = objectMapper.writeValueAsString(article);
        return esWebClient.post()
                .uri("/{index}/_doc", index)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(doc)
                .retrieve()
                .bodyToMono(String.class)
                .block();

    }

    public String indexEditArticleDocument(String index, Long id, Article article) throws JsonProcessingException {
        String doc = objectMapper.writeValueAsString(article);
        return esWebClient.post()
                .uri("/{index}/_doc/{id}", index, id)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(doc)
                .retrieve()
                .bodyToMono(String.class)
                .block();

    }
}
