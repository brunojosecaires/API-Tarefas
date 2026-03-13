import cncs.academy.ess.model.User;
import cncs.academy.ess.repository.memory.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Null;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserRepositoryTest {
    private InMemoryUserRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepository();
    }

    @Test
    void saveAndFindById_ShouldReturnSavedUser() {
        // Arrange
        User user = new User("jane", "password");

        // Act
        int id = repository.save(user);
        User savedUser = repository.findById(id);

        // Assert
        assertEquals(user.getUsername(), savedUser.getUsername());
        assertEquals(user.getPassword(), savedUser.getPassword());
    }

   @Test
    void FindByUsername_ShouldReturnSavedUser() {
        // Arrange
        User user = new User("jane", "password");
        int id = repository.save(user);

        // Act
        String usernameToFind = "jane";
        User userFind = repository.findByUsername(usernameToFind);

        // Assert
        assertEquals(usernameToFind,userFind.getUsername());
    }

    @Test
    void deleteById_ShouldReturn() {
        // Arrange
        User user = new User("jane", "password");

        // Act
        int id = repository.save(user);
        repository.deleteById(id);
        User savedUser = repository.findById(id);

        // Assert
        assertNull(savedUser);


    }
}