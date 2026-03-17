package J2EE_Bai5.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import J2EE_Bai5.models.Book;
import J2EE_Bai5.repository.BookRepository;

import java.util.List;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;

    public List<Book> getAllBooks() { return bookRepository.findAll(); }
    public void saveBook(Book book) { bookRepository.save(book); }
    public Book getBookById(int id) { return bookRepository.findById(id).orElse(null); }
    public Book updateBook(Book book) { return bookRepository.save(book); }
    public void deleteBook(int id) { bookRepository.deleteById(id); }
}