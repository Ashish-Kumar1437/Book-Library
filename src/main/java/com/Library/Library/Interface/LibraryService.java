package com.Library.Library.Interface;

import com.Library.Library.Entities.BookDTO;
import com.Library.Library.Entities.UserDTO;
import com.Library.Library.Exceptions.BookException;
import com.Library.Library.Exceptions.UserException;
import com.Library.Library.enums.Genre;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface LibraryService {

    String test();

    String createUser(List<UserDTO> user);

    List<UserDTO> allUsers();

    ResponseEntity<List<BookDTO>> getBooksIssuedToUser(Long userId) throws UserException;

    String addBook(List<BookDTO> book);

    List<BookDTO> allBooks();

    String issueBook(Long bookId,Long userId,int dates);

    String returnIssuedBooks(long userId, List<Long> bookIds, String returningDate) throws UserException;

    List<BookDTO> searchBooks(String title, String Author, Genre genre,Integer publicationYear);

    String deleteBook(long id) throws BookException;

    long getFine(long userId) throws UserException;

    String payFine(long userId,long amount) throws UserException;

    String removeUser(long userId) throws UserException;
}
