package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.RoleRepository;
import ru.kata.spring.boot_security.demo.dao.UserRepository;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Set<String> getAllRolesNames() {
        return roleRepository
                .findAll()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    @Transactional
    @Override
    public void initializeDB() {
        Role adminRole = new Role("ADMIN");
        Role userRole = new Role("USER");

//        User first = new User("", "", (byte) 1, "1", "1", Set.of(adminRole));
        User user = new User("vasya", "vasin", (byte) 17, "vasya@gmail.com", "1234", Set.of(adminRole));
        User admin = new User("petr", "petrov", (byte) 18, "petr@gmail.com", "1234", Set.of(userRole));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
//        first.setPassword(passwordEncoder.encode(first.getPassword()));

        userRepository.save(user);
        userRepository.save(admin);
//        userRepository.save(first);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsers(int count) {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void saveUser(User user, Set<String> roles) {
        if (roles == null) {
            user.setRoles(roleRepository.findAllByName("USER"));
        } else {
            user.setRoles(roleRepository.findAllByNameIn(roles));
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id).get();
    }

}
