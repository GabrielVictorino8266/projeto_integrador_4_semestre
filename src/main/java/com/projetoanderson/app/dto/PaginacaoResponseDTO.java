package com.projetoanderson.app.dto;

import org.springframework.data.domain.Page;
import java.util.List;

public class PaginacaoResponseDTO<T> {

    private List<T> data;
    private PaginationMetadata pagination;

    public PaginacaoResponseDTO(Page<T> page) {
        this.data = page.getContent();
        this.pagination = new PaginationMetadata(
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public PaginationMetadata getPagination() {
        return pagination;
    }

    public void setPagination(PaginationMetadata pagination) {
        this.pagination = pagination;
    }

    public static class PaginationMetadata {
        private int page;         
        private int size;
        private long totalPages;  
        private long totalItems;  

        public PaginationMetadata(int page, int size, long totalPages, long totalItems) {
            this.page = page;
            this.size = size;
            this.totalPages = totalPages;
            this.totalItems = totalItems;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public long getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(long totalPages) {
            this.totalPages = totalPages;
        }

        public long getTotalItems() {
            return totalItems;
        }

        public void setTotalItems(long totalItems) {
            this.totalItems = totalItems;
        }
    }
}