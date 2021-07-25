package com.anpopo.social.settings.form;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data @NoArgsConstructor
public class ProfileForm {

    private String nickname;

    @Length(max = 200)
    private String bio;
    private String profileImage;

}
