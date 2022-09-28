package me.iseunghan.todolist.model.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RetrieveAccountResponse<T> {

    private List<T> accountList;

    private PageDto pageable;
}
