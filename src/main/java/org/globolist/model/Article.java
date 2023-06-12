package org.globolist.model;

import java.util.List;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.smallrye.mutiny.Uni;

@Entity
@Table(name = "articles")
public class Article extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @NotBlank
    public String title;

    @NotBlank
    public String content;

    @NotNull
    @Enumerated(EnumType.STRING)
    public ArticleType type;

    public static Uni<List<Article>> listAllArticles() {
        return listAll();
    }
}