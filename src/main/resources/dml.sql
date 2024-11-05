-- Вставка даних у таблицю користувачів
INSERT INTO Users (username, password, role)
VALUES
    ('Admin', '3b612c75a7b5048a435fb6ec81e52ff92d6d795a8b5a9c17070f6a63c97a53b2', 'ADMIN'),
    ('User1', 'a61a8adf60038792a2cb88e670b20540a9d6c2ca204ab754fc768950e79e7d36', 'USER'),
    ('User2', 'a61a8adf60038792a2cb88e670b20540a9d6c2ca204ab754fc768950e79e7d36', 'USER');

-- Вставка даних у таблицю паркувальних місць
INSERT INTO ParkingSpots (section, level, spot_number, status, size)
VALUES
    ('A', 1, 'A01', 'Вільне', 'Стандартне'),
    ('A', 1, 'A02', 'Зайняте', 'Стандартне'),
    ('B', 2, 'B01', 'Вільне', 'Велике'),
    ('B', 2, 'B02', 'Вільне', 'Для інвалідів'),
    ('C', 3, 'C01', 'Вільне', 'Стандартне');

-- Вставка даних у таблицю бронювань
INSERT INTO Reservations (user_id, spot_id, start_time, end_time, cost)
VALUES
    (1, 1, '2024-10-25 08:00:00', '2024-10-25 12:00:00', 20.00),
    (2, 2, '2024-10-25 09:00:00', '2024-10-25 12:00:00', 15.00);

