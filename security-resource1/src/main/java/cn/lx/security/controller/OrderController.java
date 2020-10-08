package cn.lx.security.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    //@Secured("ORDER_LIST")
    @PreAuthorize(value = "hasAuthority('ORDER_LIST')")
    @RequestMapping("/findAll")
    public String findAll(){
        return "order-list";
    }
}
