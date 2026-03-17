package J2EE_Bai5.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

import J2EE_Bai5.models.*;
import J2EE_Bai5.service.*;
import jakarta.validation.Valid;

import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String listBooks(Model model) {
        List<Book> bookList = bookService.getAllBooks();
        model.addAttribute("books", bookList);
        return "book/book";
    }
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        Book b = new Book();
        b.setCategory(new Category());
        model.addAttribute("book", b);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "book/create";
    }

     @Value("${app.upload.dir:#{null}}")
    private String configuredUploadDir;

    private java.nio.file.Path getProjectImagesPath() {
        if (configuredUploadDir != null && !configuredUploadDir.isBlank()) {
            return Paths.get(configuredUploadDir).toAbsolutePath();
        }
        String userDir = System.getProperty("user.dir");
        return Paths.get(userDir, "src", "main", "resources", "static", "images").toAbsolutePath();
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("book") Book book,
                         BindingResult bindingResult,
                         @RequestParam(value = "imageBook", required = false) MultipartFile file,
                         Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "book/create";
        }

        if (file != null && !file.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename().replaceAll("\\s+", "_");
            java.nio.file.Path imagesPath = getProjectImagesPath();
            File folder = imagesPath.toFile();
            if (!folder.exists() && !folder.mkdirs()) {
                model.addAttribute("uploadError", "Could not create upload directory: " + imagesPath);
                model.addAttribute("categories", categoryService.getAllCategories());
                return "book/create";
            }
            File dest = imagesPath.resolve(fileName).toFile();
            try {
                file.transferTo(dest);
                book.setImage(fileName);
            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("uploadError", "Failed to save image: " + e.getMessage());
                model.addAttribute("categories", categoryService.getAllCategories());
                return "book/create";
            }
        }
        bookService.saveBook(book);
        return "redirect:/books";
    }

    @GetMapping("/edit/{id}")
    public String showEdit(@PathVariable int id, Model model) {
        Book book = bookService.getBookById(id);
        if (book == null) { return "redirect:/books"; }
        if (book.getCategory() == null) { book.setCategory(new Category()); }
        model.addAttribute("book", book);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "book/edit";
    }

    @PostMapping("/edit")
    public String update(@Valid @ModelAttribute("book") Book book,
                         BindingResult bindingResult,
                         @RequestParam(value = "imageBook", required = false) MultipartFile file,
                         Model model) throws IOException {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(err -> System.out.println(err));
            model.addAttribute("categories", categoryService.getAllCategories());
            return "book/create";
        }
        Book existing = bookService.getBookById(book.getId());
        if (file != null && !file.isEmpty()) {
            File uploadFolder = new File("src/main/resources/static/images");
            uploadFolder.mkdirs();
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            file.transferTo(new File(uploadFolder, fileName));
            book.setImage(fileName);
        } else if (existing != null) {
            book.setImage(existing.getImage());
        }
        bookService.saveBook(book);
        return "redirect:/books";
    }

    @PostMapping("/delete/{id}")
    public String deleteBook(@PathVariable("id") Integer id) {
        bookService.deleteBook(id);
        return "redirect:/books";
    }
}