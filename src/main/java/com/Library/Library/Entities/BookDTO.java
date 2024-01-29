package com.Library.Library.Entities;

import com.Library.Library.enums.Genre;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "books")
@Data
public class BookDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;

    @ManyToOne
    @JoinColumn(name = "issued_to")
    @JsonIgnore
    private UserDTO issuedTo;

    @Column(name = "issued_when")
    private LocalDate issuedWhen;

    @Column(name = "issued_till")
    private LocalDate issuedTill;

    @Column(name = "availability_status")
    private boolean availabilityStatus;

    private Genre genre;

    @Column(name = "publication_year")
    private Integer publicationYear;
}
