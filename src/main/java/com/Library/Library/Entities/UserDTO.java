package com.Library.Library.Entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "users")
public class UserDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String name;

    @OneToMany(mappedBy = "issuedTo")
    private List<BookDTO> booksIssued;

    @OneToMany(mappedBy = "user" , cascade = CascadeType.ALL)
    List<AuditDTO> audit;
}
