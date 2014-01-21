/*
Copyright (c) Steven P. Goldsmith. All rights reserved.

Created by Steven P. Goldsmith on November 6, 2011
sgoldsmith@com.codeferm 

Create tables used by Common Utils project
*/

/* Run these statements as postgres user on postgres database */

/* Execute drop database by itself */

drop database if exists devdb;

drop role if exists dev;

drop role if exists developers;

create role developers nosuperuser inherit nocreatedb nocreaterole;
comment on role developers is 'Development group';

/* Execute create database by itself */

create database devdb
  with owner = postgres
       encoding = 'UTF8'
       tablespace = pg_default
       connection limit = -1;

comment on database devdb is 'Development database';

grant connect, temporary on database devdb to public;
grant all on database devdb to postgres;
grant all on database devdb to developers;

create role dev login
  encrypted password 'md5c43812121e594f158520698ba706118f'
  nosuperuser inherit nocreatedb nocreaterole;
grant developers to dev;
comment on role dev is 'Development user';

/* Run these statements as dev user on devdb database */

drop table  if exists public.test_table;

create table public.test_table ( 
    id            	serial not null,
    char_field          char(50) null,
    varchar_field       varchar(50) null,
    boolean_field       boolean null,
    date_field          date null,
    time_field          time null,
    timestamp_field 	timestamp null,
    bigint_field    	bigint null,
    int_field           integer null,
    smallint_field    	smallint null,
    numeric_field       numeric (9,2) null,
    real_field          real null,
    double_field        double precision null,
    primary key(id)
);
