package com.asdf.minilog.service;

import com.asdf.minilog.dto.ArticleResponseDto;
import com.asdf.minilog.entity.Article;
import com.asdf.minilog.entity.User;
import com.asdf.minilog.exception.ArticleNotFoundException;
import com.asdf.minilog.exception.UserNotFoundException;
import com.asdf.minilog.repository.ArticleRepository;
import com.asdf.minilog.repository.UserRepository;
import com.asdf.minilog.util.EntityDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
// 트랜잭션 격리 수준
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class ArticleService {
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;

    @Autowired
    public ArticleService(UserRepository userRepository, ArticleRepository articleRepository) {
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
    }

    public ArticleResponseDto createArticle(String content, Long userId) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(
                                () ->
                                        new UserNotFoundException(
                                                String.format("해당 아이디(%d)를 가진 사용자를 찾을 수" +
                                                        "없습니다.", userId)));

        Article article = Article.builder()
                .content(content)
                .author(user)
                .build();

        Article saveArticle = articleRepository.save(article);
        return EntityDtoMapper.toDto(saveArticle);
    }

    public void deleteArticle(Long articleId) {
        Article article =
                articleRepository
                        .findById(articleId)
                        .orElseThrow(
                                () ->
                                        new ArticleNotFoundException(
                                                String.format("해당 아이디(%d)를 가진 게시글을 찾을 수" +
                                                        "없습니다.", articleId)));

        articleRepository.deleteById(articleId);
    }

    public ArticleResponseDto updateArticle(Long articleId, String content) {
        Article article =
                articleRepository
                        .findById(articleId)
                        .orElseThrow(
                                () ->
                                        new ArticleNotFoundException(
                                                String.format("해당 아이디(%d)를 가진 게시글을 찾을 수" +
                                                        "없습니다.", articleId)));

        article.setContent(content);

        return EntityDtoMapper.toDto(article);
    }

    @Transactional(readOnly = true)
    public ArticleResponseDto getArticleById(Long articleId) {
        Article article =
                articleRepository.findById(articleId)
                        .orElseThrow(
                                () ->
                                        new ArticleNotFoundException(
                                                String.format("해당 아이디(%d)를 가진 게시글을 찾을 수 " +
                                                        "없습니다", articleId)));

        return EntityDtoMapper.toDto(article);
    }

    @Transactional(readOnly = true)
    public List<ArticleResponseDto> getFeedListByFollowerId(Long userId) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(
                                () ->
                                        new UserNotFoundException(
                                                String.format("해당 아이디(%d)를 가진 사용자를 찾을 수 없습니다.", userId)));

        var feedList = articleRepository.findAllByFollowerId(userId);
        return feedList.stream()
                .map(EntityDtoMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<ArticleResponseDto> getArticleListByUserId(Long userId) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(
                                () ->
                                        new UserNotFoundException(
                                                String.format("해당 아이디(%d)를 가진 사용자를 찾을 수 없습니다.", userId)));

        var articleList = articleRepository.findAllByAuthorId(userId);
        return articleList.stream()
                .map(EntityDtoMapper::toDto).toList();
    }
}
