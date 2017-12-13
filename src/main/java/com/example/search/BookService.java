package com.example.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    BookRepository bookRepository;

    void saveAuthor(Author author){
        authorRepository.save(author);
    }

    void saveBook(Book book){
        bookRepository.save(book);
    }

    List<Book> findByTitle(String title){
        return bookRepository.findByTitle(title);
    }

}