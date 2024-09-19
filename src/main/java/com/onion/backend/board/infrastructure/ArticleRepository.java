package com.onion.backend.board.infrastructure;

import com.onion.backend.board.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article,Long> {

    @Query("SELECT a FROM Article a WHERE a.board.id = :boardId ORDER BY a.createdAt DESC")
    List<Article> findTop10ByBoardIdOrderByCreateDateDesc(@Param("boardId") Long boardId);

    @Query("SELECT a FROM Article a WHERE a.board.id = :boardId AND a.id < :articleId ORDER BY a.createdAt DESC")
    List<Article> findTop10ByBoardIdAndIdLessThanOrderByCreatedAtDesc(
            @Param("boardId") Long boardId,
            @Param("articleId") Long articleId
    );

    @Query("SELECT a FROM Article a WHERE a.board.id = :boardId AND a.id > :articleId ORDER BY a.createdAt DESC")
    List<Article> findTop10ByBoardIdAndIdGreaterThanOrderByCreatedAtDesc(
            @Param("boardId") Long boardId,
            @Param("articleId") Long articleId
    );

    Optional<Article> findByIdAndAuthorId(Long articleId, Long authorId);

    Optional<Article> findTopByAuthorIdOrderByCreatedAtDesc(Long authorId);

    Optional<Article> findTopByAuthorIdOrderByUpdatedAtDesc(Long authorId);
}
