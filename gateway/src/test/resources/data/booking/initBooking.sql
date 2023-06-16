INSERT INTO public.users (email, name)
    VALUES
    ('user1@user.ru', 'Vasia'),
    ('user2@user.ru', 'Petia'),
    ('user3@user.ru', 'Masha');

INSERT INTO public.items
    (available, description, name, owner_id)
	VALUES
	('true', 'Дрель аккумуляторная + аккумулятор', 'Дрель аккумуляторная', 1),
	('true', 'Телевизор домашний', 'Телик', 1),
	('true', 'Отвертка крестовая', 'Отвертка', 2),
	('true', 'Лейка садовая', 'Лейка', 3);

INSERT INTO public.bookings
    (end_date, start_date, status, booker_id, item_id)
    VALUES
    (current_timestamp + time '00:00:03', current_timestamp + time '00:00:04', 'WAITING', 2, 1),
    (current_timestamp + time '00:00:02', current_timestamp + time '00:00:03', 'WAITING', 1, 3);
