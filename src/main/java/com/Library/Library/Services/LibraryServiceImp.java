package com.Library.Library.Services;

import com.Library.Library.Entities.AuditDTO;
import com.Library.Library.Entities.BookDTO;
import com.Library.Library.Entities.UserDTO;
import com.Library.Library.Exceptions.BookException;
import com.Library.Library.Exceptions.UserException;
import com.Library.Library.Interface.LibraryService;
import com.Library.Library.Repository.AuditRepository;
import com.Library.Library.Repository.BookRepository;
import com.Library.Library.Repository.UserRepository;
import com.Library.Library.enums.Genre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class LibraryServiceImp implements LibraryService {

    final long FINE = 5;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    AuditRepository auditRepository;

    @Override
    public String test(){
        return "test";
    }

    @Override
    public String createUser(List<UserDTO> user){
        try {
            List<UserDTO> savedUser = userRepository.saveAll(user);
            return "Users created successfully";
        } catch (Exception e) {
            return "Failed to create user. Error: " + e.getMessage();
        }
    }

    @Override
    public String addBook(List<BookDTO> books) {
        try {
            books.forEach(book -> book.setAvailabilityStatus(true));
            List<BookDTO> savedBook = bookRepository.saveAll(books);
            return "Book added successfully";
        } catch (Exception e) {
            return "Failed to create user. Error: " + e.getMessage();
        }
    }

    @Override
    public List<UserDTO> allUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<BookDTO> allBooks() {
        return bookRepository.findAll();
    }

    @Override
    public String issueBook(Long bookId,Long userId,int days){
        try{
            Optional<UserDTO> userOptional = userRepository.findById(userId);

            if(userOptional.isEmpty()){
                throw new Exception("User not present");
            }

            Optional<BookDTO> bookOptional = bookRepository.findById(bookId);
            if (bookOptional.isPresent()) {
                BookDTO book = bookOptional.get();
                UserDTO user = userOptional.get();
                if (book.isAvailabilityStatus()) {
                    book.setIssuedTo(user);
                    book.setIssuedWhen(LocalDate.now());
                    book.setIssuedTill(LocalDate.now().plusDays(days));
                    book.setAvailabilityStatus(false);
                    bookRepository.save(book);
                    return "Book issued successfully";
                } else {
                    throw new Exception( book.getTitle() + " book is not available until " + book.getIssuedTill().toString());
                }
            } else {
                throw new Exception("Book with ID " + bookId + " not found");
            }

        }catch (Exception e) {
            return e.getMessage();
        }
    }

    @Override
    public String returnIssuedBooks(long userId, List<Long> bookIds, String returningDate) throws UserException {
        Optional<UserDTO> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty())
            throw new UserException(UserException.ErrorCode.USER_NOT_FOUND);
        UserDTO user = optionalUser.get();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate returnDate = LocalDate.parse(returningDate, formatter);
        for(long bookId: bookIds){
            Optional<BookDTO> optionalBook = bookRepository.findById(bookId);
            if(optionalBook.isPresent()){
                BookDTO book = optionalBook.get();
                if(book.getIssuedTo() != null && book.getIssuedTo().equals(user)){
                    book.setIssuedTo(null);
                    book.setAvailabilityStatus(true);
                    book.setIssuedWhen(null);
                    LocalDate actualReturnDate = book.getIssuedTill();

                    //create Audit
                    AuditDTO audit=new AuditDTO();
                    audit.setBook(book);
                    audit.setUser(user);
                    audit.setReturnDate(returnDate);
                    audit.setActualReturnDate(actualReturnDate);

                    if(returnDate.isAfter(actualReturnDate)){
                        long extraDay = Math.abs(ChronoUnit.DAYS.between(actualReturnDate,returnDate));
                        audit.setFine(extraDay * FINE);
                        audit.setFinePaid(false);
                    }

                    book.setIssuedTill(null);
                    auditRepository.save(audit);
                    bookRepository.save(book);
                }else{
                    return "Book with ID " + bookId + " is not currently issued to the user.";
                }
            }
        }
        return "Books returned successfully.";
    }

    @Override
    public ResponseEntity<List<BookDTO>> getBooksIssuedToUser(Long id) throws UserException {
            Optional<UserDTO> user = userRepository.findById(id);
            if(user.isEmpty()){
                throw new UserException(UserException.ErrorCode.USER_NOT_FOUND);
            }
            return ResponseEntity.ok(user.get().getBooksIssued());
    }

    @Override
    public List<BookDTO> searchBooks(String title, String author, Genre genre, Integer publicationYear) {
        Specification<BookDTO> specification = Specification
                .<BookDTO>where(author != null ? ((root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("author")), '%' + author.toLowerCase() + '%')) : null)
                .and(title != null ? ((root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), '%' + title.toLowerCase() + "%")) : null)
                .and(genre != null ? ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("genre"),genre)) : null)
                .and(publicationYear != null ? ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("publicationYear"), publicationYear)) : null);


        return bookRepository.findAll(specification);
    }

    @Override
    public String deleteBook(long id) throws BookException {
        Optional<BookDTO> bookOptional = bookRepository.findById(id);
        if(bookOptional.isEmpty()){
            throw new BookException(BookException.ERROR_CODE.Book_NOT_FOUND);
        }
        BookDTO book = bookOptional.get();
        if(!book.isAvailabilityStatus()) return "Book is currently issued to a user";
        bookRepository.deleteById(id);
        return "Book Removed Successfully";
    }

    @Override
    public long getFine(long userId) throws UserException {
        UserDTO user = getUser(userId);
        return totalFine(user);
    }

    @Override
    public String payFine(long userId, long amount) throws UserException {
        UserDTO user = getUser(userId);

        long totalFine = totalFine(user);
        if(amount > totalFine){
            return String.format("%s has only %d rupees fine",user.getName(),totalFine);
        }
        updateFinePayment(user,amount);
        return "Success";
    }

    @Override
    public String removeUser(long userId) throws UserException {
        UserDTO user = getUser(userId);

        if(!user.getBooksIssued().isEmpty()) return "All the books issued have not been return till now.";

        long fine = totalFine(user);
        if(fine != 0) return String.format("Kindly clear you %d rupees fine",fine);

        userRepository.deleteById(userId);

        return "User removed successfully";
    }

    private void updateFinePayment(UserDTO user,long partialAmount){
        List<AuditDTO> audits = user.getAudit();
        for(AuditDTO audit:audits){
            long fine = audit.getFine();
            if (partialAmount >= fine) {
                audit.setFine(0);
                audit.setFinePaid(true);
                partialAmount -= fine;
            }else{
                audit.setFine(fine - partialAmount);
                partialAmount = 0;
            }
            auditRepository.save(audit);
            if(partialAmount == 0) break;
        }
    }

    private UserDTO getUser(long userId) throws UserException {
        return userRepository.findById(userId).orElseThrow(() -> new UserException(UserException.ErrorCode.USER_NOT_FOUND));
    }
    private Long totalFine(UserDTO user) {

        List<AuditDTO> audits = user.getAudit();

        return audits.stream()
                .filter((audit) -> !audit.isFinePaid())
                .map(AuditDTO::getFine)
                .reduce(0L,Long::sum);
    }


}
