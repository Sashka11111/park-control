DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS ParkingSpots;
DROP TABLE IF EXISTS Reservations;
DROP TABLE IF EXISTS Categories;
DROP TABLE IF EXISTS ParkingSpotCategories;

-- Таблиця користувачів
CREATE TABLE Users (
    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(16) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER' NOT NULL CHECK(role IN ('USER', 'ADMIN'))
);

-- Таблиця паркувальних місць
CREATE TABLE ParkingSpots (
    spot_id INTEGER PRIMARY KEY AUTOINCREMENT,
    section VARCHAR(5), -- Секція (A, B, C, ...)
    level INT,  -- Поверх (1, 2, 3, ...)
    spot_number VARCHAR(10) NOT NULL, -- Номер місця в рамках секції
    status VARCHAR(15) NOT NULL CHECK (status IN ('Вільне', 'Зайняте')),
    size VARCHAR(20) NOT NULL CHECK (size IN ('Стандартне', 'Велике', 'Для інвалідів'))
);

-- Таблиця бронювань
CREATE TABLE Reservations (
    reservation_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    spot_id INTEGER NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    cost REAL NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (spot_id) REFERENCES ParkingSpots(spot_id) ON DELETE SET NULL
);

-- Таблиця категорій
CREATE TABLE Categories (
    category_id INTEGER PRIMARY KEY AUTOINCREMENT,
    category_name VARCHAR(50) NOT NULL
);

-- Таблиця для зв'язку "багато до багатьох" між ParkingSpots і Categories
CREATE TABLE ParkingSpotCategories (
    spot_id INTEGER NOT NULL,
    category_id INTEGER NOT NULL,
    PRIMARY KEY (spot_id, category_id),
    FOREIGN KEY (spot_id) REFERENCES ParkingSpots(spot_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (category_id) REFERENCES Categories(category_id)ON DELETE CASCADE ON UPDATE CASCADE
);
