package io.elice.shoppingmall.product.entity;

import io.elice.shoppingmall.cart.entity.Cart;
import io.elice.shoppingmall.cart.entity.CartItem;
import io.elice.shoppingmall.category.entity.Category;
import io.elice.shoppingmall.etc.entity.Event;
import io.elice.shoppingmall.user.entity.BookWishList;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "book")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
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

    @Column(name = "image_url")
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

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    @Column(name = "modified_date")
    private Date modifiedDate;

    @OneToMany(mappedBy = "book")
    private List<BookWishList> bookWishList = new ArrayList<>();

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "author_id")
    private Author author;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "category_id")
    private Category category;



    @OneToOne(mappedBy = "book")
    private Event event;

    @OneToMany(mappedBy = "book")
    private List<RentalBook> rentalBook;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL) // book 삭제되면 리뷰도 같이 삭제
    private List<Review> review;

}
