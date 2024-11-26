package com.ifortex.bookservice.repository.impl;

import com.ifortex.bookservice.model.Member;
import com.ifortex.bookservice.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class MemberRepositoryImpl implements MemberRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Member findMemberWithOldestRomanceBook() {
        String genre = "Romance";

        String nativeQuery = "SELECT m.id, m.name, m.membership_date FROM members m "
                + "WHERE m.id = (SELECT mb.member_id FROM member_books mb "
                + "JOIN books b ON mb.book_id = b.id "
                + "WHERE b.genre @> ARRAY[:genre]::varchar[] "
                + "ORDER BY b.publication_date ASC "
                + "LIMIT 1)";

        List<Member> members = entityManager.createNativeQuery(nativeQuery, Member.class)
                .setParameter("genre", genre)
                .getResultList();

        return members.isEmpty() ? null : members.get(0);
    }

    public List<Member> findMembersRegisteredIn2023WithoutBooks() {
        LocalDateTime startDate = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 12, 31, 23, 59, 59);

        String query = "SELECT m FROM Member m " +
                "LEFT JOIN m.borrowedBooks b " +
                "WHERE m.membershipDate BETWEEN :startDate AND :endDate " +
                "GROUP BY m.id " +
                "HAVING COUNT(b.id) = 0";

        TypedQuery<Member> typedQuery = entityManager.createQuery(query, Member.class);
        typedQuery.setParameter("startDate", startDate);
        typedQuery.setParameter("endDate", endDate);

        return typedQuery.getResultList();
    }
}
