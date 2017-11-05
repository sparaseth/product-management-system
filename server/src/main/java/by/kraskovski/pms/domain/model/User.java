package by.kraskovski.pms.domain.model;

import by.kraskovski.pms.domain.model.base.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Description entity by database table "user"
 */
@Entity
@Table(name = "\"user\"")
@NoArgsConstructor
@Getter
@Setter
public class User extends BaseEntity {

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_authority",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_id")})
    private List<Authority> authorities = new ArrayList<>();

    @OneToOne(
            mappedBy = "user",
            orphanRemoval = true,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private Cart cart;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phone;

    private String firstName;

    private String lastName;

    private LocalDateTime createDate;

    private String avatar;

    public User(final String username, final String password) {
        this.username = username;
        this.password = password;
    }

    public User(final String id, final String username, final String password) {
        this.setId(id);
        this.username = username;
        this.password = password;
    }


    public Cart getCart() {
        return this.cart;
    }

    public void createCart() {
        this.cart = new Cart();
        cart.setUser(this);
    }

    public void removeCart() {
        if (Objects.nonNull(cart)) {
            cart.setUser(null);
        }
        this.cart = null;
    }
}
