☕ Café Aurora
Aplicación web para la gestión de reservas y menú de una cafetería.
El sistema permite que los clientes realicen reservas online, mientras que el personal de recepción puede gestionar reservas directamente desde el sistema.

🧰 Tecnologías utilizadas
- Backend
	- Java 21
	- Spring Boot
	- Supabase Auth (JWT)
	- PostgreSQL

- Frontend
	- Angular
	- Servicios externos
	- Supabase (Autenticación)
	- Cloudinary (Almacenamiento de imágenes)

⚙️ Requisitos
Antes de ejecutar el proyecto necesitas tener instalado:
	- Java 21
	- Node.js 18+
	- PostgreSQL
	- Cuenta en Supabase
	- Cuenta en Cloudinary

🚀 Configuración del Backend
1. Configura las variables de entorno en application.properties:
- Base de datos
	- DB_URL
	- DB_USERNAME
	- DB_PASSWORD

- Supabase
	- SUPABASE_URL
	- SUPABASE_KEY
	- SUPABASE_JWT_SECRET
	- SUPABASE_ANONKEY

- Cloudinary
	- CLOUDINARY_CLOUD_NAME
	- CLOUDINARY_API_KEY
	- CLOUDINARY_API_SECRET

- Correo (envío de notificaciones)
	- CORREO_EMAIL
	- PASSWORD

2. Ejecuta el script SQL para crear la base de datos:
database/DB_Cafe_Aurora.sql

3. Ejecuta el backend:
./mvnw spring-boot:run

💻 Configuración del Frontend
1. Copia el archivo de entorno:
cp src/environments/environment.example.ts src/environments/environment.ts

2. Edita environment.ts y agrega tus valores reales.
Instala dependencias y ejecuta Angular:
	- npm install
	- ng serve

🌐 Deploy

Backend
Deploy en Render.

Frontend
Deploy en Vercel.

Variables necesarias en frontend:
	- API_URL
	- SUPABASE_URL
	- SUPABASE_ANON_KEY

🔗 Demo
Frontend
https://cafe-aurora-app.vercel.app/cliente/index

Backend
https://cafe-aurora.onrender.com

🔐 Configuración inicial en Supabase
1. Desactivar confirmación de correo
Supabase Dashboard → Authentication → Providers → Email
Desactivar:
Confirm email

2. Configurar Google OAuth
Authentication → Providers → Google
Agregar:
	- Client ID
	- Client Secret
En Google Cloud Console agregar como redirect URL:

https://tu-proyecto.supabase.co/auth/v1/callback

3. Crear usuario administrador
En Supabase:
Authentication → Users → Add user
Crear el usuario y copiar el UID generado.
Luego ejecutar en la base de datos:

INSERT INTO tb_users (id_user, name, email, phone, password, is_active, id_role)
VALUES (
  'UID-copiado-de-supabase',
  'Admin',
  'admin@cafeaurora.com',
  '999999999',
  'password-encriptado',
  true,
  (SELECT id_role FROM tb_roles WHERE name_role = 'A')
);

4. Ejecutar el script de base de datos
database/DB_Cafe_Aurora.sql

Esto creará las tablas y los datos iniciales del sistema.