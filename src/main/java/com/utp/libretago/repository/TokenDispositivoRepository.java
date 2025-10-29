package com.utp.libretago.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.utp.libretago.entity.TokenDispositivo;

/**
 * Repositorio para la gestión de la entidad {@link TokenDispositivo}.
 *
 * Extiende {@link JpaRepository} para ofrecer operaciones CRUD básicas sobre los tokens de dispositivos móviles o web
 * registrados por los usuarios del sistema.
 *
 *
 * Incluye consultas personalizadas para:
 * <ul>
 * <li>Listar tokens de varios usuarios a partir de sus identificadores.</li>
 * <li>Buscar un token específico para validación o autenticación.</li>
 * <li>Eliminar un token cuando deja de ser válido o se cierra sesión.</li>
 * </ul>
 *
 * @see TokenDispositivo
 * @see org.springframework.data.jpa.repository.JpaRepository
 *
 * @author Jhon
 * @version 1.0
 * @since 2025-10
 */
public interface TokenDispositivoRepository extends JpaRepository<TokenDispositivo, Long> {

    /**
     * Obtiene todos los tokens de dispositivo asociados a una lista de usuarios.
     *
     * @param usuariosId lista de identificadores únicos de usuarios.
     * @return lista de entidades {@link TokenDispositivo} correspondientes a los usuarios indicados.
     */
    @Query("SELECT td FROM TokenDispositivo td WHERE td.usuarioPropietario.id IN (?1)")
    List<TokenDispositivo> listarTokensPorUsuariosId(List<Long> usuariosId);

    /**
     * Busca un token específico por su valor literal.
     *
     * @param token cadena del token de dispositivo.
     * @return la entidad {@link TokenDispositivo} asociada, o {@code null} si no existe.
     */
    TokenDispositivo findByToken(String token);

    /**
     * Elimina un token específico de la base de datos.
     * <p>
     * Este método es útil para invalidar tokens antiguos o no autorizados.
     * </p>
     *
     * @param token cadena del token de dispositivo a eliminar.
     */
    void deleteByToken(String token);
}
