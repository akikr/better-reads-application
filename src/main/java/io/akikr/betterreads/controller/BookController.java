package io.akikr.betterreads.controller;

import io.akikr.betterreads.models.Book;
import io.akikr.betterreads.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Objects;
import java.util.Optional;

/**
 * @apiNote : The BookController to get book data from cassandra database
 * @author ankit
 */

@Controller
public class BookController
{
    @Value("${app.cover-image-base-url:  https://covers.openlibrary.org/b/id/}")
    private String coverImageBaseUrl;

    private final BookRepository bookRepository;

    @Autowired
    public BookController(BookRepository bookRepository)
    {
        this.bookRepository = bookRepository;
    }

    @GetMapping(value = "/books/{bookId}")
    public String getBook(@PathVariable String bookId, Model model)
    {
        Optional<Book> optionalBook = bookRepository.findById(bookId);

        if (optionalBook.isPresent())
        {
            Book book = optionalBook.get();
            String coverImageUrl = "/images/no-image.png";

            if (Objects.nonNull(book.getCoverIds()) & book.getCoverIds().size() > 0)
                coverImageUrl = coverImageBaseUrl + book.getCoverIds().get(0) + "-L.jpg";

            model.addAttribute("coverImage", coverImageUrl);
            model.addAttribute("book", book);
            return "book";
        }
        return "book-not-found";
    }
}
