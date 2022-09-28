package me.iseunghan.todolist.model.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PageDto {

    private int number;

    private boolean first;

    private boolean last;

    private int totalPages;

    private long totalElements;
}
