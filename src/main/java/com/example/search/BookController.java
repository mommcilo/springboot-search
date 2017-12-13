package com.example.search;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

@RestController
public class BookController {

    @Autowired
    BookService bookService;

    @PersistenceContext
    EntityManager entityManager;

    @GetMapping(path = "/books")
    public List<Book> get(){
        return bookService.findByTitle("Today and yesterday");
    }

    @GetMapping(path = "/save")
    public void save(){
        Author author = new Author();
        author.setName("Mika");

        SearchApplication search = new SearchApplication();
        bookService.saveAuthor(author);

        Book book = new Book();
        book.setPublicationDate(new Date());
        final String title = "See you tomorrow, talk today";
        book.setTitle(title);
        final String subtitle = "Everything now";
        book.setSubtitle(subtitle);
        bookService.saveBook(book);
    }

    @GetMapping(path = "/reindex")
    public void reindex() throws InterruptedException {
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        fullTextEntityManager.createIndexer().startAndWait();
    }

    @GetMapping(path = "/search")
    @Transactional
    public List search(){
        FullTextEntityManager fullTextEntityManager =
                org.hibernate.search.jpa.Search.getFullTextEntityManager(entityManager);
        // create native Lucene query unsing the query DSL
        // alternatively you can write the Lucene query using the Lucene query parser
        // or the Lucene programmatic API. The Hibernate Search DSL is recommended though
        QueryBuilder qb = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder().forEntity(Book.class).get();
        org.apache.lucene.search.Query luceneQuery = qb
                .keyword()
                .onFields("title", "subtitle", "authors.name")
                .matching("Today")
                .createQuery();

        // wrap Lucene query in a javax.persistence.Query
        javax.persistence.Query jpaQuery =
                fullTextEntityManager.createFullTextQuery(luceneQuery, Book.class);

        // execute search
        List result = jpaQuery.getResultList();

        return result;
    }

}