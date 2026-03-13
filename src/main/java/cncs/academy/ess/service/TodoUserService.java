package cncs.academy.ess.service;

import cncs.academy.ess.cryptography.PBKDF2;
import cncs.academy.ess.model.User;
import cncs.academy.ess.repository.UserRepository;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static cncs.academy.ess.cryptography.PBKDF2.bytesToHex;
import static cncs.academy.ess.cryptography.PBKDF2.generateSalt;

public class TodoUserService {
    private final UserRepository repository;

    public TodoUserService(UserRepository userRepository) {
        this.repository = userRepository;
    }
    public User addUser(String username, String password) throws NoSuchAlgorithmException {
        User user = new User(username, password);
        int id = repository.save(user);
        user.setId(id);
        return user;
    }
    public User getUser(int id) {
        return repository.findById(id);
    }

    public void deleteUser(int id) {
        repository.deleteById(id);
    }

    public String login(String username, String password) throws NoSuchAlgorithmException {
        User user = repository.findByUsername(username);
        if (user == null) {
            return null;
        }

        /*calcular o HASH da PWD com salt do user que está em BD*/
        String hashedPasswordString;
        byte[] salt = user.getSalt(); // Obtem o salt do user
        int iterations = 10000;
        int keyLength = 256;
        // Hash the password using PBKDF2
        byte[] hashedPassword = null;
        try {
            hashedPassword = PBKDF2.hashPassword(password, salt, iterations, keyLength);
            // Convert the hashed password to a string for storage
            hashedPasswordString = bytesToHex(hashedPassword);
        } catch (Exception e) {
            throw new RuntimeException(e);
            /*TODO:ctx.status(201).json(response);*/
        }

        if (user.getPassword().equals(hashedPasswordString)) {
            return createAuthToken(user);
        }
        return null;
    }

    private String createAuthToken(User user) {

        //return "Bearer " + user.getUsername();
        return "Bearer " + user.getToken();
    }
}
