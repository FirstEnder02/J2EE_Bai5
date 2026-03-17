package J2EE_Bai5.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import J2EE_Bai5.models.Book;
import J2EE_Bai5.repository.BookRepository;

import java.util.List;

@Service
public class BookService {
    @Autowired
    private BookRepository BookRepository;
    @Autowired

    public List<Book> getAllBooks() {
        return BookRepository.findAll();
    }

    public void saveBook(Book Book) {
        BookRepository.save(Book);
    }

    public Book getBookById(int id) {
        return BookRepository.findById(id).orElse(null);
    }

    public Book updateBook(Book Book) {
        return BookRepository.save(Book);
    }

    public void deleteBook(int id) {
        BookRepository.deleteById(id);
    }
}