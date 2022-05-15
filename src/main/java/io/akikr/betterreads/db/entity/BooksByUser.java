package io.akikr.betterreads.db.entity;

import lombok.Data;
import org.springframework.data.annotation.Transient;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @apiNote An entity that represents the books read by a user that helps showing user's recent books in the home page
 * @author ankit
 * @since 1.0
 */

@Data
@Table(value = "books_by_user")
public class BooksByUser
{
	@PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private String id;

	@PrimaryKeyColumn(name = "book_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
	@CassandraType(type = CassandraType.Name.TEXT)
	private String bookId;

	@PrimaryKeyColumn(name = "time_uuid", type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
	@CassandraType(type = CassandraType.Name.TIMEUUID)
	private UUID timeUuid;

	@PrimaryKeyColumn(name = "reading_status", type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING)
	@CassandraType(type = CassandraType.Name.TEXT)
	private String readingStatus;

	@Column("book_name")
	@CassandraType(type = CassandraType.Name.TEXT)
	private String bookName;

	@Column("author_names")
	@CassandraType(type = CassandraType.Name.LIST, typeArguments = CassandraType.Name.TEXT)
	private List<String> authorNames;

	@Column("cover_ids")
	@CassandraType(type = CassandraType.Name.LIST, typeArguments = CassandraType.Name.TEXT)
	private List<String> coverIds;

	@Column("rating")
	@CassandraType(type = CassandraType.Name.INT)
	private int rating;

	@Transient
	private String coverUrl;

	// Here override the equals & hashCode methods for id & bookId to get distinct BooksByUser object

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		BooksByUser that = (BooksByUser) o;
		return id.equals(that.id) && bookId.equals(that.bookId);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id, bookId);
	}
}
