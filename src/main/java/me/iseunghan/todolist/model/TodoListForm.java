package me.iseunghan.todolist.model;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

public class TodoListForm {

    private String title;
    private LocalDate date = LocalDate.now();
    private TodoStatus todoStatus = TodoStatus.NEVER;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(String date) {
        String[] split = date.split("-");
        this.date = LocalDate.of(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
    }
}
