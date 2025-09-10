package com.postsproject.util;

public record Page<T>(
    String search,
    long pageSize,
    long pageNumber,
    long pageCount,
    Iterable<T> posts
) {}
