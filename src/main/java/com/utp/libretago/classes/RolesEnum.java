package com.utp.libretago.classes;

/**
 * Contiene los diferentes roles disponibles en el sistema.
 * <p>
 * Esta clase funciona como un contenedor de constantes {@code String} que representan
 * los roles que pueden asignarse a los usuarios.
 * </p>
 * 
 * @author Roberto Anton
 * @version 1.0
 * @since 28-10-2025
  */

public class RolesEnum {
    /** Rol de administrador del sistema. */
    public static final String ADMIN = "ADMIN";
    /** Rol asignado a un colegio o institución educativa. */
    public static final String COLEGIO = "COLEGIO";
    /** Rol asignado a un profesor dentro del sistema. */
    public static final String PROFESOR = "PROFESOR";
    /** Rol asignado a un apoderado o tutor de un estudiante. */
    public static final String APODERADO = "APODERADO";
}
