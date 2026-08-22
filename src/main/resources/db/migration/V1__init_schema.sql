-- 1. Создаем таблицу пользователей (users)
CREATE TABLE users (
id BIGSERIAL PRIMARY KEY,
email VARCHAR(255) NOT NULL UNIQUE,
password VARCHAR(255) NOT NULL,
full_name VARCHAR(255),
age INTEGER,
contact_info VARCHAR(255),
role VARCHAR(255) NOT NULL
);

-- 2. Создаем таблицу поездок (trips)
CREATE TABLE trips (
id BIGSERIAL PRIMARY KEY,
from_place VARCHAR(255) NOT NULL,
to_place VARCHAR(255) NOT NULL,
departure_date TIMESTAMP NOT NULL,
total_seats INTEGER NOT NULL,
available_seats INTEGER NOT NULL,
description TEXT,
driver_id BIGINT,
CONSTRAINT fk_trips_driver FOREIGN KEY (driver_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 3. Создаем таблицу заявок (trip_applications)
CREATE TABLE trip_applications (
id BIGSERIAL PRIMARY KEY,
status VARCHAR(255) NOT NULL,
reacted_at TIMESTAMP NOT NULL,
seats_count INTEGER,
trip_id BIGINT NOT NULL,
passenger_id BIGINT NOT NULL,
CONSTRAINT fk_apps_trip FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE,
CONSTRAINT fk_apps_passenger FOREIGN KEY (passenger_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 4. Создаем таблицу расходов (trip_budgets)
CREATE TABLE trip_budgets (
id BIGSERIAL PRIMARY KEY,
expense_name VARCHAR(255) NOT NULL,
total_amount NUMERIC(19, 2) NOT NULL,
amount_per_person NUMERIC(19, 2) NOT NULL,
trip_id BIGINT NOT NULL,
CONSTRAINT fk_budgets_trip FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE
);

-- 5. Создаем таблицу комментариев (comments)
CREATE TABLE comments (
id BIGSERIAL PRIMARY KEY,
message TEXT NOT NULL,
created_at TIMESTAMP NOT NULL,
trip_id BIGINT NOT NULL,
user_id BIGINT NOT NULL,
CONSTRAINT fk_comments_trip FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE,
CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 6. Создаем таблицу отзывов (reviews)
CREATE TABLE reviews (
id BIGSERIAL PRIMARY KEY,
rating INTEGER NOT NULL,
text TEXT,
author_id BIGINT NOT NULL,
target_user_id BIGINT NOT NULL,
CONSTRAINT fk_reviews_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE,
CONSTRAINT fk_reviews_target FOREIGN KEY (target_user_id) REFERENCES users(id) ON DELETE CASCADE
);
