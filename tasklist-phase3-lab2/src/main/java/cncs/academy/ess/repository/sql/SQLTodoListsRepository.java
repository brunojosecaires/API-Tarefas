package cncs.academy.ess.repository.sql;

import cncs.academy.ess.model.Todo;
import cncs.academy.ess.model.TodoList;
import cncs.academy.ess.model.User;
import cncs.academy.ess.repository.TodoListsRepository;

import java.sql.*;
import java.util.List;

import org.apache.commons.dbcp2.BasicDataSource;

import java.util.ArrayList;

public class SQLTodoListsRepository implements TodoListsRepository {
    private final BasicDataSource dataSource;

    public SQLTodoListsRepository(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public TodoList findById(int TodoListId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT * FROM lists WHERE id = ?")) {
            stmt.setInt(1, TodoListId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTodoList(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find lists by ID", e);
        }
        return null;
    }

    @Override
    public List<TodoList> findAll() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT * FROM lists");
             ResultSet rs = stmt.executeQuery()) {
            List<TodoList> lists = new ArrayList<>();
            while (rs.next()) {
                lists.add(mapResultSetToTodoList(rs));
            }
            return lists;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all lists", e);
        }
    }

    @Override
    public int save(TodoList TodoList) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "INSERT INTO public.lists(name, owner_id) VALUES (?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, TodoList.getName());
            stmt.setInt(2, TodoList.getOwnerId());
            stmt.executeUpdate();
            int generatedId = 0;
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                }
            }
            return generatedId;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save TodoList", e);
        }
    }

    @Override
    public void update(TodoList TodoList) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "UPDATE lists SET name=?, owner_id=? WHERE id=?",
                     Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, TodoList.getName());
            stmt.setInt(2, TodoList.getOwnerId());
            stmt.setInt(3, TodoList.getListId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update TodoList", e);
        }
    }

    @Override
    public boolean deleteById(int TodoListId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement("DELETE FROM lists WHERE id = ?")) {
            stmt.setInt(1, TodoListId);
            int rowsaffect = stmt.executeUpdate();
            return rowsaffect>0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete TodoList", e);
        }
    }

    @Override
    public List<TodoList> findAllByUserId(int userId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT * FROM lists WHERE owner_id = ?")) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<TodoList> lists = new ArrayList<>();
                while (rs.next()) {
                    lists.add(mapResultSetToTodoList(rs));
                }
                return lists;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find Lists by userId", e);
        }
    }

    private TodoList mapResultSetToTodoList(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        int owner_id = rs.getInt("owner_id");
        return new TodoList(id, name,owner_id);
    }
}