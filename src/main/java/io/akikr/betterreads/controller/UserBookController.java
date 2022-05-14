package io.akikr.betterreads.controller;

import io.akikr.betterreads.models.UserBooks;
import io.akikr.betterreads.models.UserBooksPrimaryKey;
import io.akikr.betterreads.repository.UserBooksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;

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
	private final UserBooksRepository userBooksRepository;

	@Autowired
	public UserBookController(UserBooksRepository userBooksRepository)
	{
		this.userBooksRepository = userBooksRepository;
	}

	@PostMapping("/add-user-book")
	public ModelAndView addBookForUser(@RequestBody MultiValueMap<String, String> formData, @AuthenticationPrincipal OAuth2User principal)
	{
		// Check if user is logged-in
		if (isNull(principal) || isNull(principal.getAttribute("login")))
			return null;

		// Check if bookId present
		String bookId = requireNonNull(formData.getFirst("bookId"), "BookId cannot be null !!");

		UserBooksPrimaryKey key = new UserBooksPrimaryKey();
		key.setUserId(principal.getAttribute("login"));
		key.setBookId(bookId);

		UserBooks userBooks = new UserBooks();
		userBooks.setStartedDate(LocalDate.parse(formData.toSingleValueMap()
				.getOrDefault("startDate", LocalDate.now().toString())));
		userBooks.setCompletedDate(LocalDate.parse(formData.toSingleValueMap()
				.getOrDefault("completedDate", LocalDate.now().toString())));
		userBooks.setReadingStatus(formData.toSingleValueMap()
				.getOrDefault("readingStatus", "0"));
		userBooks.setRating(Integer.parseInt(formData.toSingleValueMap()
				.getOrDefault("rating", "0")));
		userBooks.setKey(key);

		userBooksRepository.save(userBooks);

		return new ModelAndView("redirect:/books/" + bookId);
	}
}
