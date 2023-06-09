CREATE TABLE IF NOT EXISTS public.users
(
    id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    email character varying(130)  NOT NULL,
    name character varying(50)  NOT NULL,
    CONSTRAINT users_pkey PRIMARY KEY (id),
    CONSTRAINT users_email_uk UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS public.items
(
    id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    available boolean NOT NULL,
    description character varying(255)  NOT NULL,
    name character varying(50)  NOT NULL,
    request_id bigint,
    owner_id bigint NOT NULL,
    CONSTRAINT items_pkey PRIMARY KEY (id),
    CONSTRAINT items_owner_id_fk FOREIGN KEY (owner_id)
        REFERENCES public.users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS public.bookings
(
    id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    end_date timestamp without time zone NOT NULL,
    start_date timestamp without time zone NOT NULL,
    status character varying(255),
    booker_id bigint NOT NULL,
    item_id bigint NOT NULL,
    CONSTRAINT bookings_pkey PRIMARY KEY (id),
    CONSTRAINT bookings_item_id_fk FOREIGN KEY (item_id)
        REFERENCES public.items (id) ON DELETE CASCADE,
    CONSTRAINT bookings_booker_id_fk FOREIGN KEY (booker_id)
        REFERENCES public.users (id) ON DELETE CASCADE

);
CREATE TABLE IF NOT EXISTS public.comments
(
    id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    created timestamp without time zone NOT NULL,
    text character varying(255) NOT NULL,
    author_id bigint NOT NULL,
    item_id bigint NOT NULL,
    CONSTRAINT comments_pkey PRIMARY KEY (id),
    CONSTRAINT comments_item_id_fk FOREIGN KEY (item_id)
        REFERENCES public.items (id) ON DELETE CASCADE,
    CONSTRAINT comments_author_id_fk FOREIGN KEY (author_id)
        REFERENCES public.users (id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS public.requests
(
    id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    created timestamp without time zone NOT NULL,
    description character varying(255) NOT NULL,
    requestor_id bigint NOT NULL,
    CONSTRAINT requests_pkey PRIMARY KEY (id),
    CONSTRAINT requests_requestor_id_fk FOREIGN KEY (requestor_id)
        REFERENCES public.users (id) ON DELETE CASCADE
)

