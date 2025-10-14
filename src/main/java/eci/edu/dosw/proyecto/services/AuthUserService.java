package eci.edu.dosw.proyecto.services;

import eci.edu.dosw.proyecto.models.AuthUser;

/**
 * Interfaz que maneja los métodos que va a imlementar los usuarios autenticados.
 */
public interface AuthUserService {
    AuthUser getByEmail(String email);
    AuthUser saveUser(AuthUser user);
}