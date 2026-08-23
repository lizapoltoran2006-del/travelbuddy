-- Пароли: qwerty123 (захешированы BCrypt)
INSERT INTO users (email, password, full_name, age, contact_info, role) VALUES
    ('alice@buddy.by', '$2a$10$NkKZR7L4r8X5s9T2m1Q4Iu3Y7R2oFmJkL9pV8bN6mX3cE1wQ5zO2', 'Алиса Иванова', 22, '@alice_travel', 'ROLE_USER'),
    ('bob@buddy.by', '$2a$10$NkKZR7L4r8X5s9T2m1Q4Iu3Y7R2oFmJkL9pV8bN6mX3cE1wQ5zO2', 'Борис Смирнов', 28, '@bob_drive', 'ROLE_USER'),
    ('carol@buddy.by', '$2a$10$NkKZR7L4r8X5s9T2m1Q4Iu3Y7R2oFmJkL9pV8bN6mX3cE1wQ5zO2', 'Карина Петрова', 24, '@carol_life', 'ROLE_USER');


-- Поездки
INSERT INTO trips (from_place, to_place, departure_date, total_seats, available_seats, description, driver_id) VALUES
    ('Минск', 'Гродно', '2026-08-20 15:00:00', 4, 4, 'Поеду на выходные в Гродно, есть места. Возьму с собой ноутбук.', 2),
    ('Минск', 'Брест', '2026-08-25 08:00:00', 3, 3, 'Еду в Брест на конференцию. Вежливые попутчики приветствуются.', 1),
    ('Гомель', 'Минск', '2026-08-22 10:30:00', 2, 2, 'Возвращаюсь из Гомеля, есть 2 места. С собой только ручная кладь.', 3);


-- Заявки (пассажиры подали заявки на поездки)
INSERT INTO trip_applications (status, reacted_at, trip_id, passenger_id, seats_count) VALUES
    ('ACCEPTED', '2026-08-13 10:00:00', 1, 1, 2),
    ('ACCEPTED', '2026-08-13 11:00:00', 1, 3, 1),
    ('ACCEPTED', '2026-08-13 12:00:00', 2, 2, 1);


-- Бюджет для поездки в Гродно (trip_id = 1)
INSERT INTO trip_budgets (expense_name, total_amount, amount_per_person, trip_id) VALUES
      ('Бензин (Минск-Гродно)', 80.00, 20.00, 1),
      ('Проживание (хостел на 1 ночь)', 100.00, 25.00, 1),
      ('Еда в дороге', 40.00, 10.00, 1);


-- Комментарии к поездкам
INSERT INTO comments (message, created_at, trip_id, user_id) VALUES
     ('Отличная поездка, всем рекомендую!', '2026-08-13 14:00:00', 1, 1),
     ('Когда планируешь остановки в пути?', '2026-08-13 14:30:00', 1, 3),
     ('Сколько времени в пути?', '2026-08-13 15:00:00', 2, 2);


-- Отзывы о пользователях
INSERT INTO reviews (rating, text, author_id, target_user_id) VALUES
    (5, 'Отличный водитель, ехали комфортно, спасибо!', 1, 2),
    (4, 'Хороший попутчик, приятно общаться, пунктуальный.', 3, 1),
    (5, 'Всё супер, поездка прошла отлично!', 2, 3);



-- Добавляем реквизиты оплаты для существующих поездок (если колонка уже создана)
UPDATE trips SET payment_details = 'Карта: 1234 5678 9012 3456 (Сбер), тел: +375 29 123-45-67' WHERE id = 1;
UPDATE trips SET payment_details = 'Перевод по номеру телефона: +375 44 987-65-43' WHERE id = 2;
UPDATE trips SET payment_details = 'Сбербанк: 9876 5432 1098 7654' WHERE id = 3;


