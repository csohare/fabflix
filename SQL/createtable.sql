drop database if exists moviedb;
create database if not exists moviedb;
use moviedb;
drop database if exists moviedb;
create database if not exists moviedb;
use moviedb;

CREATE TABLE IF NOT EXISTS movies (
        id varchar(10) NOT NULL,
        title varchar(100) NOT NULL,
        year integer NOT NULL,
        director varchar(100) NOT NULL,
        
        PRIMARY KEY (id)
);
       
CREATE TABLE IF NOT EXISTS stars (
    id varchar(10) NOT NULL,
    name varchar(100) NOT NULL,
    birthYear INTEGER DEFAULT NULL,
    
    PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS stars_in_movies (
    starId varchar(10) NOT NULL, 
    movieId varchar(10) NOT NULL,
    
    FOREIGN KEY (starID) REFERENCES stars(id),
    FOREIGN KEY (movieID) REFERENCES movies(id),
    PRIMARY KEY(starID, movieId)
);
       
CREATE TABLE IF NOT EXISTS genres(
    id integer NOT NULL AUTO_INCREMENT,
    name varchar(32) NOT NULL,
    
    PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS genres_in_movies (
    genreId integer,
    movieId varchar(10),
    
    FOREIGN KEY (genreId) REFERENCES genres(id),
    FOREIGN KEY (movieId) REFERENCES movies(id),
    PRIMARY KEY(genreId, movieId)
);

CREATE TABLE IF NOT EXISTS creditcards (
    id varchar(20) NOT NULL,
    firstName varchar(50) NOT NULL,
    lastName varchar(50) NOT NULL,
    expiration date NOT NULL,
    
    PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS customers (
    id integer NOT NULL AUTO_INCREMENT,
    firstName varchar(50) NOT NULL,
    lastName varchar(50) NOT NULL,
    ccId varchar(20) NOT NULL,
    address varchar(200) NOT NULL,
    email varchar(50) NOT NULL,
    password varchar(50) NOT NULL,
    
    FOREIGN KEY (ccId) REFERENCES creditcards(id),
    PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS sales (
    id integer NOT NULL,
    customerId integer NOT NULL,
    movieId varchar(10) NOT NULL,
    saleDate date NOT NULL,
    
    FOREIGN KEY (customerId) REFERENCES customers(id),
    FOREIGN KEY (movieId) REFERENCES movies(id),
    PRIMARY KEY(id)
);


CREATE TABLE IF NOT EXISTS ratings (
    movieId varchar(10) NOT NULL,
    rating float NOT NULL,
    numVotes integer NOT NULL,
    
    FOREIGN KEY (movieId) REFERENCES movies(id),
    PRIMARY KEY(movieId)
    
);