package io.seanapse.clients.jms.services.filesservice.file.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.seanapse.clients.jms.services.filesservice.file.domain.model.FileInfo;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class PagedFileResponse {
    private Pageable pageable;
    private List<FileInfo> files;
    private long totalNumberOfElements;

    public PagedFileResponse() {
    }

    public PagedFileResponse(Pageable pageable, List<FileInfo> files, long totalNumberOfElements) {
        this.pageable = pageable;
        this.files = files;
        this.totalNumberOfElements = totalNumberOfElements;
    }

    public List<FileInfo> getFiles() {
        return files;
    }

    @JsonProperty("totalCount")
    public long getTotalCount() {
        return totalNumberOfElements;
    }

    @JsonProperty("totalPages")
    public int getTotalPages() {
        double totalNumberOfPages = Math.floor(((double) totalNumberOfElements + (double) pageable.getPageSize() - 1) / (double) pageable.getPageSize());
        return (int) totalNumberOfPages;
    }

    @JsonProperty("currentPage")
    public int getCurrentPage() {
        return pageable.getPageNumber() + 1;
    }

    @JsonProperty("pageSize")
    public int getPageSize() {
        return pageable.getPageSize();
    }
}
