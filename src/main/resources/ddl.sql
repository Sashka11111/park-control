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
    location VARCHAR(100) NOT NULL,
    status VARCHAR(10) NOT NULL CHECK(status IN ('FREE', 'OCCUPIED'))
);

-- Таблиця бронювань
CREATE TABLE Reservations (
    reservation_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    spot_id INTEGER NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (spot_id) REFERENCES ParkingSpots(spot_id) ON DELETE SET NULL
);

-- Таблиця платежів
CREATE TABLE Payments (
    payment_id INTEGER PRIMARY KEY AUTOINCREMENT,
    reservation_id INTEGER NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reservation_id) REFERENCES Reservations(reservation_id) ON DELETE CASCADE
);

-- Таблиця звітів
CREATE TABLE Reports (
    report_id INTEGER PRIMARY KEY AUTOINCREMENT,
    report_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    report_type VARCHAR(50) NOT NULL,
    report_details VARCHAR(500)
);

-- Таблиця для зв'язку "багато до багатьох" між ParkingSpots і Reports
CREATE TABLE ParkingSpotReports (
    spot_id INTEGER NOT NULL,
    report_id INTEGER NOT NULL,
    PRIMARY KEY (spot_id, report_id),
    FOREIGN KEY (spot_id) REFERENCES ParkingSpots(spot_id) ON DELETE CASCADE,
    FOREIGN KEY (report_id) REFERENCES Reports(report_id) ON DELETE CASCADE
);
