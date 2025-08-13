package com.example.work.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Page<T> {
    private int pageNum;
    private int pageSize;
    private int total;
    private List<T> list;
}
