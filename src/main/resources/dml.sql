-- Вставка даних у таблицю користувачів
INSERT INTO Users (name, email, password, role)
VALUES
    ('John Doe', 'john.doe@example.com', 'SecurePass123!', 'USER'),
    ('Jane Smith', 'jane.smith@example.com', 'AdminPass2024#', 'ADMIN'),
    ('Alice Johnson', 'alice.johnson@example.com', 'AliceSecure2023$', 'USER'),
    ('Bob Brown', 'bob.brown@example.com', 'BobBrown2023@', 'USER');

-- Вставка даних у таблицю паркувальних місць
INSERT INTO ParkingSpots (location, status)
VALUES
    ('Zone A - Spot 1', 'FREE'),
    ('Zone A - Spot 2', 'OCCUPIED'),
    ('Zone B - Spot 1', 'FREE');

-- Вставка даних у таблицю бронювань
INSERT INTO Reservations (user_id, spot_id, start_time, end_time)
VALUES
    (1, 1, '2024-10-25 08:00:00', '2024-10-25 12:00:00'),
    (2, 2, '2024-10-25 09:00:00', NULL);

-- Вставка даних у таблицю платежів
INSERT INTO Payments (reservation_id, amount, payment_date)
VALUES
    (1, 20.00, '2024-10-25 12:15:00'),
    (2, 15.00, CURRENT_TIMESTAMP);

-- Вставка даних у таблицю звітів
INSERT INTO Reports (report_type, report_details)
VALUES
    ('Usage Report', 'Monthly usage statistics for parking spots'),
    ('Revenue Report', 'Total revenue generated for October');

-- Вставка даних у таблицю для зв’язків між ParkingSpots і Reports
INSERT INTO ParkingSpotReports (spot_id, report_id) VALUES
    (1, 1),
    (2, 1),
    (1, 2);
