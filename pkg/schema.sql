SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

CREATE EXTENSION IF NOT EXISTS pg_trgm WITH SCHEMA public;
COMMENT ON EXTENSION pg_trgm IS 'text similarity measurement and index searching based on trigrams';


SET default_tablespace = '';
SET default_table_access_method = heap;

CREATE TABLE public.auth_user (
    id serial not null unique,
    password character varying(128) NOT NULL,
    last_login timestamp with time zone,
    is_superuser boolean NOT NULL,
    username character varying(150) NOT NULL,
    first_name character varying(30) NOT NULL,
    last_name character varying(150) NOT NULL,
    email character varying(254) NOT NULL,
    is_staff boolean NOT NULL,
    is_active boolean NOT NULL,
    date_joined timestamp with time zone NOT NULL
);

CREATE TABLE public.movies_actor (
    id serial not null unique,
    first_name character varying(300) NOT NULL,
    last_name character varying(300) NOT NULL
);

CREATE TABLE public.movies_comment (
    id serial not null unique,
    content character varying(1000) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    creator_id integer NOT NULL,
    movie_id integer NOT NULL
);

CREATE TABLE public.movies_genre (
    id serial not null unique,
    name character varying(300) NOT NULL
);

CREATE TABLE public.movies_location (
    id serial not null unique,
    name character varying(300) NOT NULL,
    index integer NOT NULL
);

CREATE TABLE public.movies_movie (
    id serial not null unique,
    name character varying(300) NOT NULL,
    description text NOT NULL,
    year integer NOT NULL,
    to_delete boolean NOT NULL,
    deleted boolean NOT NULL,
    created_at timestamp with time zone NOT NULL,
    location_id integer NOT NULL,
    link character varying(2000) NOT NULL
);

CREATE TABLE public.movies_movie_actors (
    id serial not null unique,
    movie_id integer NOT NULL,
    actor_id integer NOT NULL
);

CREATE TABLE public.movies_moviesingenre (
    id serial not null unique,
    genre_id integer NOT NULL,
    movie_id integer NOT NULL
);

CREATE TABLE public.movies_watchstatus (
    id serial not null unique,
    watched_on timestamp with time zone NOT NULL,
    movie_id integer NOT NULL,
    user_id integer NOT NULL
);

ALTER TABLE ONLY public.auth_user
    ADD CONSTRAINT auth_user_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.auth_user
    ADD CONSTRAINT auth_user_username_key UNIQUE (username);

ALTER TABLE ONLY public.movies_actor
    ADD CONSTRAINT movies_actor_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.movies_comment
    ADD CONSTRAINT movies_comment_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.movies_genre
    ADD CONSTRAINT movies_genre_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.movies_location
    ADD CONSTRAINT movies_location_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.movies_movie_actors
    ADD CONSTRAINT movies_movie_actors_movie_id_actor_id_73463e17_uniq UNIQUE (movie_id, actor_id);

ALTER TABLE ONLY public.movies_movie_actors
    ADD CONSTRAINT movies_movie_actors_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.movies_movie
    ADD CONSTRAINT movies_movie_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.movies_moviesingenre
    ADD CONSTRAINT movies_moviesingenre_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.movies_watchstatus
    ADD CONSTRAINT movies_watchstatus_pkey PRIMARY KEY (id);

CREATE INDEX auth_user_username_6821ab7c_like ON public.auth_user USING btree (username varchar_pattern_ops);

CREATE INDEX movies_comment_creator_id_470d0a84 ON public.movies_comment USING btree (creator_id);

CREATE INDEX movies_comment_movie_id_967cba99 ON public.movies_comment USING btree (movie_id);

CREATE INDEX movies_movie_actors_actor_id_44828aa1 ON public.movies_movie_actors USING btree (actor_id);

CREATE INDEX movies_movie_actors_movie_id_baed65f3 ON public.movies_movie_actors USING btree (movie_id);

CREATE INDEX movies_movie_location_id_07f5fc13 ON public.movies_movie USING btree (location_id);

CREATE INDEX movies_moviesingenre_genre_id_a7baf703 ON public.movies_moviesingenre USING btree (genre_id);

CREATE INDEX movies_moviesingenre_movie_id_8efdcb88 ON public.movies_moviesingenre USING btree (movie_id);

CREATE INDEX movies_watchstatus_movie_id_50ee87cb ON public.movies_watchstatus USING btree (movie_id);

CREATE INDEX movies_watchstatus_user_id_da4273c7 ON public.movies_watchstatus USING btree (user_id);

ALTER TABLE ONLY public.movies_comment
    ADD CONSTRAINT movies_comment_creator_id_470d0a84_fk_auth_user_id FOREIGN KEY (creator_id) REFERENCES public.auth_user(id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE ONLY public.movies_comment
    ADD CONSTRAINT movies_comment_movie_id_967cba99_fk_movies_movie_id FOREIGN KEY (movie_id) REFERENCES public.movies_movie(id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE ONLY public.movies_movie_actors
    ADD CONSTRAINT movies_movie_actors_actor_id_44828aa1_fk_movies_actor_id FOREIGN KEY (actor_id) REFERENCES public.movies_actor(id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE ONLY public.movies_movie_actors
    ADD CONSTRAINT movies_movie_actors_movie_id_baed65f3_fk_movies_movie_id FOREIGN KEY (movie_id) REFERENCES public.movies_movie(id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE ONLY public.movies_movie
    ADD CONSTRAINT movies_movie_location_id_07f5fc13_fk_movies_location_id FOREIGN KEY (location_id) REFERENCES public.movies_location(id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE ONLY public.movies_moviesingenre
    ADD CONSTRAINT movies_moviesingenre_genre_id_a7baf703_fk_movies_genre_id FOREIGN KEY (genre_id) REFERENCES public.movies_genre(id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE ONLY public.movies_moviesingenre
    ADD CONSTRAINT movies_moviesingenre_movie_id_8efdcb88_fk_movies_movie_id FOREIGN KEY (movie_id) REFERENCES public.movies_movie(id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE ONLY public.movies_watchstatus
    ADD CONSTRAINT movies_watchstatus_movie_id_50ee87cb_fk_movies_movie_id FOREIGN KEY (movie_id) REFERENCES public.movies_movie(id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE ONLY public.movies_watchstatus
    ADD CONSTRAINT movies_watchstatus_user_id_da4273c7_fk_auth_user_id FOREIGN KEY (user_id) REFERENCES public.auth_user(id) DEFERRABLE INITIALLY DEFERRED;

