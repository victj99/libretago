CREATE TABLE rol (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

-- Insertar los roles iniciales
INSERT INTO rol (nombre) VALUES ('ADMIN'), ('COLEGIO'), ('PROFESOR'), ('APODERADO');

CREATE TABLE usuario (
    id BIGSERIAL PRIMARY KEY,
    nombre_usuario VARCHAR(50) NOT NULL UNIQUE,
    nombre_completo VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    telefono VARCHAR(20),
    contrasenia VARCHAR(255) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Insertar el usuario administrador
INSERT INTO public.usuario (nombre_usuario, nombre_completo, email, contrasenia) 
VALUES ('admin', 'Administrador Sistema', 'admin@gmail.com', '$2a$12$GwusTbDXIe.aRB2ICOQ54ueXZP96hWnhZD6.KgmS3zRIPOyBiAOMi');

CREATE TABLE usuario_rol (
    usuario_id BIGINT NOT NULL,
    rol_id BIGINT NOT NULL,
    CONSTRAINT fk_usuario_rol_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id) ON DELETE CASCADE,
    CONSTRAINT fk_usuario_rol_rol FOREIGN KEY (rol_id) REFERENCES rol (id) ON DELETE CASCADE,
    PRIMARY KEY (usuario_id, rol_id)
);

-- Asignar el rol admin al usuario administrador
INSERT INTO usuario_rol (usuario_id, rol_id) VALUES ((SELECT id FROM usuario WHERE nombre_usuario = 'admin'), 1);

CREATE TABLE institucion_educativa (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    direccion VARCHAR(255),
    telefono VARCHAR(20),
    codigo_ugel VARCHAR(50) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE usuario_institucion (
    usuario_colegio_id BIGINT NOT NULL,
    institucion_educativa_id BIGINT NOT NULL,
    CONSTRAINT fk_usuario_institucion_usuario FOREIGN KEY (usuario_colegio_id) REFERENCES usuario (id) ON DELETE RESTRICT,
    CONSTRAINT fk_usuario_institucion_institucion FOREIGN KEY (institucion_educativa_id) REFERENCES institucion_educativa (id) ON DELETE CASCADE,
    PRIMARY KEY (usuario_colegio_id, institucion_educativa_id)
);

CREATE TABLE grupo (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    institucion_educativa_id BIGINT NOT NULL,
    usuario_profesor_id BIGINT, -- El usuario con rol PROFESOR asignado
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_grupo_institucion_educativa FOREIGN KEY (institucion_educativa_id) REFERENCES institucion_educativa (id) ON DELETE CASCADE,
    CONSTRAINT fk_grupo_usuario_profesor FOREIGN KEY (usuario_profesor_id) REFERENCES usuario (id) ON DELETE RESTRICT
);

CREATE TABLE alumno (
    id BIGSERIAL PRIMARY KEY,
    nombres VARCHAR(255) NOT NULL,
    apellidos VARCHAR(255) NOT NULL,
    codigo_alumno VARCHAR(50) NOT NULL,
    institucion_educativa_id BIGINT NOT NULL,
    usuario_apoderado_id BIGINT,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (codigo_alumno, institucion_educativa_id),
    CONSTRAINT fk_alumno_institucion_educativa FOREIGN KEY (institucion_educativa_id) REFERENCES institucion_educativa (id),
    CONSTRAINT fk_alumno_usuario_apoderado FOREIGN KEY (usuario_apoderado_id) REFERENCES usuario (id) ON DELETE SET NULL
);

CREATE TABLE alumno_grupo (
    alumno_id BIGINT NOT NULL,
    grupo_id BIGINT NOT NULL,
    CONSTRAINT fk_alumno_grupo_alumno_id FOREIGN KEY (alumno_id) REFERENCES alumno (id) ON DELETE CASCADE,
    CONSTRAINT fk_alumno_grupo_grupo FOREIGN KEY (grupo_id) REFERENCES grupo (id) ON DELETE CASCADE,
    PRIMARY KEY (alumno_id, grupo_id)
);

CREATE TABLE notificacion (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    detalle TEXT NOT NULL,
    usuario_creador_id BIGINT NOT NULL,
    estado CHAR(1) NOT NULL DEFAULT 'P', -- ESTADO -> P: PENDIENTE, A: APROBADO, R: RECHAZADO
    usuario_evaluador_id BIGINT,
    fecha_evaluacion TIMESTAMP,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notificacion_usuario_creador FOREIGN KEY (usuario_creador_id) REFERENCES usuario (id) ON DELETE RESTRICT,
    CONSTRAINT fk_notificacion_usuario_eval FOREIGN KEY (usuario_evaluador_id) REFERENCES usuario (id) ON DELETE SET NULL
);

CREATE TABLE notificacion_grupo (
    notificacion_id BIGINT NOT NULL,
    grupo_id BIGINT NOT NULL,
    CONSTRAINT fk_notificacion_grupo_notificacion FOREIGN KEY (notificacion_id) REFERENCES notificacion (id) ON DELETE CASCADE,
    CONSTRAINT fk_notificacion_grupo_grupo FOREIGN KEY (grupo_id) REFERENCES grupo (id) ON DELETE CASCADE,
    PRIMARY KEY (notificacion_id, grupo_id)
);

CREATE TABLE evento (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    detalle TEXT NOT NULL,
    fecha_evento DATE NOT NULL,
    usuario_creador_id BIGINT NOT NULL,
    estado CHAR(1) NOT NULL DEFAULT 'P', -- ESTADO -> P: PENDIENTE, A: APROBADO, R: RECHAZADO
    usuario_evaluador_id BIGINT,
    fecha_evaluacion TIMESTAMP,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_evento_usuario_creador FOREIGN KEY (usuario_creador_id) REFERENCES usuario (id) ON DELETE RESTRICT,
    CONSTRAINT fk_evento_usuario_aprobador FOREIGN KEY (usuario_evaluador_id) REFERENCES usuario (id) ON DELETE SET NULL
);

CREATE TABLE evento_grupo (
    evento_id BIGINT NOT NULL,
    grupo_id BIGINT NOT NULL,
    CONSTRAINT fk_evento_grupo_evento FOREIGN KEY (evento_id) REFERENCES evento (id) ON DELETE CASCADE,
    CONSTRAINT fk_evento_grupo_grupo FOREIGN KEY (grupo_id) REFERENCES grupo (id) ON DELETE CASCADE,
    PRIMARY KEY (evento_id, grupo_id)
);

CREATE TABLE token_dispositivo (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
    usuario_propietario_id BIGINT NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_token_dispositivo_usuario FOREIGN KEY (usuario_propietario_id) REFERENCES usuario (id) ON DELETE CASCADE
);

-- Indices poderosos pa que las consultas no sean lentas :)
CREATE INDEX IF NOT EXISTS idx_token_dispositivo_usuario_propietario_id ON token_dispositivo (usuario_propietario_id);

CREATE INDEX IF NOT EXISTS idx_grupo_institucion_educativa_id ON grupo (institucion_educativa_id);
