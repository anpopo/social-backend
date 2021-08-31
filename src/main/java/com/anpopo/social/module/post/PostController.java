package com.anpopo.social.module.post;

import com.anpopo.social.module.account.CurrentUser;
import com.anpopo.social.module.account.domain.Account;
import com.anpopo.social.module.interest.InterestRepository;
import com.anpopo.social.module.tag.Tag;
import com.anpopo.social.module.tag.TagRepository;
import com.anpopo.social.module.tag.TagService;
import com.anpopo.social.module.tag.form.TagForm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/post")
@Controller
public class PostController {

    private final PostService postService;
    private final TagService tagService;
    private final InterestRepository interestRepository;
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final CommentRepository commentRepository;
    private final ObjectMapper objectMapper;


    @GetMapping
    public String newPostView(@CurrentUser Account account, Model model) throws JsonProcessingException {
        model.addAttribute(account);
        model.addAttribute(new PostForm());

        List<String> whiteList = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());
        model.addAttribute("whiteList", objectMapper.writeValueAsString(whiteList));
        model.addAttribute("interests", interestRepository.findAll());

        return "post/form";
    }

    @PostMapping
    public String createPost(@CurrentUser Account account, @Valid PostForm postForm, Errors errors,
                             String interests, Model model) throws JsonProcessingException {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(postForm);
            List<String> whiteList = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());
            model.addAttribute("whiteList", objectMapper.writeValueAsString(whiteList));
            model.addAttribute("interests", interestRepository.findAll());
            return "post/form";
        }
        // 포스트 저장
        postService.savePost(account, postForm);

        return "redirect:/";
    }

    @GetMapping("/{id}")
    public String viewPostDetail(@CurrentUser Account account, @PathVariable Long id, Model model) {

        Post post = postRepository.findPostWithTagsWithInterestWithAccountWithLikeAccountById(id);

        if (post == null) {
            throw new IllegalArgumentException("해당 포스트가 존재하지 않습니다.");
        }

        model.addAttribute(account);
        model.addAttribute(post);
        model.addAttribute("commentList", commentRepository.findCommentWithAccountByPostOrderByModifiedAtDesc(post));
        model.addAttribute(CommentForm.builder().postId(id).build());

        return "post/detail";
    }

    @GetMapping("/{id}/edit")
    public String updatePostView(@CurrentUser Account account, @PathVariable Long id, Model model) throws JsonProcessingException {
        model.addAttribute(account);

        // 태그를 포함한 포스팅 가져오기
        Post post = postRepository.findPostWithTagsWithInterestById(id);

        if (post == null) {
            throw new IllegalArgumentException("포스팅에 대한 잘못된 접근입니다.");
        }

        if (!post.getAccount().equals(account)) {
            throw new IllegalArgumentException("작성자 본인이 아니면 접근할 수 없습니다.");
        }

        PostForm postForm = PostForm.builder()
                .id(post.getId())
                .context(post.getContext())
                .tags(objectMapper.writeValueAsString(post.getTags().stream().map(Tag::getTitle)))
                .hiddenTags("|" + post.getTags().stream().map(Tag::getTitle).collect(Collectors.joining("|")) + "|")
                .interest(post.getInterest().getInterest())
                .postImage1(post.getPostImage1())
                .postImage2(post.getPostImage2())
                .postImage3(post.getPostImage3())
                .build();

        model.addAttribute(postForm);

        List<String> whiteList = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());
        model.addAttribute("whiteList", objectMapper.writeValueAsString(whiteList));
        model.addAttribute("interests", interestRepository.findAll());

        return "post/edit";
    }

    @PostMapping("/{id}/edit")
    public String updatePost(@CurrentUser Account account, @PathVariable Long id, @Valid PostForm postForm, Errors errors, Model model) throws JsonProcessingException {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(postForm);
            List<String> whiteList = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());
            model.addAttribute("whiteList", objectMapper.writeValueAsString(whiteList));
            model.addAttribute("interests", interestRepository.findAll());
            return "post/form";
        }
        Post post = postRepository.findPostWithTagsWithInterestById(id);

        if (!post.getAccount().equals(account)) {
            throw new IllegalArgumentException("본인이 아니면 수정할 수 없습니다.");
        }

        postService.updatePost(post, postForm);

        return "redirect:/";
    }

    @PostMapping("/{id}/delete")
    public String deletePost(@CurrentUser Account account, @PathVariable Long id) {
        if (postRepository.existsByAccountAndId(account, id)) {
            postService.deletePost(id);
        }
        return "redirect:/";
    }


    @ResponseBody
    @PostMapping("/tags/add")
    public ResponseEntity addTags(@CurrentUser Account account, @RequestBody TagForm tagForm) {

        tagService.findOrCreateNew(tagForm.getTagTitle(), account);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/tags/remove")
    public ResponseEntity removeTag(@CurrentUser Account account, @RequestBody TagForm tagForm) {
        String title = tagForm.getTagTitle();

        Optional<Tag> tag = tagRepository.findByTitle(title);

        if (tag.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }


    @PostMapping("/like")
    public ResponseEntity postLike(@CurrentUser Account account, @RequestBody LikeForm likeForm) {

        Long postId = likeForm.getPostId();

        Optional<Post> post = postRepository.findPostWithLikeAccountById(postId);

        if (post.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Integer likeCount = postService.likePost(account, post.get());

        return ResponseEntity.ok().body(likeCount);
    }

    @PostMapping("/dislike")
    public ResponseEntity postDislike(@CurrentUser Account account, @RequestBody LikeForm likeForm) {

        Long postId = likeForm.getPostId();

        Optional<Post> post = postRepository.findPostWithLikeAccountById(postId);

        if (post.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Integer dislikeCount = postService.dislikePost(account, post.get());

        return ResponseEntity.ok().body(dislikeCount);
    }

    @PostMapping("/comment")
    public String addComment(@CurrentUser Account account, @Valid CommentForm commentForm, Errors errors, Model model) {

        if (errors.hasErrors()) {
            Post post = postRepository.findPostWithTagsWithInterestWithAccountWithLikeAccountById(commentForm.getPostId());
            model.addAttribute(account);
            model.addAttribute(post);
            model.addAttribute(commentForm);
            model.addAttribute("commentList", commentRepository.findCommentWithAccountByPostOrderByModifiedAtDesc(post));
            return "post/detail";
        }

        postService.addCommentToPost(account, commentForm);

        return "redirect:/post/" + commentForm.getPostId();
    }
}
