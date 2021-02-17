package me.iseunghan.todolist.model;

import java.time.LocalDate;

public class TodoitemDto  {
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

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
