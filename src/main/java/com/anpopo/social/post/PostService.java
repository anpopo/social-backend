package com.anpopo.social.post;

import com.anpopo.social.account.repository.AccountRepository;
import com.anpopo.social.account.domain.Account;
import com.anpopo.social.interest.Interest;
import com.anpopo.social.interest.InterestRepository;
import com.anpopo.social.post.event.FollowingPostEvent;
import com.anpopo.social.post.event.InterestPostEvent;
import com.anpopo.social.tag.Tag;
import com.anpopo.social.tag.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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
    private final InterestRepository interestRepository;
    private final ApplicationEventPublisher eventPublisher;


    public void savePost(Account account, PostForm postForm) {
        // 관심 주제
        Interest interest = interestRepository.findByInterest(postForm.getInterest());
        interest.addNumberOfPost();

        Post post = new Post(
                postForm.getContext(),
                account,
                interest,
                postForm.getPostImage1(), postForm.getPostImage2(), postForm.getPostImage3()
        );

        // 태그 저장
        List<String> tagTitles = Arrays.stream(postForm.getHiddenTags().split("\\|")).filter(t -> !t.isBlank()).collect(Collectors.toList());
        Set<Tag> tags = tagRepository.findByTitleIn(tagTitles);

        post.saveTags(tags);

        postRepository.save(post);

        eventPublisher.publishEvent(new FollowingPostEvent(account, post.getId()));
        eventPublisher.publishEvent(new InterestPostEvent(interest, post.getId()));
    }


    public void updatePost(Long id, PostForm postForm) {

        Post post = postRepository.findPostWithTagsWithInterestById(id);

        Interest interest = interestRepository.findByInterest(postForm.getInterest());

        // 태그 저장
        List<String> tagTitles = Arrays.stream(postForm.getHiddenTags().split("\\|")).filter(t -> !t.isBlank()).collect(Collectors.toList());
        Set<Tag> tags = tagRepository.findByTitleIn(tagTitles);

        post.updatePost(postForm.getContext(), tags, interest, postForm.getPostImage1(), postForm.getPostImage2(), postForm.getPostImage3() );

    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
}
