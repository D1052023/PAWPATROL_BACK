package eci.edu.dosw.proyecto.services.impl;

import org.springframework.stereotype.Service;
import eci.edu.dosw.proyecto.models.AuthUser;
import eci.edu.dosw.proyecto.repositories.AuthUserRepository;
import eci.edu.dosw.proyecto.services.AuthUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthUserServiceImpl implements AuthUserService {

    private final AuthUserRepository repository;

    @Override
    public AuthUser getByEmail(String email) {
        return repository.findByEmail(email).orElse(null);
    }

    @Override
    public AuthUser saveUser(AuthUser user) {
        return repository.save(user);
    }
}