package com.anpopo.social.interest;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Getter @EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor
@SQLDelete(sql = "UPDATE interest SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
@Entity
public class Interest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String interest;

    private Integer numberOfAccount = 0;

    private Integer numberOfPost = 0;

    private boolean deleted = false;

    private boolean hotInterest = false;

    private boolean superHotInterest = false;

    public void createNewInterest(String interest) {
        this.interest = interest;
    }

    public void addNumberOfPost() {
        this.numberOfPost++;
    }

    public void minusNumberOfPost() {
        this.numberOfPost--;
    }

    public void addNumberOfAccount() {
        this.numberOfAccount++;
    }

    public void minusNumberOfAccount() {
        this.numberOfAccount--;
    }
}
