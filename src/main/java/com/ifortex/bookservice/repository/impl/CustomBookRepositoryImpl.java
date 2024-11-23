package com.ifortex.bookservice.repository.impl;

import com.ifortex.bookservice.dto.SearchCriteria;
import com.ifortex.bookservice.model.Book;
import com.ifortex.bookservice.repository.CustomBookRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class CustomBookRepositoryImpl implements CustomBookRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Map<String, Long> countBooksByGenre() {
        // SQL запрос с суммированием по одинаковым жанрам
        String queryStr = "SELECT genre, SUM(count) FROM (" +
                "SELECT unnest(genre) AS genre, COUNT(*) AS count FROM books GROUP BY genre" +
                ") AS subquery " +
                "GROUP BY genre " +
                "ORDER BY SUM(count) DESC";

        List<Object[]> results = entityManager.createNativeQuery(queryStr).getResultList();

        // Проверяем, что результаты не пустые
        if (results.isEmpty()) {
            return Collections.emptyMap();
        }

        // Используем LinkedHashMap для сохранения порядка
        return results.stream()
                .map(result -> new AbstractMap.SimpleEntry<>((String) result[0], ((Number) result[1]).longValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Long::sum, LinkedHashMap::new));
    }



    @Override
    public List<Book> findBooksByCriteria(SearchCriteria searchCriteria) {
        StringBuilder queryStr = new StringBuilder("SELECT b FROM Book b WHERE 1=1");
        boolean hasCriteria = false;

        // Проверка названия книги
        if (searchCriteria.getTitle() != null && !searchCriteria.getTitle().isEmpty()) {
            queryStr.append(" AND LOWER(b.title) LIKE :title");
            hasCriteria = true;
        }

        // Проверка автора книги
        if (searchCriteria.getAuthor() != null && !searchCriteria.getAuthor().isEmpty()) {
            queryStr.append(" AND LOWER(b.author) LIKE :author");
            hasCriteria = true;
        }

        // Обработка жанра
        if (searchCriteria.getGenre() != null && !searchCriteria.getGenre().isEmpty()) {
            List<String> genres = Arrays.asList(searchCriteria.getGenre().split(","));
//            queryStr.append(" AND b.genres IN :genres");
            queryStr.append(" AND :genre MEMBER OF b.genres");
            hasCriteria = true;
        }

        // Проверка описания книги
        if (searchCriteria.getDescription() != null && !searchCriteria.getDescription().isEmpty()) {
            queryStr.append(" AND LOWER(b.description) LIKE :description");
            hasCriteria = true;
        }

        // Проверка года публикации
        if (searchCriteria.getYear() != null) {
            queryStr.append(" AND b.year = :year");
            hasCriteria = true;
        }

        // Сортировка по дате публикации
        queryStr.append(" ORDER BY b.publicationDate ASC");

        TypedQuery<Book> query = entityManager.createQuery(queryStr.toString(), Book.class);

        // Установка параметров запроса
        if (searchCriteria.getTitle() != null && !searchCriteria.getTitle().isEmpty()) {
            query.setParameter("title", "%" + searchCriteria.getTitle().toLowerCase() + "%");
        }

        if (searchCriteria.getAuthor() != null && !searchCriteria.getAuthor().isEmpty()) {
            query.setParameter("author", "%" + searchCriteria.getAuthor().toLowerCase() + "%");
        }

        if (searchCriteria.getGenre() != null && !searchCriteria.getGenre().isEmpty()) {
            List<String> genres = Arrays.asList(searchCriteria.getGenre().split(","));
            query.setParameter("genres", genres);
        }

        if (searchCriteria.getDescription() != null && !searchCriteria.getDescription().isEmpty()) {
            query.setParameter("description", "%" + searchCriteria.getDescription().toLowerCase() + "%");
        }

        if (searchCriteria.getYear() != null) {
            query.setParameter("year", searchCriteria.getYear());
        }

        // Если нет критериев, возвращаем все книги
        if (!hasCriteria) {
            return entityManager.createQuery("SELECT b FROM Book b ORDER BY b.publicationDate ASC", Book.class).getResultList();
        }

        return query.getResultList();
    }
}