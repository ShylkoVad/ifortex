package com.ifortex.bookservice.controller;

import com.ifortex.bookservice.dto.SearchCriteria;
import com.ifortex.bookservice.model.Book;
import com.ifortex.bookservice.service.BookService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/books") // Устанавливаем базовый путь
@AllArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping("/statistic")
    public ResponseEntity<Map<String, Long>> getStatistic() {
        Map<String, Long> genreCounts = bookService.getBooks();
        return ResponseEntity.ok(genreCounts);
    }

    @PostMapping("/search")
    public ResponseEntity<List<Book>> findBooks(@RequestBody SearchCriteria searchCriteria) {
        List<Book> books = bookService.getAllByCriteria(searchCriteria);
        return ResponseEntity.ok(books);
    }
}
