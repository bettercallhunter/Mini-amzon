package com.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SpringBootApplication
@RestController
public class Index {
    private final AccountRepository accountRepository;
    public Index(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }
    public static void main(String[] args){
        SpringApplication.run(Index.class,args);
    }
    @GetMapping("/")
    public List<Account>getAccounts(){
        return accountRepository.findAll();
    }
//    class hreetResponse{
//        private final String msg;
//        hreetResponse(String b){
//            this.msg = b;
//        }
//        public String gethreet(){
//            return msg;
//        }
//        @Override
//        public String toString(){
//            return "Greet response{" + msg +"}";
//        }

}

