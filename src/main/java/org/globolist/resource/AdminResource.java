package org.globolist.resource;

import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.globolist.model.Article;

import java.util.List;
@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminResource {
    @GET
    @Path("/articles")
    @RolesAllowed({"ADMIN"})
    public Uni<List<Article>> listArticles() {
        return Article.listAll();
    }

    @POST
    @Path("/articles")
    @RolesAllowed({"ADMIN"})
    public Uni<Article> createArticle(Article article) {
        return article.persistAndFlush().replaceWith(article);
    }

    @PUT
    @Path("/articles/{id}")
    @RolesAllowed({"ADMIN"})
    public Uni<Article> updateArticle(@PathParam("id") Long id, Article updatedArticle) {
        return Article.<Article>findById(id)
                .onItem().ifNull().failWith(new NotFoundException())
                .invoke(existingArticle -> {
                    existingArticle.title = updatedArticle.title;
                    existingArticle.content = updatedArticle.content;
                    existingArticle.type = updatedArticle.type;
                })
                .call(Article::persistAndFlush)
                .replaceWith(updatedArticle);
    }
}