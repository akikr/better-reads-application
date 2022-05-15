package io.akikr.betterreads.db.entity;

import lombok.Data;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDate;

/**
 * @apiNote An UserBooks entity to persist the book info for a user
 * @author ankit
 * @since 1.0
 */

@Data
@Table(value = "book_by_user_id_and_book_id")
public class UserBooks
{
	@PrimaryKey
	private UserBooksPrimaryKey key;

	@Column("started_date")
	@CassandraType(type = CassandraType.Name.DATE)
	private LocalDate startedDate;

	@Column("completed_date")
	@CassandraType(type = CassandraType.Name.DATE)
	private LocalDate completedDate;

	@Column("reading_status")
	@CassandraType(type = CassandraType.Name.TEXT)
	private String readingStatus;

	@Column("rating")
	@CassandraType(type = CassandraType.Name.INT)
	private int rating;

}
