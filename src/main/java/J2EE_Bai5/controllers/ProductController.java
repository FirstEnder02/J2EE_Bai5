package J2EE_Bai5.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import J2EE_Bai5.models.*;
import J2EE_Bai5.service.*;

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
    public String create(@ModelAttribute Product product, 
                        @RequestParam("imageProduct") MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            File uploadFolder = new File("target/classes/static/images");
            uploadFolder.mkdirs();
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            file.transferTo(new File(uploadFolder, fileName));
            product.setImage(fileName);
        }
        productService.saveProduct(product);
        return "redirect:/product";
    }

    @PostMapping("/save")
    public String saveProduct(@ModelAttribute("product") Product product) {
        productService.saveProduct(product);
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEdit(@PathVariable int id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        return "product/edit";
    }

    @PostMapping("/edit")
    public String update(@ModelAttribute Product product, 
                        @RequestParam("imageProduct") MultipartFile file) throws IOException {
        
        Product existing = productService.getProductById(product.getId());
        
        if (!file.isEmpty()) {
            File uploadFolder = new File("target/classes/static/images");
            uploadFolder.mkdirs();
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            file.transferTo(new File(uploadFolder, fileName));
            product.setImage(fileName);
        } else {
            product.setImage(existing.getImage());
        }
        
        productService.saveProduct(product);
        return "redirect:/product";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Integer id) {
        productService.deleteProduct(id);
        return "redirect:/products";
    }
}