package com.anpopo.social.module.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data @NoArgsConstructor
@Builder @AllArgsConstructor
public class CommentForm {

    private Long postId;

    @Length(max = 1100, message = "1000자 이내로 작성해 주세요.")
    private String comment;
}
