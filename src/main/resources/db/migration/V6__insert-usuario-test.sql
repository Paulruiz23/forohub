-- ============================================
-- INSERTAR USUARIO DE PRUEBA
-- Password: "123456" (encriptado con BCrypt)
-- ============================================

-- Usuario de prueba
INSERT INTO usuarios (nombre, email, contrasena, activo) VALUES
    ('Admin Test',
     'admin@forohub.com',
     '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8ssKQqpXRXUhieo2r6',
     TRUE);

-- Asignar rol ADMIN al usuario
INSERT INTO usuarios_perfiles (usuario_id, perfil_id)
SELECT u.id, p.id
FROM usuarios u, perfiles p
WHERE u.email = 'admin@forohub.com'
  AND p.nombre = 'ROLE_ADMIN';


-- **Credenciales de Prueba - RESUMEN**
--
-- ### **Usuario Administrador**
-- Email:    admin@forohub.com
-- Password: 123456
-- Rol:      ROLE_ADMIN
--
-- ### **Usuario Normal** (opcional)
--
-- Email:    user@forohub.com
-- Password: 123456
-- Rol:      ROLE_USER
