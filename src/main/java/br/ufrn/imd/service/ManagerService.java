package br.ufrn.imd.service;

import br.ufrn.imd.model.Manager;
import br.ufrn.imd.model.Event;
import br.ufrn.imd.repository.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class ManagerService {

    private final ManagerRepository managerRepository;

    @Autowired
    public ManagerService(ManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }

    public Manager saveManager(Manager manager) {
        validateManager(manager);
        return managerRepository.save(manager);
    }

    public Optional<Manager> getManagerById(String id) {
        validateId(id);
        return managerRepository.findById(id);
    }

    public void deleteManager(String id) {
        validateId(id);
        managerRepository.deleteById(id);
    }

    public List<Manager> getAllManagers() {
        return managerRepository.findAll();
    }

    public List<Event> getManagerEvents(String managerId) {
        validateId(managerId);
        Manager manager = managerRepository.findById(managerId)
            .orElseThrow(() -> new IllegalArgumentException("Manager not found with ID: " + managerId));
        return manager.getEvents();
    }

    private void validateManager(Manager manager) {
        if (manager == null) {
            throw new IllegalArgumentException("Manager cannot be null.");
        }
        if (!StringUtils.hasText(manager.getName())) {
            throw new IllegalArgumentException("Manager name cannot be empty.");
        }
        if (manager.getEmail() != null && !isValidEmail(manager.getEmail())) {
            throw new IllegalArgumentException("Invalid email format.");
        }
    }

    private void validateId(String id) {
        if (!StringUtils.hasText(id)) {
            throw new IllegalArgumentException("Manager ID cannot be null or empty.");
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}");
    }
}
