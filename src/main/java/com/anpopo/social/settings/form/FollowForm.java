package com.anpopo.social.settings.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
@AllArgsConstructor
public class FollowForm {

    private Long id;
    private String nickname;
    private String profileImage;

}