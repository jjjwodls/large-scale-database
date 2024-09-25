package com.onion.backend.board.infrastructure;

import com.onion.backend.board.domain.Article;
import com.onion.backend.board.domain.Board;
import com.onion.backend.board.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findTopByAuthorIdOrderByCreatedAtDesc(Long authorId);

    Optional<Comment> findTopByAuthorIdOrderByUpdatedAtDesc(Long authorId);

    Optional<Comment> findByIdAndAuthorId(Long Id, Long authorId);
}
