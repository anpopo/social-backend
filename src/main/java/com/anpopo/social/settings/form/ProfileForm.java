package com.anpopo.social.settings.form;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data @NoArgsConstructor
public class ProfileForm {

    private String nickname;

    @Length(max = 130, message = "100자 이내로 작성해주세요.(띄어쓰기 포함)")
    private String bio;
    private String profileImage;

}
