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
@RequestMapping("/Books")
public class BookController {

    @Autowired
    private BookService BookService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String listBooks(Model model) {
        List<Book> BookList = BookService.getAllBooks();
        model.addAttribute("Books", BookList);
        return "Book/Book";
    }

    @PostMapping("/create")
public String create(@Valid @ModelAttribute("Book") Book Book,
                     BindingResult bindingResult,
                     @RequestParam(value = "imageBook", required = false) MultipartFile file,
                     Model model) throws IOException {

    if (bindingResult.hasErrors()) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "Book/create";
    }

    if (file != null && !file.isEmpty()) {
        File uploadFolder = new File("src/main/resources/static/images");
        uploadFolder.mkdirs();
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        file.transferTo(new File(uploadFolder, fileName));
        Book.setImage(fileName);
    }

    BookService.saveBook(Book);
    return "redirect:/Books";
}


@GetMapping("/create")
public String showCreateForm(Model model) {
    Book p = new Book();
    p.setCategory(new Category());
    model.addAttribute("Book", p);
    model.addAttribute("categories", categoryService.getAllCategories());
    return "Book/create";
}



    @PostMapping("/save")
    public String saveBook(@Valid @ModelAttribute("Book") Book Book,
                            BindingResult bindingResult,
                            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "Book/create";
        }
        BookService.saveBook(Book);
        return "redirect:/Books";
    }

    @GetMapping("/edit/{id}")
public String showEdit(@PathVariable int id, Model model) {
    model.addAttribute("Book", BookService.getBookById(id));
    model.addAttribute("categories", categoryService.getAllCategories());
    return "Book/edit";
}


    @PostMapping("/edit")
    public String update(@Valid @ModelAttribute Book Book,
                        BindingResult bindingResult,
                        @RequestParam(value = "imageBook", required = false) MultipartFile file,
                        Model model) throws IOException {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "Book/edit";
        }

        Book existing = BookService.getBookById(Book.getId());

        if (file != null && !file.isEmpty()) {
            File uploadFolder = new File("target/classes/static/images");
            uploadFolder.mkdirs();
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            file.transferTo(new File(uploadFolder, fileName));
            Book.setImage(fileName);
        } else if (existing != null) {
            Book.setImage(existing.getImage());
        }

        BookService.saveBook(Book);
        return "redirect:/Books";
    }

    @PostMapping("/delete/{id}")
    public String deleteBook(@PathVariable("id") Integer id) {
        BookService.deleteBook(id);
        return "redirect:/Books";
    }
}