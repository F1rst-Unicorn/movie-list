set statement_timeout = 0;
set lock_timeout = 0;
set idle_in_transaction_session_timeout = 0;
set client_encoding = 'UTF8';
set standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
set check_function_bodies = false;
set xmloption = content;
set client_min_messages = warning;
set row_security = off;

create extension IF not EXISTS pg_trgm WITH SCHEMA public;
COMMENT on extension pg_trgm IS 'text similarity measurement and index searching based on trigrams';


set default_tablespace = '';
set default_table_access_method = heap;

create table public.auth_user
(
    id       serial primary key unique,
    password character varying(128) not null,
    username character varying(150) not null unique
);

create table public.movies_actor
(
    id         serial primary key unique,
    first_name character varying(300) not null,
    last_name  character varying(300) not null
);

create table public.movies_location
(
    id    serial primary key unique,
    name  character varying(300) not null,
    index integer                not null
);

create table public.movies_movie
(
    id          serial primary key unique,
    name        character varying(300)   not null,
    description text                     not null,
    year        integer                  not null,
    to_delete   boolean                  not null,
    deleted     boolean                  not null,
    created_at  timestamp with time zone not null default now(),
    location_id integer                  not null,
    link        character varying(2000)  not null,
    foreign key (location_id) references public.movies_location (id) on update cascade on delete cascade deferrable initially deferred
);

create table public.movies_comment
(
    id         serial primary key unique,
    content    character varying(1000)  not null,
    created_at timestamp with time zone not null default now(),
    creator_id integer                  not null,
    movie_id   integer                  not null,
    foreign key (creator_id) references public.auth_user (id) on update cascade on delete cascade deferrable initially deferred,
    foreign key (movie_id) references public.movies_movie (id) on update cascade on delete cascade deferrable initially deferred
);

create table public.movies_genre
(
    id   serial primary key unique,
    name character varying(300) not null
);

create table public.movies_movie_actors
(
    id       serial primary key unique,
    movie_id integer not null,
    actor_id integer not null,
    foreign key (actor_id) references public.movies_actor (id) on update cascade on delete cascade deferrable initially deferred,
    foreign key (movie_id) references public.movies_movie (id) on update cascade on delete cascade deferrable initially deferred,
    constraint movies_movie_actors_movie_id_actor_id_73463e17_uniq UNIQUE (movie_id, actor_id)
);

create table public.movies_moviesingenre
(
    id       serial primary key unique,
    genre_id integer not null,
    movie_id integer not null,
    foreign key (genre_id) references public.movies_genre (id) on update cascade on delete cascade deferrable initially deferred,
    foreign key (movie_id) references public.movies_movie (id) on update cascade on delete cascade deferrable initially deferred
);

create table public.movies_watchstatus
(
    id         serial primary key unique,
    watched_on timestamp with time zone not null default now(),
    movie_id   integer                  not null,
    user_id    integer                  not null,
    foreign key (movie_id) references public.movies_movie (id) on update cascade on delete cascade deferrable initially deferred,
    foreign key (user_id) references public.auth_user (id) on update cascade on delete cascade deferrable initially deferred
);

create index auth_user_username_6821ab7c_like on public.auth_user using btree (username varchar_pattern_ops);

create index movies_comment_creator_id_470d0a84 on public.movies_comment using btree (creator_id);

create index movies_comment_movie_id_967cba99 on public.movies_comment using btree (movie_id);

create index movies_movie_actors_actor_id_44828aa1 on public.movies_movie_actors using btree (actor_id);

create index movies_movie_actors_movie_id_baed65f3 on public.movies_movie_actors using btree (movie_id);

create index movies_movie_location_id_07f5fc13 on public.movies_movie using btree (location_id);

create index movies_moviesingenre_genre_id_a7baf703 on public.movies_moviesingenre using btree (genre_id);

create index movies_moviesingenre_movie_id_8efdcb88 on public.movies_moviesingenre using btree (movie_id);

create index movies_watchstatus_movie_id_50ee87cb on public.movies_watchstatus using btree (movie_id);

create index movies_watchstatus_user_id_da4273c7 on public.movies_watchstatus using btree (user_id);
