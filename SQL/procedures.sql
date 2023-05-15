use moviedb;

DROP FUNCTION IF EXISTS nextStarId;
DELIMITER $$

CREATE FUNCTION nextStarId()
RETURNS varchar(10)
DETERMINISTIC
BEGIN
	DECLARE starId varchar(10);
    DECLARE starInt INTEGER;
    
    SELECT max(id)
    INTO starId
    FROM stars;
    
    SET starId = substring(starId, 3);
    SET starInt = CAST(starId AS SIGNED) + 1;
    SET starId = CONCAT("nm", starInt);
    
    RETURN (starId);
    
END$$
DELIMITER ;

DROP FUNCTION IF EXISTS genreId;
DELIMITER $$

CREATE FUNCTION genreId(genreName varchar(32))
RETURNS INTEGER
DETERMINISTIC
BEGIN
	DECLARE genreId INTEGER;
    
	SELECT id INTO genreId FROM genres WHERE name = genreName;
	IF genreId IS NOT NULL THEN
        return genreId;
	ELSE
		SELECT max(id) INTO genreId FROM genres;
        SET genreID = genreID + 1;
		INSERT INTO genres VALUES(genreId, genreName);
        SELECT id INTO genreId FROM genres WHERE name = genreName;
        return genreId;
	END IF;
END$$

DELIMITER ;

DROP FUNCTION IF EXISTS nextMovieId;
DELIMITER $$

CREATE FUNCTION nextMovieId(movieTitle varchar(100), movieYear INTEGER, movieDirector varchar(100))
RETURNS varchar(10) DETERMINISTIC
BEGIN
	DECLARE movieId varchar(10);
    DECLARE tmpString varchar(10);
    DECLARE movieInt INTEGER;
    
	SELECT id INTO movieId FROM movies WHERE title = movieTitle and year = movieYear and director = movieDirector;
	IF movieid  IS NOT NULL THEN
        return "-1";
	ELSE
		SELECT max(id) INTO movieId FROM movies;
        SET movieId = substring(movieId, 3);
        SELECT LPAD(CONVERT(CONVERT(movieId, DECIMAL), UNSIGNED) + 1, 7, '0') INTO movieId;
        SET movieId = CONCAT("tt", movieId);
        RETURN movieId;
        
	END IF;
END$$

DELIMITER ;


DROP PROCEDURE IF EXISTS InsertStar;
DELIMITER $$

CREATE PROCEDURE InsertStar(IN starName varchar(100), IN starBirth INTEGER)
BEGIN
	DECLARE starId varchar(10);
    SELECT nextStarId() INTO starId;
    IF(starBirth = -1) THEN
		INSERT INTO stars VALUES(starId, starName, NULL);
	ELSE
		INSERT INTO stars VALUES(starId, starName, starBirth);
	END IF;
    SELECT starId as newStarId;
END$$
DELIMITER ;


DROP PROCEDURE IF EXISTS InsertMovie;
DELIMITER $$

CREATE PROCEDURE InsertMovie(IN movieTitle varchar(100), IN movieYear INTEGER, IN movieDirector varchar(100),
IN starName varchar(100), IN starBirth INTEGER, IN genreName varchar(32))
BEGIN
	DECLARE movieId varchar(10);
    DECLARE genreId INTEGER;
    DECLARE starId varchar(10);
    SELECT nextMovieId(movieTitle, movieYear, movieDirector) INTO movieId;
    IF movieId = -1 THEN
		SELECT movieId as movieId;
	ELSE
		INSERT INTO movies VALUES(movieId, movieTitle, movieYear, movieDirector);
        SELECT genreId(genreName) INTO genreId;
        SELECT id INTO starId FROM stars WHERE name = starName and birthYear = starBirth;
        IF starId IS NULL THEN
			SELECT nextStarId() INTO starId;
				IF starBirth = -1 THEN
					INSERT INTO stars (id, name) VALUES(starId, starName);
			ELSE
				INSERT INTO stars VALUES(starId, starName, starBirth);
			END IF;
		END IF;
        
        SELECT movieId, genreId, starID;
        INSERT INTO genres_in_movies VALUES(genreId, movieID);
        INSERT INTO stars_in_movies VALUES(starId, movieID);
        
        
	END IF;
END$$
DELIMITER ;
