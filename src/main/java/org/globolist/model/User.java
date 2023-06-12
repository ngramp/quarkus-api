package org.globolist.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.*;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
import org.jboss.logging.Logger;


@Entity
@Table(name = "users")
@UserDefinition
public class User extends PanacheEntityBase {
    private static final Logger LOG = Logger.getLogger(User.class);

    @Id
    @Username
    public String username;
    @Password
    public String password;
    @Roles
    public String role;

    protected User(){}

    public User(String username){
        super();
        this.username = username;
    }
    public User(String username, String password, String role){
        super();
        this.username = username;
        this.password = BcryptUtil.bcryptHash(password);
        this.role = role;
    }


    public static Uni<User> add(String username, String password, String role) {
        User user = new User(username);
        user.password = BcryptUtil.bcryptHash(password);
        user.role = role; //validate more
        return user.persist();
    }
    public static Uni<User> findByUsername(String username) {
        return find("username", username).firstResult();
    }

    public static Uni<User> authenticateUser(User creds) {
        return findByUsername(creds.username)
                .onItem()
                .ifNotNull()
                .transformToUni(user -> {
                    if (user.password.equals(BcryptUtil.bcryptHash(creds.password))) {
                        return Uni.createFrom().item(user);
                    } else {
                        return Uni.createFrom().nullItem();
                    }
                });
    }

}