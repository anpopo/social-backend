package com.anpopo.social.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class PostForm {

    private Long id;
    @Length(max = 1000)
    private String context;
    private String hiddenTags;
    private String tags;
    private String postImage1;
    private String postImage2;
    private String postImage3;
}
