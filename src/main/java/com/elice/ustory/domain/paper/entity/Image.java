package com.elice.ustory.domain.paper.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "image")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "paper_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_PAPER_ID"))
    private Paper paper;

    @Column(name = "image_url", nullable = false, columnDefinition = "varchar(1000)")
    private String imageUrl;

    @Column(name = "sequence", nullable = false, columnDefinition = "tinyint")
    private int sequence;

    public Image(String imageUrl, int sequence) {
        this.imageUrl = imageUrl;
        this.sequence = sequence;
    }

    public Image update(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public void setPaper(Paper paper){
        this.paper = paper;

        if(!paper.getImages().contains(this)) {
            paper.getImages().add(this);
        }
    }

}
