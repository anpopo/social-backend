package com.anpopo.social.post;

import com.anpopo.social.account.CurrentUser;
import com.anpopo.social.account.domain.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/post")
@Controller
public class PostController {

    private final PostService postService;
    private final PostRepository postRepository;

    @GetMapping
    public String newPostView(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new PostForm());
        return "post/form";
    }

    @PostMapping
    public String createPost(@CurrentUser Account account, @Valid PostForm postForm, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return "post/form";
        }

        // 포스트 저장
        postService.savePost(account, postForm);

        return "redirect:/post/list";
    }

    @GetMapping("/list")
    public String postList(@CurrentUser Account account, Model model) {

        model.addAttribute(account);
        List<Post> posts = postService.getPostsWithTags(account);

        if (posts.size() > 0) {
            model.addAttribute("posts", posts);
        }

        return "post/list";
    }

    @GetMapping("/{id}")
    public String updatePostView(@CurrentUser Account account, @PathVariable Long id, Model model) {
        model.addAttribute(account);
        // 태그를 포함한 포스팅 가져오기
        Post post = postRepository.findPostWithTagsById(id);

        model.addAttribute(post);
        return "post/form";
    }

    @PostMapping("/{id}")
    public String updatePost (@CurrentUser Account account, @PathVariable Long id, @Valid PostForm postForm, Errors errors, Model model){
        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(postForm);
            return "post/form";
        }
        Post post = postRepository.findPostWithTagsById(id);

        postService.updatePost(post, postForm);

        return "redirect:/list";
    }

    @DeleteMapping("/{id}")
    public String deletePost(@CurrentUser Account account) {
        return "redirect:/";
    }



}
