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
        Post post = createPost(postForm, account);

        Set<Tag> tags = tagRepository.findAllByTitleIn(Arrays.stream(postForm.getTags().split(",")).filter(t -> !t.isBlank()).collect(Collectors.toList()));
        post.saveTags(tags);
        account.savePost(post);

        postRepository.save(post);
        accountRepository.save(account);
    }

    private Post createPost(PostForm postForm, Account account) {

        Post post = new Post();

        post.createNewPost(postForm.getContext(), account);

        return post;

    }

    public List<Post> getPostsWithTags(Account account) {
        return postRepository.findAllWithTagsByAccount(account);
    }

    public void updatePost(Post post, PostForm postForm) {

        Set<Tag> tags = tagRepository.findAllByTitleIn(Arrays.stream(postForm.getTags().split(",")).filter(t -> !t.isBlank()).collect(Collectors.toList()));

        post.updatePost(postForm.getContext(), tags);

    }
}
