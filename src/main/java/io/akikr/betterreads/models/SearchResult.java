package io.akikr.betterreads.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SearchResult
{
	@JsonProperty("numFound")
	private int numFound;
	@JsonProperty("docs")
	private List<SearchResultBook> searchResultBooks;
}
