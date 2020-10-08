package cn.lx.security.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
public class ProductController {

    //@Secured("PRODUCT_LIST")
    @PreAuthorize(value = "hasAuthority('PRODUCT_LIST')")
    @RequestMapping("/findAll")
    public String findAll(){
        return "product-list";
    }
}
