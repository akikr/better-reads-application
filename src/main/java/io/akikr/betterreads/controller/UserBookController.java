package io.akikr.betterreads.controller;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import io.akikr.betterreads.db.entity.Book;
import io.akikr.betterreads.db.entity.BooksByUser;
import io.akikr.betterreads.db.entity.UserBooks;
import io.akikr.betterreads.db.entity.UserBooksPrimaryKey;
import io.akikr.betterreads.db.repository.BookRepository;
import io.akikr.betterreads.db.repository.BooksByUserRepository;
import io.akikr.betterreads.db.repository.UserBooksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

/**
 * @apiNote : The UserBookController to get book data for a user from cassandra database
 * @author ankit
 * @since 1.0
 */

@Controller
public class UserBookController
{
	private final BookRepository bookRepository;
	private final UserBooksRepository userBooksRepository;
	private final BooksByUserRepository booksByUserRepository;

	@Autowired
	public UserBookController(
			BookRepository bookRepository,
			UserBooksRepository userBooksRepository,
			BooksByUserRepository booksByUserRepository)
	{
		this.bookRepository = bookRepository;
		this.userBooksRepository = userBooksRepository;
		this.booksByUserRepository = booksByUserRepository;
	}

	@PostMapping("/add-user-book")
	public ModelAndView addBookForUser(@RequestBody MultiValueMap<String, String> formData, @AuthenticationPrincipal OAuth2User principal)
	{
		// Check if user is logged-in
		if (isNull(principal) || isNull(principal.getAttribute("login")))
			return null;

		// Get User id if user is logged-in
		String userId = principal.getAttribute("login");

		// Check if bookId present
		String bookId = requireNonNull(formData.getFirst("bookId"), "BookId cannot be null !!");

		// Check if book present otherwise redirect
		Optional<Book> optionalBook = bookRepository.findById(bookId);
		if (optionalBook.isEmpty()) {
			return new ModelAndView("redirect:/");
		}

		UserBooksPrimaryKey key = new UserBooksPrimaryKey();
		key.setUserId(userId);
		key.setBookId(bookId);

		// Prepare the user's info for every book
		UserBooks userBooks = new UserBooks();
		String startDate = formData.getFirst("startDate");
		userBooks.setStartedDate(LocalDate.parse(StringUtils.hasText(startDate) ? startDate : LocalDate.now().toString()));
		String completedDate = formData.getFirst("completedDate");
		userBooks.setCompletedDate(LocalDate.parse(StringUtils.hasText(completedDate) ? completedDate : LocalDate.now().toString()));
		String readingStatus = formData.getFirst("readingStatus");
		userBooks.setReadingStatus(readingStatus);
		String rating = formData.getFirst("rating");
		userBooks.setRating(Integer.parseInt(StringUtils.hasText(rating) ? rating : "0"));
		userBooks.setKey(key);
		// Save the user's info for every book
		userBooksRepository.save(userBooks);

		// Get the books
		Book book = optionalBook.get();

		// Prepare the book's info for every user
		BooksByUser booksByUser = new BooksByUser();
		booksByUser.setId(userId);
		booksByUser.setBookId(bookId);
		booksByUser.setTimeUuid(Uuids.timeBased());
		booksByUser.setBookName(book.getName());
		booksByUser.setCoverIds(book.getCoverIds());
		booksByUser.setAuthorNames(book.getAuthorNames());
		booksByUser.setReadingStatus(readingStatus);
		booksByUser.setRating(Integer.parseInt(StringUtils.hasText(rating) ? rating : "0"));
		// Save the book's info for every user
		booksByUserRepository.save(booksByUser);

		return new ModelAndView("redirect:/books/" + bookId);
	}
}
