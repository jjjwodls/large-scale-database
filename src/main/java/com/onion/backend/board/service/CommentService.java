package com.onion.backend.board.service;

import com.onion.backend.board.domain.Article;
import com.onion.backend.board.domain.Board;
import com.onion.backend.board.domain.Comment;
import com.onion.backend.board.dto.*;
import com.onion.backend.board.infrastructure.BoardRepository;
import com.onion.backend.board.infrastructure.CommentRepository;
import com.onion.backend.exception.RateLimitException;
import com.onion.backend.exception.ResourceNotFoundException;
import com.onion.backend.user.domain.User;
import com.onion.backend.user.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private final BoardRepository boardRepository;

    private final UserService userService;

    private final CommentRepository commentRepository;

    private final ArticleService articleService;

    public CommentService(BoardRepository boardRepository, ArticleService articleService,
                          UserService userService,
                          CommentRepository commentRepository) {
        this.boardRepository = boardRepository;
        this.articleService = articleService;
        this.userService = userService;
        this.commentRepository = commentRepository;
    }

    @Transactional
    public CommentResponse writeComment(WriteCommentDto writeCommentDto) {
        Optional<Board> board = boardRepository.findById(writeCommentDto.getBoardId());
        if (board.isEmpty()) {
            throw new ResourceNotFoundException("board not found");
        }

        Article article = articleService.findById(writeCommentDto.getArticleId());

        if (!isCanWriteComment()) {
            throw new RateLimitException("comment not write by rate limit");
        }

        User user = userService.userBySecurityContext();

        Comment comment = new Comment();
        comment.setAuthor(user);
        comment.setContent(writeCommentDto.getContent());
        comment.setArticle(article);
        commentRepository.save(comment);

        return new CommentResponse(comment.getId(), comment.getContent());
    }

//    public List<ArticleResponse> firstGetArticle(Long boardId) {
//        List<Article> articles = articleRepository.findTop10ByBoardIdOrderByCreateDateDesc(boardId);
//
//        return articles.stream()
//                .map(article -> new ArticleResponse(article.getId(), article.getTitle(), article.getContent()))
//                .collect(Collectors.toList());
//    }
//
//    public List<ArticleResponse> getOldArticle(Long boardId, Long articleId) {
//        List<Article> articles = articleRepository.findTop10ByBoardIdAndIdLessThanOrderByCreatedAtDesc(boardId, articleId);
//
//        return articles.stream()
//                .map(article -> new ArticleResponse(article.getId(), article.getTitle(), article.getContent()))
//                .collect(Collectors.toList());
//    }
//
//    public List<ArticleResponse> getNewArticle(Long boardId, Long articleId) {
//        List<Article> articles = articleRepository.findTop10ByBoardIdAndIdGreaterThanOrderByCreatedAtDesc(boardId, articleId);
//
//        return articles.stream()
//                .map(article -> new ArticleResponse(article.getId(), article.getTitle(), article.getContent()))
//                .collect(Collectors.toList());
//    }

    @Transactional()
    public CommentResponse editComment(EditCommentDto editCommentDto, Long commentId) {
        Article article = articleService.findById(editCommentDto.getArticleId());

        User user = userService.userBySecurityContext();

        Comment comment = findByIdAndUserId(commentId, user.getId());

        if (!isCanEditComment()) {
            throw new RateLimitException("comment not edited by rate limit");
        }

        if (editCommentDto.getContent() != null) {
            comment.setContent(editCommentDto.getContent());
        }

        return new CommentResponse(comment.getId(), comment.getContent());

    }

    public Long deleteComment(Long commentId) {
        User user = userService.userBySecurityContext();

        Comment comment = findByIdAndUserId(commentId, user.getId());
        commentRepository.delete(comment);

        if (!isCanEditComment()) {
            throw new RateLimitException("comment not edited by rate limit");
        }

        return comment.getId();
    }

    private Comment findByIdAndUserId(Long Id, Long userId) {
        return commentRepository.findByIdAndAuthorId(Id, userId).orElseThrow(() -> new ResourceNotFoundException("comment not found"));
    }

    private boolean isCanWriteComment() {
        User user = userService.userBySecurityContext();

        Optional<Comment> comment = commentRepository.findTopByAuthorIdOrderByCreatedAtDesc(user.getId());

        return comment.map(value -> isMoreThanOneMinutesApart(value.getCreatedAt())).orElse(true);

    }

    private boolean isCanEditComment() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String username = userDetails.getUsername();
        User user = userService.findByUsername(username);

        Comment comment = commentRepository.findTopByAuthorIdOrderByUpdatedAtDesc(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("comment not found"));

        if (comment.getUpdatedAt() == null) {
            return true;
        }
        return isMoreThanOneMinutesApart(comment.getUpdatedAt());
    }

    private boolean isMoreThanOneMinutesApart(LocalDateTime time) {
        LocalDateTime now = LocalDateTime.now();

        Duration duration = Duration.between(time, now);

        return duration.toMinutes() >= 1;
    }

    public ArticleDetailResponse getArticle(Long boardId, Long articleId){
        Optional<Board> board = boardRepository.findById(boardId);
        if (board.isEmpty()) {
            throw new ResourceNotFoundException("board not found");
        }
        Article article = articleService.findById(articleId);
        List<Comment> comments = findByArticleId(articleId);

        article.setComments(comments);

        return ArticleDetailResponse.toResponse(article);
    }

    public List<Comment> findByArticleId(Long articleId){
        return commentRepository.findByArticleId(articleId);
    }
}
