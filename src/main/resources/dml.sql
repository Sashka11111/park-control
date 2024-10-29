-- Вставка даних у таблицю користувачів
INSERT INTO Users (username, password, role)
VALUES
    ('Admin', '3b612c75a7b5048a435fb6ec81e52ff92d6d795a8b5a9c17070f6a63c97a53b2', 'ADMIN'),
    ('User1', 'a61a8adf60038792a2cb88e670b20540a9d6c2ca204ab754fc768950e79e7d36', 'USER'),
    ('User2', 'a61a8adf60038792a2cb88e670b20540a9d6c2ca204ab754fc768950e79e7d36', 'USER');
-- Вставка даних у таблицю паркувальних місць
INSERT INTO ParkingSpots (location, status, size)
VALUES
    ('Паркінг біля ТЦ', 'Вільне', 'Стандартне'),
    ('Вулиця Лесі Українки', 'Зайняте', 'Велике'),
    ('Паркінг біля стадіону', 'Вільне', 'Для інвалідів'),
    ('Площа Свободи', 'Зайняте', 'Стандартне'),
    ('Кафе на Хрещатику', 'Вільне', 'Велике');


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
