package com.ifortex.bookservice.repository;

import com.ifortex.bookservice.model.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class MemberRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public Member findMemberWithOldestRomanceBook() {
        String sql = """
            SELECT m.*
            FROM members m
            JOIN member_books mb ON m.id = mb.member_id
            JOIN books b ON b.id = mb.book_id
            WHERE :genre = ANY(b.genre)
            ORDER BY b.publication_date ASC, m.membership_date DESC
            LIMIT 1
            """;

        Query query = entityManager.createNativeQuery(sql, Member.class);
        query.setParameter("genre", "Romance");

        return (Member) query.getSingleResult();
    }


    public List<Member> findMembersRegisteredIn2023WithoutBooks() {
        String jpql = """
                SELECT m 
                FROM Member m 
                WHERE m.membershipDate BETWEEN :start AND :end 
                  AND m.borrowedBooks IS EMPTY
                """;

        LocalDateTime startOfYear = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime endOfYear = LocalDateTime.of(2023, 12, 31, 23, 59);

        TypedQuery<Member> query = entityManager.createQuery(jpql, Member.class);
        query.setParameter("start", startOfYear);
        query.setParameter("end", endOfYear);

        return query.getResultList();
    }
}
