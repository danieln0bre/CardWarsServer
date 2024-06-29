package br.ufrn.imd.service;

import br.ufrn.imd.model.Manager;
import br.ufrn.imd.model.Player;
import br.ufrn.imd.model.User;
import br.ufrn.imd.repository.ManagerRepository;
import br.ufrn.imd.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.Optional;

@Service
public class UserService {

    private final PlayerRepository playerRepository;
    private final ManagerRepository managerRepository;

    @Autowired
    public UserService(PlayerRepository playerRepository, ManagerRepository managerRepository) {
        this.playerRepository = playerRepository;
        this.managerRepository = managerRepository;
    }

    /*public Optional<User> getUserByUsername(String username) {
        Optional<Player> player = playerRepository.findByUsername(username);
        if (player.isPresent()) {
            return Optional.of(player.get());
        }

        Optional<Manager> manager = managerRepository.findByUsername(username);
        if (manager.isPresent()) {
            return Optional.of(manager.get());
        }

        return Optional.empty();
    } */

    public Optional<User> getUserByEmail(String email) {
        Optional<Player> player = playerRepository.findByEmail(email);
        if (player.isPresent()) {
            return Optional.of(player.get());
        }

        Optional<Manager> manager = managerRepository.findByEmail(email);
        if (manager.isPresent()) {
            return Optional.of(manager.get());
        }

        System.out.println("USer Service : User not found with email: " + email);
        return Optional.empty();
    }

    public Optional<String> getUserIdByEmail(String email) {
        Optional<Player> player = playerRepository.findByEmail(email);
        if (player.isPresent()) {
            return Optional.of(player.get().getId());
        }

        Optional<Manager> manager = managerRepository.findByEmail(email);
        if (manager.isPresent()) {
            return Optional.of(manager.get().getId());
        }

        return Optional.empty();
    }

    public boolean checkPassword(User user, String rawPassword) {
        return rawPassword.equals(user.getPassword());
    }

    public Player savePlayer(Player player) {
        if (usernameExists(player.getUsername()) || emailExists(player.getEmail())) {
            throw new IllegalArgumentException("Username or email already exists.");
        }
        if (!StringUtils.hasText(player.getName())) {
            throw new IllegalArgumentException("Player name cannot be empty.");
        }
        if (player.getEmail() != null && !isValidEmail(player.getEmail())) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        if (!StringUtils.hasText(player.getPassword())) {
            throw new IllegalArgumentException("Player password cannot be empty.");
        }
        return playerRepository.save(player);
    }

    public Manager saveManager(Manager manager) {
        if (usernameExists(manager.getUsername()) || emailExists(manager.getEmail())) {
            throw new IllegalArgumentException("Username or email already exists.");
        }
        if (!StringUtils.hasText(manager.getName())) {
            throw new IllegalArgumentException("Manager name cannot be empty.");
        }
        if (manager.getEmail() != null && !isValidEmail(manager.getEmail())) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        if (!StringUtils.hasText(manager.getPassword())) {
            throw new IllegalArgumentException("Manager password cannot be empty.");
        }
        return managerRepository.save(manager);
    }

    public Optional<User> getUserById(String id, String userType) {
        if ("player".equalsIgnoreCase(userType)) {
            return playerRepository.findById(id).map(player -> (User) player);
        } else if ("manager".equalsIgnoreCase(userType)) {
            return managerRepository.findById(id).map(manager -> (User) manager);
        }
        return Optional.empty();
    }

    private boolean usernameExists(String username) {
        return playerRepository.findByUsername(username).isPresent() || managerRepository.findByUsername(username).isPresent();
    }

    private boolean emailExists(String email) {
        return playerRepository.findByEmail(email).isPresent() || managerRepository.findByEmail(email).isPresent();
    }

    private boolean isValidEmail(String email) {
        return email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}");
    }
}
