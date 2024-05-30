package io.elice.shoppingmall.product.entity;

import io.elice.shoppingmall.cart.entity.Cart;
import io.elice.shoppingmall.category.entity.Category;
import io.elice.shoppingmall.etc.entity.Event;
import io.elice.shoppingmall.etc.entity.Review;
import io.elice.shoppingmall.order.entity.OrderLineBook;
import io.elice.shoppingmall.user.entity.BookWishList;
import io.elice.shoppingmall.user.entity.WishList;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "book")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String publisher;

    @Column(nullable = false, name = "published_date")
    private Date publishedDate;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, name = "image_url")
    private String imageURL;

    @Column(nullable = false, name = "total_stock_quantity")
    private int totalStockQuantity;

    @Column(nullable = false)
    private int page;

    @Column(name = "average_rate")
    private double averageRate;

    @Column(name = "review_count", columnDefinition = "int default 0")
    private int reviewCount;

    @Column(name = "wish_count", columnDefinition = "int default 0")
    private int wishCount;

    @OneToMany(mappedBy = "book")
    private List<BookWishList> bookWishList = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "author_id")
    private Author author;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToOne(mappedBy = "book", cascade = CascadeType.REMOVE)
    private Cart cart;

    @OneToOne(mappedBy = "book")
    private Event event;

    @OneToMany(mappedBy = "book")
    private List<RentalBook> rentalBook;

    @OneToMany(mappedBy = "book")
    private List<Review> review;
}
