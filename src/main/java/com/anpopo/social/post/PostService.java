package com.anpopo.social.post;

import com.anpopo.social.account.repository.AccountRepository;
import com.anpopo.social.account.domain.Account;
import com.anpopo.social.tag.Tag;
import com.anpopo.social.tag.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class PostService {

    private final AccountRepository accountRepository;
    private final PostRepository postRepository;
    private final TagRepository tagRepository;

    public void savePost(Account account, PostForm postForm) {

        Post post = new Post(
                postForm.getContext(),
                account,
                postForm.getPostImage1(), postForm.getPostImage2(), postForm.getPostImage3()
        );

        // 태그 저장
        List<String> tagTitles = Arrays.stream(postForm.getHiddenTags().split("\\|")).filter(t -> !t.isBlank()).collect(Collectors.toList());
        Set<Tag> tags = tagRepository.findByTitleIn(tagTitles);

        post.saveTags(tags);

        postRepository.save(post);
    }


    public void updatePost(Long id, PostForm postForm) {

        Post post = postRepository.findPostWithTagsById(id);

        // 태그 저장
        List<String> tagTitles = Arrays.stream(postForm.getHiddenTags().split("\\|")).filter(t -> !t.isBlank()).collect(Collectors.toList());
        Set<Tag> tags = tagRepository.findByTitleIn(tagTitles);

        post.updatePost(postForm.getContext(), tags, postForm.getPostImage1(), postForm.getPostImage2(), postForm.getPostImage3() );

    }
}
