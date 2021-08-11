package com.anpopo.social.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class PostForm {

    private Long id;
    @Length(max = 1000)
    private String context;
    private String hiddenTags;
    private String tags;
    @NotBlank(message = "관심 주제를 선택해 주세요.")
    private String interest;
    @NotBlank(message = "1개의 이미지를 선택 하셔야 합니다.")
    private String postImage1;
    private String postImage2;
    private String postImage3;
}
