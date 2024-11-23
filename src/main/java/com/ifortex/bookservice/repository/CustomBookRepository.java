package com.ifortex.bookservice.repository;

import com.ifortex.bookservice.dto.SearchCriteria;
import com.ifortex.bookservice.model.Book;

import java.util.List;
import java.util.Map;

public interface CustomBookRepository {
    Map<String, Long> countBooksByGenre();
    List<Book> findBooksByCriteria(SearchCriteria searchCriteria);
}
