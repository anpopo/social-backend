package com.anpopo.social.module.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data @NoArgsConstructor
@Builder @AllArgsConstructor
public class CommentForm {

    private Long postId;

    @Length(max = 198, message = "140자 이내로 작성해 주세요.")
    private String comment;
}
