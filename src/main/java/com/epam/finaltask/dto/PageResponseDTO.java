package com.epam.finaltask.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class PageResponseDTO<T> {
    private List<T> content;

    @JsonProperty("number")
    private int pageNumber;

    @JsonProperty("size")
    private int pageSize;

    private long totalElements;
}