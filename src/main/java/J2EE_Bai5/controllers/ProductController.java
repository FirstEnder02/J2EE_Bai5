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
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String listProducts(Model model) {
        List<Product> productList = productService.getAllProducts();
        model.addAttribute("products", productList);
        return "product/product";
    }

    @PostMapping("/create")
public String create(@Valid @ModelAttribute("product") Product product,
                     BindingResult bindingResult,
                     @RequestParam(value = "imageProduct", required = false) MultipartFile file,
                     Model model) throws IOException {

    if (bindingResult.hasErrors()) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "product/create";
    }

    if (file != null && !file.isEmpty()) {
        File uploadFolder = new File("src/main/resources/static/images");
        uploadFolder.mkdirs();
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        file.transferTo(new File(uploadFolder, fileName));
        product.setImage(fileName);
    }

    productService.saveProduct(product);
    return "redirect:/products";
}


@GetMapping("/create")
public String showCreateForm(Model model) {
    Product p = new Product();
    p.setCategory(new Category());
    model.addAttribute("product", p);
    model.addAttribute("categories", categoryService.getAllCategories());
    return "product/create";
}



    @PostMapping("/save")
    public String saveProduct(@Valid @ModelAttribute("product") Product product,
                            BindingResult bindingResult,
                            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "product/create";
        }
        productService.saveProduct(product);
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
public String showEdit(@PathVariable int id, Model model) {
    model.addAttribute("product", productService.getProductById(id));
    model.addAttribute("categories", categoryService.getAllCategories());
    return "product/edit";
}


    @PostMapping("/edit")
    public String update(@Valid @ModelAttribute Product product,
                        BindingResult bindingResult,
                        @RequestParam(value = "imageProduct", required = false) MultipartFile file,
                        Model model) throws IOException {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "product/edit";
        }

        Product existing = productService.getProductById(product.getId());

        if (file != null && !file.isEmpty()) {
            File uploadFolder = new File("target/classes/static/images");
            uploadFolder.mkdirs();
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            file.transferTo(new File(uploadFolder, fileName));
            product.setImage(fileName);
        } else if (existing != null) {
            product.setImage(existing.getImage());
        }

        productService.saveProduct(product);
        return "redirect:/products";
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Integer id) {
        productService.deleteProduct(id);
        return "redirect:/products";
    }
}