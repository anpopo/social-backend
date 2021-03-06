package com.anpopo.social.module.interest;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    @Column(nullable = false)
    private Integer numberOfAccount = 0;

    @Column(nullable = false)
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
