DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS ParkingSpots;
DROP TABLE IF EXISTS Reservations;
DROP TABLE IF EXISTS Payments;
DROP TABLE IF EXISTS Reports;
DROP TABLE IF EXISTS ParkingSpotReports;

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
    start_time DATETIME  NOT NULL,
    end_time DATETIME  NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (spot_id) REFERENCES ParkingSpots(spot_id) ON DELETE SET NULL
);
