package io.akikr.betterreads.repository;

import io.akikr.betterreads.models.UserBooks;
import io.akikr.betterreads.models.UserBooksPrimaryKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

/**
 * @apiNote : The book repository to do CURD operation on book table in cassandra database
 * @author ankit
 * @since 1.0
 */

@Repository
public interface UserBooksRepository extends CassandraRepository<UserBooks, UserBooksPrimaryKey>
{

}
