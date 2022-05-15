package io.akikr.betterreads.db.entity;

import lombok.Data;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

/**
 * @apiNote An UserBooksPrimaryKey class to persist the primary keys for UserBooks entity
 * @author ankit
 * @since 1.0
 */

@Data
@PrimaryKeyClass
public class UserBooksPrimaryKey
{
	@PrimaryKeyColumn(name = "book_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private String bookId;

	@PrimaryKeyColumn(name = "user_id", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
	private String userId;
}
