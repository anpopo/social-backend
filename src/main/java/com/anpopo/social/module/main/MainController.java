package com.anpopo.social.module.main;

import com.anpopo.social.module.account.domain.Account;
import com.anpopo.social.module.account.CurrentUser;
import com.anpopo.social.module.account.domain.Follow;
import com.anpopo.social.module.account.repository.FollowRepository;
import com.anpopo.social.module.notification.NotificationRepository;
import com.anpopo.social.module.post.Post;
import com.anpopo.social.module.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Controller
public class MainController {

    private final PostRepository postRepository;
    private final FollowRepository followRepository;

    @GetMapping("/")
    public String home(@CurrentUser Account account, Model model) {
        if (account != null) {
            model.addAttribute(account);
            // 로그인 한 계정의 포스팅과 팔로잉들의 포스팅 목록을 시간순서로 정렬해서 20개만 보여주자 일단 페이징 처리해야 함.

            List<Follow> following = followRepository.findFollowWithFollowedAccountByFollowAndIsAccepted(account, true);

            List<Account> followingAccountList = following.stream().map(Follow::getFollowed).collect(Collectors.toList());
            followingAccountList.add(account);

            List<Post> followingPostList = postRepository.findFirst20PostWithTagsWithInterestWithAccountByAccountInOrderByPostedAtDesc(followingAccountList);

            model.addAttribute("postList", followingPostList);

            // TODO 추천 친구 보여주기

            return "index-after-login";

        }
        return "index";
    }

    @GetMapping("/login")
    public String login(@CurrentUser Account account) {
        if(account != null) {
            return "redirect:/";
        }

        return "login";
    }
}
