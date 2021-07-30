package com.anpopo.social.interest;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Getter @EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE interest SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
@Entity
public class Interest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String interest;

    private Integer numberOfAccount;

    private boolean deleted = false;

    private boolean hotInterest;

    private boolean superHotInterest;
}
