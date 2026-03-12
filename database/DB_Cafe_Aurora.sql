-- TABLA DE ROLES
CREATE TABLE TB_ROLES (
  ID_ROLE SERIAL PRIMARY KEY,
  NAME_ROLE CHAR(1) NOT NULL UNIQUE
);

-- Insertar roles por defecto
INSERT INTO TB_ROLES (NAME_ROLE) VALUES
('A'),
('R'),
('C');

-- TABLA DE USUARIOS (extendida de Supabase Auth)
CREATE TABLE TB_USERS (
  ID_USER UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
  ID_ROLE INT NOT NULL REFERENCES TB_ROLES(ID_ROLE),
  EMAIL VARCHAR(100) NOT NULL UNIQUE,
  PASSWORD VARCHAR(255) NOT NULL,
  NAME VARCHAR(100) NOT NULL,
  PHONE VARCHAR(15) NOT NULL,
  IS_ACTIVE BOOLEAN DEFAULT TRUE,
  CREATED_AT TIMESTAMP DEFAULT NOW()
);

-- TABLA DE CATEGORÍAS DEL MENÚ
CREATE TABLE TB_CATEGORIES (
  ID_CAT SERIAL PRIMARY KEY,
  NAME_CAT VARCHAR(100) NOT NULL UNIQUE,
  DESCRIPTION TEXT,
  IS_ACTIVE BOOLEAN DEFAULT TRUE,
  CREATED_AT TIMESTAMP DEFAULT NOW()
);

-- Categorías ejemplo
INSERT INTO TB_CATEGORIES (NAME_CAT, DESCRIPTION) VALUES
('Cafés', 'Variedades de café caliente y frío'),
('Postres', 'Pasteles, tartas y dulces'),
('Desayunos', 'Opciones para empezar el día'),
('Bebidas', 'Jugos, smoothies y más');

-- TABLA DE ITEMS DEL MENÚ
CREATE TABLE TB_ITEMS (
  ID_ITEM SERIAL PRIMARY KEY,
  ID_CAT INT NOT NULL REFERENCES TB_CATEGORIES(ID_CAT) ON DELETE CASCADE,
  NAME VARCHAR(150) NOT NULL,
  DESCRIPTION TEXT,
  PRICE DECIMAL(10, 2) NOT NULL CHECK (PRICE >= 0),
  IMAGE_URL TEXT,
  IS_AVAILABLE BOOLEAN DEFAULT TRUE,
  IS_FEATURED BOOLEAN DEFAULT FALSE, -- Para destacados
  CREATED_AT TIMESTAMP DEFAULT NOW()
);

-- Items ejemplos
-- CAFÉ
INSERT INTO TB_ITEMS (ID_CAT, NAME, DESCRIPTION, PRICE, IMAGE_URL, IS_AVAILABLE, IS_FEATURED)
VALUES
(1, 'Capuccino Clásico', 'Café expreso con leche vaporizada y espuma suave.', 9.50, 'https://res.cloudinary.com/dgh58k3hv/image/upload/v1773278968/capuchino_tktxga.jpg', TRUE, TRUE),
(1, 'Latte Vainilla', 'Espresso suave con leche y un toque de vainilla.', 10.00, 'https://res.cloudinary.com/dgh58k3hv/image/upload/v1773278968/latte_vainilla_siye9z.jpg', TRUE, FALSE),
(1, 'Americano', 'Café negro estilo americano, fuerte y aromático.', 7.00, 'https://res.cloudinary.com/dgh58k3hv/image/upload/v1773278968/americano_zexx2a.jpg', TRUE, FALSE);

-- POSTRES
INSERT INTO TB_ITEMS (ID_CAT, NAME, DESCRIPTION, PRICE, IMAGE_URL, IS_AVAILABLE, IS_FEATURED)
VALUES
(2, 'Cheesecake de Fresa', 'Pastel cremoso con salsa natural de fresa.', 14.00, 'https://res.cloudinary.com/dgh58k3hv/image/upload/v1773279075/CHEESECAKE_fxdpqz.jpg', TRUE, TRUE),
(2, 'Brownie con Helado', 'Brownie tibio con bola de helado de vainilla.', 12.50, 'https://res.cloudinary.com/dgh58k3hv/image/upload/v1773279075/BROWNIE_HELADO_acxfi5.jpg', TRUE, FALSE),
(2, 'Tiramisú', 'Clásico postre italiano con café y cacao.', 13.00, 'https://res.cloudinary.com/dgh58k3hv/image/upload/v1773279077/TIRAMISU_pdimtb.avif', TRUE, FALSE);

-- DESAYUNOS
INSERT INTO TB_ITEMS (ID_CAT, NAME, DESCRIPTION, PRICE, IMAGE_URL, IS_AVAILABLE, IS_FEATURED)
VALUES
(3, 'Sandwich de Pollo', 'Pan artesanal con pollo, lechuga y mayonesa casera.', 16.00, 'https://res.cloudinary.com/dgh58k3hv/image/upload/v1773279195/SANDWICH_POLLO_ga9m6g.jpg', TRUE, FALSE),
(3, 'Tostadas Francesas', 'Tostadas dulces con miel y frutos rojos.', 15.50, 'https://res.cloudinary.com/dgh58k3hv/image/upload/v1773279196/TOSTADAS_FRANCESAS_ac5xaw.jpg', TRUE, TRUE),
(3, 'Omelette Clásico', 'Huevos batidos con jamón, queso y vegetales.', 14.00, 'https://res.cloudinary.com/dgh58k3hv/image/upload/v1773279195/OMELETTE_kxkcam.avif', TRUE, FALSE);

-- BEBIDAS
INSERT INTO TB_ITEMS (ID_CAT, NAME, DESCRIPTION, PRICE, IMAGE_URL, IS_AVAILABLE, IS_FEATURED)
VALUES
(4, 'Jugo Natural de Naranja', 'Jugo recién exprimido, sin azúcar.', 8.00, 'https://res.cloudinary.com/dgh58k3hv/image/upload/v1773279324/JUGO_NARANJA_ep26pp.jpg', TRUE, FALSE),
(4, 'Smoothie de Fresa', 'Batido cremoso de fresas con yogurt.', 11.00, 'https://res.cloudinary.com/dgh58k3hv/image/upload/v1773279324/SMOOTHIE_FRESA_pmncp8.jpg', TRUE, TRUE),
(4, 'Limonada Frozen', 'Refrescante limonada frapé.', 9.00, 'https://res.cloudinary.com/dgh58k3hv/image/upload/v1773279325/LIMONADA_FROZEN_r19dpv.jpg', TRUE, FALSE);

-- TABLA DE MESAS
CREATE TABLE TB_TABLES (
  ID_TABLE SERIAL PRIMARY KEY,
  TABLE_NUMBER INT NOT NULL UNIQUE,
  CAPACITY INT NOT NULL CHECK (CAPACITY > 0),
  LOCATION VARCHAR(50), -- 'Interior', 'Terraza', 'VIP', etc.
  IS_AVAILABLE BOOLEAN DEFAULT TRUE,
  CREATED_AT TIMESTAMP DEFAULT NOW()
);

-- Mesas ejemplo
INSERT INTO TB_TABLES (TABLE_NUMBER, CAPACITY, LOCATION) VALUES
(1, 2, 'Interior'),
(2, 2, 'Interior'),
(3, 4, 'Interior'),
(4, 4, 'Terraza'),
(5, 6, 'Terraza'),
(6, 8, 'VIP');

-- TABLA DE RESERVAS
CREATE TABLE TB_RESERVATIONS (
  ID_RESERVATION SERIAL PRIMARY KEY,
  ID_USER UUID NULL REFERENCES TB_USERS(ID_USER) ON DELETE CASCADE,
  ID_TABLE INT NULL REFERENCES TB_TABLES(ID_TABLE),
  RESERVATION_DATE DATE NOT NULL,
  RESERVATION_TIME TIME NOT NULL,
  NUM_PEOPLE INT NOT NULL CHECK (NUM_PEOPLE > 0),
  CUSTOMER_NAME VARCHAR(100) NOT NULL,
  CUSTOMER_PHONE VARCHAR(15) NULL,
  CUSTOMER_EMAIL VARCHAR(100) NULL,
  SPECIAL_NOTES TEXT,
  SOURCE VARCHAR(20) DEFAULT 'ONLINE',
  STATUS VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
  -- Estados: 'PENDIENTE', 'CONFIRMADA', 'RECHAZADA', 'CANCELADA', 'COMPLETADA', 'NO_ASISTIO'
  ATTENDED_BY UUID REFERENCES TB_USERS(ID_USER),
  RESPONSE_NOTES TEXT,
  CREATED_AT TIMESTAMP DEFAULT NOW(),
  UPDATED_AT TIMESTAMP DEFAULT NOW(),
  CONSTRAINT chk_status_valid 
  CHECK (STATUS IN ('PENDIENTE','CONFIRMADA','RECHAZADA','CANCELADA','COMPLETADA', 'NO_ASISTIO')),
  CONSTRAINT chk_source
  CHECK (SOURCE IN ('ONLINE','RECEPCION')),
  CONSTRAINT unique_reservation 
  UNIQUE(ID_TABLE, RESERVATION_DATE, RESERVATION_TIME)
);

-- TABLA DE GALERIAS
CREATE TABLE TB_GALLERY (
  ID_GALLERY SERIAL PRIMARY KEY,
  TITLE VARCHAR(100),
  DESCRIPTION TEXT,
  IMAGE_URL TEXT NOT NULL,
  FEATURED BOOLEAN,
  IS_VISIBLE BOOLEAN DEFAULT TRUE,
  CREATED_AT TIMESTAMP DEFAULT NOW()
);

-- Galerias ejemplo
INSERT INTO TB_GALLERY (TITLE, DESCRIPTION, IMAGE_URL, FEATURED)
VALUES
('Vista del Local', 'Ambiente cálido con iluminación moderna.', 'https://res.cloudinary.com/dgh58k3hv/image/upload/v1773279934/local1_hc4yol.jpg', TRUE),
('Mesas y Decoración', 'Detalles en madera y estilo minimalista.', 'https://res.cloudinary.com/dgh58k3hv/image/upload/v1773280548/local2_aagzwd.jpg', FALSE),
('Entrada Principal', 'Fachada de la cafetería vista desde afuera.', 'https://res.cloudinary.com/dgh58k3hv/image/upload/v1773279930/local3_wgqcar.jpg', FALSE);

INSERT INTO TB_GALLERY (TITLE, DESCRIPTION, IMAGE_URL, FEATURED)
VALUES
('Noche de Café', 'Evento especial con música en vivo.', 'https://res.cloudinary.com/dgh58k3hv/image/upload/v1773280548/event1_l30p84.webp', TRUE),
('Taller de Barismo', 'Introducción a técnicas de latte art.', 'https://res.cloudinary.com/dgh58k3hv/image/upload/v1773279931/event2_s4uw8r.webp', FALSE);

INSERT INTO TB_GALLERY (TITLE, DESCRIPTION, IMAGE_URL, FEATURED)
VALUES
('Nuestro Barista', 'Barista preparando un espresso.', 'https://res.cloudinary.com/dgh58k3hv/image/upload/v1773280548/barista_ici0yg.jpg', TRUE),
('Equipo Completo', 'Fotografía grupal del staff.', 'https://res.cloudinary.com/dgh58k3hv/image/upload/v1773280547/equipo_ttdlk2.jpg', FALSE),
('Extracción de Café', 'Momento exacto del espresso saliendo.', 'https://res.cloudinary.com/dgh58k3hv/image/upload/v1773279934/extraccion_rrcmma.jpg', FALSE);

-- ÍNDICES PARA MEJOR PERFORMANCE
CREATE INDEX idx_users_email ON TB_USERS(EMAIL);
CREATE INDEX idx_users_role ON TB_USERS(ID_ROLE);
CREATE INDEX idx_items_category ON TB_ITEMS(ID_CAT);
CREATE INDEX idx_items_available ON TB_ITEMS(IS_AVAILABLE);
CREATE INDEX idx_reservations_user ON TB_RESERVATIONS(ID_USER);
CREATE INDEX idx_reservations_date ON TB_RESERVATIONS(RESERVATION_DATE);
CREATE INDEX idx_reservations_status ON TB_RESERVATIONS(STATUS);
CREATE INDEX idx_reservations_table ON TB_RESERVATIONS(ID_TABLE);

CREATE OR REPLACE FUNCTION update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
  NEW.UPDATED_AT = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_reservations
BEFORE UPDATE ON TB_RESERVATIONS
FOR EACH ROW
EXECUTE FUNCTION update_timestamp();
