package io.github.enessaidtatli.controller.restresponse;

import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Getter
public class DataResponse<T> {

    private List<T> items = List.of();
    private int page;
    private int size;
    private long totalSize;
    private String sortDirection;

    public DataResponse(List<T> items, int page, int size, long totalSize, String sortDirection) {
        this.items = items;
        this.page = page;
        this.size = size;
        this.totalSize = totalSize;
        this.sortDirection = sortDirection;
    }

    public DataResponse(List<T> items){
        this.items = items;
    }

}
