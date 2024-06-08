package br.ufrn.imd.service;

import br.ufrn.imd.model.Manager;
import br.ufrn.imd.model.Player;
import br.ufrn.imd.model.User;
import br.ufrn.imd.repository.ManagerRepository;
import br.ufrn.imd.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Optional<User> getUserByUsername(String username) {
        Optional<Player> player = playerRepository.findByUsername(username);
        if (player.isPresent()) {
            System.out.println("Found player: " + player.get().getUsername());
            return Optional.of(player.get());
        }

        Optional<Manager> manager = managerRepository.findByUsername(username);
        if (manager.isPresent()) {
            System.out.println("Found manager: " + manager.get().getUsername());
            return Optional.of(manager.get());
        }

        System.out.println("User not found with username: " + username);
        return Optional.empty();
    }
    
    public Optional<String> getUserIdByUsername(String username) {
        Optional<Player> player = playerRepository.findByUsername(username);
        if (player.isPresent()) {
            return Optional.of(player.get().getId());
        }

        Optional<Manager> manager = managerRepository.findByUsername(username);
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
        return playerRepository.save(player);
    }

    public Manager saveManager(Manager manager) {
        if (usernameExists(manager.getUsername()) || emailExists(manager.getEmail())) {
            throw new IllegalArgumentException("Username or email already exists.");
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
}
