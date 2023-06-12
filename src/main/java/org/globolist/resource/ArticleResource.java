package org.globolist.resource;


import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.globolist.model.Article;

import java.util.List;


@Path("/articles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ArticleResource {
    @GET
    public Uni<List<Article>> listArticles() {
        return Article.listAllArticles();
    }

    @GET
    @Path("/{id}")
    public Uni<Article> getArticle(@PathParam("id") Long id) {
        return Article.<Article>findById(id)
                .onItem().ifNull().failWith(new NotFoundException());
    }
}