/**
 * 
 */
package sud_evp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sud_evp.database.model.UserTable;

/**
 * Repository to save a user to the database. / retrieve user from the database
 * 
 * @author busch
 *
 */
@Repository()
public interface UserRepository extends JpaRepository<UserTable, Integer>{
	Optional<UserTable> findByUsername(String username);
	boolean existsByUsername(String username);
	
}