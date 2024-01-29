package com.Library.Library.Entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.LocalDate;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "audit")
@Data
public class AuditDTO {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    long id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    BookDTO book;

    @ManyToOne
    @JoinColumn(name = "user_id")
    UserDTO user;

    @Column(name = "actual_return_date")
    LocalDate actualReturnDate;

    @Column(name = "return_date")
    LocalDate returnDate;

    @Column(name = "fine_paid")
    @ColumnDefault("true")
    boolean finePaid;

    long fine;
}
