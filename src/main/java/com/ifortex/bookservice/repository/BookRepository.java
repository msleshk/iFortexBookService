package com.ifortex.bookservice.repository;

import com.ifortex.bookservice.dto.SearchCriteria;
import com.ifortex.bookservice.model.Book;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class BookRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Map<String, Long> getBooksByGenre(){
        String sql = """
            SELECT unnest(genre) AS genre, COUNT(*) AS total_count
            FROM books
            GROUP BY unnest(genre)
            ORDER BY total_count DESC
            """;

        List<Object[]> results = entityManager.createNativeQuery(sql).getResultList();
        return results.stream()
                .collect(Collectors.toMap(row -> (String) row[0], row -> ((Number) row[1]).longValue()));
    }

    public List<Book> findBooksBySearchCriteria(SearchCriteria searchCriteria) {
        String sql = "SELECT * FROM books WHERE " +
                "title ILIKE :title " +
                "AND author ILIKE :author " +
                "AND description ILIKE :description " +
                "AND :genre = ANY(genre) " +
                "ORDER BY publication_date";

        Query query = entityManager.createNativeQuery(sql, Book.class);

        String title = searchCriteria.getTitle() != null ? searchCriteria.getTitle() : "";
        String author = searchCriteria.getAuthor() != null ? searchCriteria.getAuthor() : "";
        String description = searchCriteria.getDescription() != null ? searchCriteria.getDescription() : "";
        String genre = searchCriteria.getGenre() != null ? searchCriteria.getGenre() : "";

        query.setParameter("title", "%" + title + "%");
        query.setParameter("author", "%" + author + "%");
        query.setParameter("description", "%" + description + "%");
        query.setParameter("genre", genre);

        return query.getResultList();
    }
}
