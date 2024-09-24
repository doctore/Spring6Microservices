----------------------------------------------------------------
-- Main database
create user spring6 with encrypted password 'spring6';

create database spring6 with owner spring6;

-- Used by database management tool: Flyway
create schema changelog;

-- To connect from Linux console using the new user
psql -U spring6 -d spring6
