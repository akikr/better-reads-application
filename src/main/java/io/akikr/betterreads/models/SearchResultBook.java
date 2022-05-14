package io.akikr.betterreads.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SearchResultBook
{
	@JsonProperty("key")
	private String key;
	@JsonProperty("title")
	private String title;
	@JsonProperty("author_name")
	private List<String> authorName;
	@JsonProperty("cover_i")
	private String coverImage;
	@JsonProperty("first_publish_year")
	private int firstPublishYear;
}
