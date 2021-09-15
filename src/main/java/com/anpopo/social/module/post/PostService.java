package com.anpopo.social.module.post;

import com.anpopo.social.module.account.domain.Account;
import com.anpopo.social.module.interest.Interest;
import com.anpopo.social.module.interest.InterestRepository;
import com.anpopo.social.module.post.event.FollowingInterestPostEvent;
import com.anpopo.social.module.post.repository.CommentRepository;
import com.anpopo.social.module.post.repository.PostRepository;
import com.anpopo.social.module.tag.Tag;
import com.anpopo.social.module.tag.TagRepository;
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

    private final CommentRepository commentRepository;
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

        eventPublisher.publishEvent(new FollowingInterestPostEvent(account, interest, post.getId()));
    }


    public void updatePost(Post post, PostForm postForm) {
        Interest interest = interestRepository.findByInterest(postForm.getInterest());

        // 태그 저장
        List<String> tagTitles = Arrays.stream(postForm.getHiddenTags().split("\\|")).filter(t -> !t.isBlank()).collect(Collectors.toList());
        Set<Tag> tags = tagRepository.findByTitleIn(tagTitles);

        post.updatePost(postForm.getContext(), tags, interest,
                postForm.getPostImage1(), postForm.getPostImage2(), postForm.getPostImage3() );

    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    public Integer likePost(Account account, Post post) {
        post.addLike(account);

        return post.getLikeCount();
    }
    public Integer dislikePost(Account account, Post post) {

        post.minusLike(account);
        return post.getLikeCount();
    }

    public void addCommentToPost(Account account, CommentForm commentForm) {

        Post post = postRepository.findById(commentForm.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("해당 포스트가 없습니다."));

        Comment comment = new Comment(commentForm.getComment(), account, post);

        commentRepository.save(comment);

    }

    public void addCommentReplyToComment(Account account, Long postId, Long commentId, String commentReply) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 포스트가 없습니다."));


        Comment findComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다."));


        Comment comment = new Comment(commentReply, account, post);

        findComment.setCommentFamily(comment);

        commentRepository.save(comment);

    }
}
