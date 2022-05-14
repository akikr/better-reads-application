package io.akikr.betterreads.controller;

import io.akikr.betterreads.models.SearchResult;
import io.akikr.betterreads.models.SearchResultBook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @apiNote : The search controller to get search results for queried books
 * @author ankit
 * @since 1.0
 */

@Controller
public class SearchController
{
    @Value("${app.search-base-url: https://openlibrary.org/search.json}")
    private String searchBaseUrl;

    @Value("${app.cover-image-base-url:  https://covers.openlibrary.org/b/id/}")
    private String coverImageBaseUrl;

    private final WebClient webClient;

    public SearchController(WebClient webClient)
    {
        this.webClient = webClient;
    }

    @GetMapping(value = "/search")
    public String getSearchResult(@RequestParam String query, Model model)
    {
        final String searchUri = searchBaseUrl + "?q={query}";
        Mono<SearchResult> searchResultMono = webClient.get()
                .uri(searchUri, query)
                .retrieve()
                .bodyToMono(SearchResult.class);

        SearchResult searchResult = searchResultMono.block();

        List<SearchResultBook> books = new ArrayList<>();
        if (Objects.nonNull(searchResult))
        {
            books = searchResult.getSearchResultBooks()
                    .stream()
                    .limit(10)
                    .filter(Objects::nonNull)
                    .peek(this::getBookResult)
                    .collect(Collectors.toList());
        }
        model.addAttribute("searchResults", books);
        return "search";
    }

    private void getBookResult(SearchResultBook bookResult)
    {
        bookResult.setKey(bookResult.getKey().replace("/works/", ""));
        String coverId = bookResult.getCoverImage();

        if (StringUtils.hasText(coverId))
            coverId = coverImageBaseUrl + coverId + "-M.jpg";
        else
            coverId = "/images/no-image.png";

        bookResult.setCoverImage(coverId);
    }
}
