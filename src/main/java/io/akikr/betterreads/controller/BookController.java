package io.akikr.betterreads.controller;

import io.akikr.betterreads.db.entity.Book;
import io.akikr.betterreads.db.entity.UserBooks;
import io.akikr.betterreads.db.entity.UserBooksPrimaryKey;
import io.akikr.betterreads.db.repository.BookRepository;
import io.akikr.betterreads.db.repository.UserBooksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

import static java.util.Objects.nonNull;

/**
 * @apiNote The BookController to get book data from cassandra database
 * @author ankit
 * @since 1.0
 */

@Controller
public class BookController
{
    @Value("${app.cover-image-base-url:  https://covers.openlibrary.org/b/id/}")
    private String coverImageBaseUrl;

    private final BookRepository bookRepository;
    private final UserBooksRepository userBooksRepository;

    @Autowired
    public BookController(BookRepository bookRepository, UserBooksRepository userBooksRepository)
    {
        this.bookRepository = bookRepository;
        this.userBooksRepository = userBooksRepository;
    }

    @GetMapping(value = "/books/{bookId}")
    public String getBook(@PathVariable String bookId, Model model, @AuthenticationPrincipal OAuth2User principal)
    {
        Optional<Book> optionalBook = bookRepository.findById(bookId);

        if (optionalBook.isPresent())
        {
            Book book = optionalBook.get();
            String coverImageUrl = "/images/no-image.png";

            if (nonNull(book.getCoverIds()) && book.getCoverIds().size() > 0)
                coverImageUrl = coverImageBaseUrl + book.getCoverIds().get(0) + "-L.jpg";

            model.addAttribute("coverImage", coverImageUrl);
            model.addAttribute("book", book);

            // Only if user is logged-in
            if (nonNull(principal) && nonNull(principal.getAttribute("login")))
            {
                String userId = principal.getAttribute("login");
                model.addAttribute("loginId", userId);

                UserBooksPrimaryKey key = new UserBooksPrimaryKey();
                key.setBookId(bookId);
                key.setUserId(userId);
                Optional<UserBooks> userBooks = userBooksRepository.findById(key);

                if (userBooks.isPresent())
                    model.addAttribute("userBooks", userBooks.get());
                else
                    model.addAttribute("userBooks", new UserBooks());
            }

            return "book";
        }
        return "book-not-found";
    }
}
