package com.Library.Library.Controllers;

import com.Library.Library.Entities.BookDTO;
import com.Library.Library.Entities.UserDTO;
import com.Library.Library.Exceptions.BookException;
import com.Library.Library.Exceptions.UserException;
import com.Library.Library.Interface.LibraryService;
import com.Library.Library.enums.Genre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/library")
public class LibraryController {

    @Autowired
    LibraryService libraryService;

    @GetMapping("")
    public String test(){
       return libraryService.test();
    }

    @PostMapping("/createUser")
    public String createUser(@RequestBody()List<UserDTO> user){
        return libraryService.createUser(user);
    }

    @PostMapping("/addBook")
    public String addBook(@RequestBody() List<BookDTO> book){
        return libraryService.addBook(book);
    }

    @GetMapping("/users")
    public List<UserDTO> allUsers(){
        return libraryService.allUsers();
    }

    @GetMapping("/books")
    public List<BookDTO> allBooks(){
        return libraryService.allBooks();
    }

    @GetMapping("/issueBook")
    public String issueBook(@RequestParam(name = "bookId")Long bookId,@RequestParam(name = "userId")Long userId,@RequestParam(name = "days")int days){
        return libraryService.issueBook(bookId,userId,days);
    }

    @PostMapping("/returnBook")
    public String returnIssuedBooks(@RequestParam(name = "userId")long userId, @RequestBody()List<Long> bookIds,@RequestParam(name = "date")String returningDate) throws UserException{
        return libraryService.returnIssuedBooks(userId,bookIds,returningDate);
    }

    @GetMapping("/issuedBooks")
    public ResponseEntity<List<BookDTO>> getBooksIssuedToUser(@RequestParam(name = "userId")Long id) throws UserException {
        return libraryService.getBooksIssuedToUser(id);
    }

    @GetMapping("/searchBook")
    public List<BookDTO> searchBooks(
            @RequestParam(required = false)String title,
            @RequestParam(required = false)String author,
            @RequestParam(required = false)Genre genre,
            @RequestParam(required = false)Integer publication
    ){
        return libraryService.searchBooks(title,author,genre,publication);
    }

    @GetMapping("/removeBook/{id}")
    public String deleteBook(@PathVariable(value = "id")long id) throws BookException {
        return libraryService.deleteBook(id);
    }

    @GetMapping("/fine/{id}")
    public long getFine(@PathVariable(name = "id") long id) throws UserException{
        return libraryService.getFine(id);
    }

    @GetMapping("/payFine")
    public String payFine(@RequestParam long userId,@RequestParam long amount) throws UserException {
        return libraryService.payFine(userId,amount);
    }

    @GetMapping("/removeUser")
    public String removeUser(@RequestParam long userId) throws UserException {
        return libraryService.removeUser(userId);
    }
}
