package com.example.demo;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
public class MyController {

    @GetMapping("/hello")
    public Object hello(@RequestParam(required = false) String name, HttpSession session, Model model) {
//        System.out.println("servletRequest: " + servletRequest.getRemoteAddr());

        if (name != null) {
            session.setAttribute("name", name);
        } else {
            name = (String) session.getAttribute("name");
        }
        model.addAttribute("name", name);
        return "hello";
    }

    @GetMapping("/listProducts")
    public List<Product> listProducts() {
        return products;
    }

    List<Product> products = Collections.synchronizedList(new ArrayList<>());

    {
        products.add(new Product("Product1", 1, true));
        products.add(new Product("Product2", 10, true));
        products.add(new Product("Product3", 100, false));
    }

    @PostMapping("/postForm")
    public String processForm(@ModelAttribute Product product) {
//        System.out.println("name =" + name + " price = " + price + " model = " + model.asMap());
        System.out.println("product =" + product);
        products.add(product);
        new Product();
        return "redirect:/listProducts";
    }

}

class Product {

    private String name;
    private int price;
    private boolean inStock;

    public Product(String name, int price, boolean inStock) {
        this.name = name;
        this.price = price;
        this.inStock = inStock;
    }

    public Product() {
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", inStock=" + inStock +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }
}
