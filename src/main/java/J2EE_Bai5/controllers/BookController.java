package J2EE_Bai5.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;
import java.io.File;
import java.io.IOException;
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
            try {
                File devFolder = new File("src/main/resources/static/images");
                devFolder.mkdirs();
                File devDest = new File(devFolder, fileName);
                file.transferTo(devDest);

                File targetFolder = new File("target/classes/static/images");
                targetFolder.mkdirs();
                File targetDest = new File(targetFolder, fileName);
                if (!devDest.getAbsolutePath().equals(targetDest.getAbsolutePath())) {
                    java.nio.file.Files.copy(devDest.toPath(), targetDest.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }

                book.setImage(fileName);
                System.out.println("Saved file to: " + devDest.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("uploadError", "Failed to save image: " + e.getMessage());
                model.addAttribute("categories", categoryService.getAllCategories());
                return "book/create";
            }
        }

        if (book.getCategory() != null) {
            int catId = book.getCategory().getId();
            if (catId > 0) {
                Category managed = categoryService.getCategoryById(catId);
                if (managed == null) {
                    model.addAttribute("categories", categoryService.getAllCategories());
                    model.addAttribute("uploadError", "Selected category not found.");
                    return "book/create";
                }
                book.setCategory(managed);
            } else {
                model.addAttribute("categories", categoryService.getAllCategories());
                model.addAttribute("uploadError", "Please select a category.");
                return "book/create";
            }
        } else {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("uploadError", "Please select a category.");
            return "book/create";
        }

        Book saved = bookService.saveBook(book);
        System.out.println("Saved entity: id=" + saved.getId() + " image=" + saved.getImage());
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
            model.addAttribute("categories", categoryService.getAllCategories());
            return "book/edit";
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