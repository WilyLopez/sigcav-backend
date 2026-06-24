-- Seed: usuarios iniciales del sistema
-- Contraseñas (BCrypt 10):
--   admin        → Admin@123
--   asistente    → Asistente@123

INSERT INTO usuario (nombre_completo, nombre_usuario, correo, contrasena_hash, rol, activo)
VALUES
    (
        'Administrador del Sistema',
        'admin',
        'admin@sigcav.com',
        '$2a$10$LOKfzSufgp7C4r2S0PAxNedv5Jx7H45eUQa4PHoQNLcyLin4nEUqy',
        'ADMINISTRADOR',
        TRUE
    ),
    (
        'Asistente Administrativo',
        'asistente',
        'asistente@sigcav.com',
        '$2a$10$w9eqSREYyGVlcU8VIWXZy.73rtHC2i6y2piWULcdL813KsoPF2/VO',
        'ASISTENTE_ADMINISTRATIVO',
        TRUE
    );
