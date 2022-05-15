package io.akikr.betterreads.controller;

import io.akikr.betterreads.models.BooksByUser;
import io.akikr.betterreads.repository.BooksByUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * @apiNote The HomeController to show home login page
 * @author ankit
 * @since 1.0
 */

@Controller
public class HomeController
{
	@Value("${app.cover-image-base-url:  https://covers.openlibrary.org/b/id/}")
	private String coverImageBaseUrl;

	private final BooksByUserRepository booksByUserRepository;

	@Autowired
	public HomeController(BooksByUserRepository booksByUserRepository)
	{
		this.booksByUserRepository = booksByUserRepository;
	}

	@GetMapping(value = "/")
	public String home(Model model, @AuthenticationPrincipal OAuth2User principal)
	{
		// If user is NOT logged-in
		if (isNull(principal) || isNull(principal.getAttribute("login")))
			return "index";

		// Get the userId of the logged-in user
		String userId = principal.getAttribute("login");

		// Cassandra zero based page request: page must be equal to 0
		Slice<BooksByUser> sliceBooksByUser = booksByUserRepository.findAllById(userId, CassandraPageRequest.of(0, 20));
		List<BooksByUser> bookByUser = sliceBooksByUser.getContent()
				.stream()
				.distinct()
				.peek(book -> {
					String coverImageUrl = "/images/no-image.png";
					if (nonNull(book.getCoverIds()) && book.getCoverIds().size() > 0) {
						coverImageUrl = coverImageBaseUrl + book.getCoverIds().get(0) + "-M.jpg";
					}
					book.setCoverUrl(coverImageUrl);
				}).collect(Collectors.toList());

		model.addAttribute("books", bookByUser);
		return "home";
	}
}
