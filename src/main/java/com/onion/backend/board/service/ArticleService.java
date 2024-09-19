package com.onion.backend.board.service;

import com.onion.backend.board.domain.Article;
import com.onion.backend.board.domain.Board;
import com.onion.backend.board.dto.ArticleResponse;
import com.onion.backend.board.dto.EditArticleDto;
import com.onion.backend.board.dto.WriteArticleDto;
import com.onion.backend.board.infrastructure.ArticleRepository;
import com.onion.backend.board.infrastructure.BoardRepository;
import com.onion.backend.exception.RateLimitException;
import com.onion.backend.exception.ResourceNotFoundException;
import com.onion.backend.user.domain.User;
import com.onion.backend.user.infrastructure.UserRepository;
import com.onion.backend.user.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ArticleService {

    private final BoardRepository boardRepository;

    private final ArticleRepository articleRepository;

    private final UserService userService;

    public ArticleService(BoardRepository boardRepository, ArticleRepository articleRepository,
                          UserService userService) {
        this.boardRepository = boardRepository;
        this.articleRepository = articleRepository;
        this.userService = userService;
    }

    public Article writeArticle(WriteArticleDto writeArticleDto) {
        Optional<Board> board = boardRepository.findById(writeArticleDto.getBoardId());
        if (board.isEmpty()) {
            throw new ResourceNotFoundException("board not found");
        }

        if (!isCanWriteArticle()) {
            throw new RateLimitException("article not write by rate limit");
        }

        String username = writeArticleDto.getUsername();

        User user = userService.findByUsername(username);

        Article article = new Article();
        article.setBoard(board.get());
        article.setAuthor(user);
        article.setTitle(writeArticleDto.getTitle());
        article.setContent(writeArticleDto.getContent());
        articleRepository.save(article);

        return article;
    }

    public List<ArticleResponse> firstGetArticle(Long boardId) {
        List<Article> articles = articleRepository.findTop10ByBoardIdOrderByCreateDateDesc(boardId);

        return articles.stream()
                .map(article -> new ArticleResponse(article.getId(), article.getTitle(), article.getContent()))
                .collect(Collectors.toList());
    }

    public List<ArticleResponse> getOldArticle(Long boardId, Long articleId) {
        List<Article> articles = articleRepository.findTop10ByBoardIdAndIdLessThanOrderByCreatedAtDesc(boardId, articleId);

        return articles.stream()
                .map(article -> new ArticleResponse(article.getId(), article.getTitle(), article.getContent()))
                .collect(Collectors.toList());
    }

    public List<ArticleResponse> getNewArticle(Long boardId, Long articleId) {
        List<Article> articles = articleRepository.findTop10ByBoardIdAndIdGreaterThanOrderByCreatedAtDesc(boardId, articleId);

        return articles.stream()
                .map(article -> new ArticleResponse(article.getId(), article.getTitle(), article.getContent()))
                .collect(Collectors.toList());
    }

    @Transactional()
    public ArticleResponse editArticle(EditArticleDto editArticleDto, Long articleId) {
        Optional<Board> board = boardRepository.findById(editArticleDto.getBoardId());
        if (board.isEmpty()) {
            throw new ResourceNotFoundException("board not found");
        }

        String username = editArticleDto.getUsername();

        User user = userService.findByUsername(username);

        Article article = getArticle(articleId, user);

        if (!isCanEditArticle()) {
            throw new RateLimitException("article not edited by rate limit");
        }

        if (editArticleDto.getTitle().isPresent()) {
            article.setTitle(editArticleDto.getTitle().get());
        }

        if (editArticleDto.getContent().isPresent()) {
            article.setContent(editArticleDto.getContent().get());
        }

        return new ArticleResponse(article.getId(), article.getTitle(), article.getContent());

    }

    public Long deleteArticle(Long articleId) {
        User user = userService.userBySecurityContext();

        Article article = getArticle(articleId, user);
        articleRepository.delete(article);

        if(!isCanEditArticle()){
            throw new RateLimitException("article not edited by rate limit");
        }

        return article.getId();
    }

    private Article getArticle(Long articleId, User user) {
        return articleRepository.findByIdAndAuthorId(articleId, user.getId()).orElseThrow(() -> new ResourceNotFoundException("article not found"));
    }

    private boolean isCanWriteArticle() {
        User user = userService.userBySecurityContext();

        Article article = articleRepository.findTopByAuthorIdOrderByCreatedAtDesc(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("article not found"));
        return isMoreThanFiveMinutesApart(article.getCreatedAt());
    }

    private boolean isCanEditArticle() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String username = userDetails.getUsername();
        User user = userService.findByUsername(username);

        Article article = articleRepository.findTopByAuthorIdOrderByUpdatedAtDesc(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("article not found"));

        if (article.getUpdatedAt() == null) {
            return true;
        }
        return isMoreThanFiveMinutesApart(article.getUpdatedAt());
    }

    private boolean isMoreThanFiveMinutesApart(LocalDateTime time) {
        // 현재 시간
        LocalDateTime now = LocalDateTime.now();

        // 두 시간 간의 차이 계산
        Duration duration = Duration.between(time, now);

        // 차이가 5분(300초) 이상이면 true 반환
        return duration.toMinutes() >= 5;
    }
}
