INSERT INTO public.users (email, name) VALUES ('user1@user.ru', 'Vasia'), ( 'user2@user.ru', 'Petia');

INSERT INTO public.items
    (available, description, name, owner_id)
	VALUES
	('true', 'Дрель аккумуляторная + аккумулятор', 'Дрель аккумуляторная', 1),
	('true', 'Отвертка крестовая', 'Отвертка', 2);