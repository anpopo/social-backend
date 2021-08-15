package com.anpopo.social.module.notification;

import com.anpopo.social.module.account.UserAccount;
import com.anpopo.social.module.account.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Component
public class NotificationInterceptor implements HandlerInterceptor {

    private final NotificationRepository notificationRepository;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (modelAndView != null && !isRedirectView(modelAndView) && authentication != null && authentication.getPrincipal() instanceof UserAccount) {
            Account account = ((UserAccount) authentication.getPrincipal()).getAccount();
            List<Notification> notifications = notificationRepository.findByCreatedAtBefore(LocalDateTime.now().minusDays(30));

            if (!notifications.isEmpty()) {
                notificationRepository.deleteAll(notifications);
            }

            long count = notificationRepository.countByAccountAndChecked(account, false);

            modelAndView.addObject("hasNotification", count > 0);
            if (count > 0) {
                modelAndView.addObject("notificationCount", count);
            }
        }
    }

    private boolean isRedirectView(ModelAndView modelAndView) {
        return modelAndView.getViewName().startsWith("redirect:") || modelAndView.getView() instanceof RedirectView;
    }
}