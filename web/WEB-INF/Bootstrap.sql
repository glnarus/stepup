--remove foreign links so we can drop the tables 
Alter Table GNARUS.PROFILES DROP userid;
Alter Table GNARUS.Followers DROP userid;
Alter Table GNARUS.Achievements DROP userid;
Alter Table GNARUS.Posts DROP authorid;
Alter Table GNARUS.USERS DROP profileId;

DROP TABLE Posts;
DROP TABLE Profiles;
DROP TABLE Followers;
DROP TABLE Achievements;
DROP TABLE Users;

--must create users first since you don't have to specify a profileId
CREATE TABLE Users (
    username VARCHAR(15) NOT NULL UNIQUE,
    password VARCHAR(15) NOT NULL,
    id INT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    profileId INT
);

--create profiles second, then after created put the key in the user dbase row
CREATE TABLE Profiles (
    joindate DATE DEFAULT CURRENT_DATE,
    firstname VARCHAR(20) NOT NULL,
    lastname VARCHAR(30) NOT NULL,
    email VARCHAR(100),
    phone CHAR(10),
    userid INT NOT NULL,
    goal LONG VARCHAR,
    reward VARCHAR(80),
    picture BLOB(200K),
    pictype VARCHAR(30),
    emailsubscribe BIT,
    textsubscribe BIT,
    id INT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY
);

Alter Table GNARUS.PROFILES
Add FOREIGN KEY (USERID)
References GNARUS.USERS (id);  

--setup foreign key relationship now that Profiles exists
Alter Table GNARUS.USERS
Add FOREIGN KEY (profileId)
References GNARUS.Profiles (id);

--many followers can track many leaders
CREATE TABLE Followers (
    ID INT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    leaderid INT,
    followerid INT    
);

--setup foreign key relationship for followerid and leaderid
Alter Table GNARUS.FOLLOWERS
Add FOREIGN KEY (leaderid)
References GNARUS.Users(userid)
Add Foreign Key (followerid)
References GNARUS.User(userid);

--one user to many achievements
CREATE TABLE Achievements (
    exercise VARCHAR(140) NOT NULL,
    duration INT NOT NULL,
    notes VARCHAR (280),
    userid INT NOT NULL,
    dateoccurred DATE DEFAULT CURRENT_DATE,
    daterecorded DATE DEFAULT CURRENT_DATE,
    id INT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY
);

Alter Table GNARUS.Achievements
Add FOREIGN KEY (userid)
References GNARUS.Users(userid);


CREATE TABLE Posts (
    content VARCHAR(280) NOT NULL,
    authorid INT NOT NULL,
    postdate DATE DEFAULT CURRENT_DATE,
    id INT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY
);

Alter Table GNARUS.Posts
Add FOREIGN KEY (authorid)
References GNARUS.USERS (id);

----> Start here with the adds

INSERT INTO Users (username, password) VALUES
    ('johndoe', 'password'),
    ('jilljack', 'password');

INSERT INTO Profiles (joindate, firstname, lastname, email, zip, userid) VALUES
    ('2013-05-09', 'John', 'Doe', 'jd@example.com', '98008',1),
    ('2013-10-31', 'Jill', 'Jack', 'jj@nowhere.com', '24201',2);

UPDATE USERS SET profileId=1 WHERE id=1;
UPDATE USERS SET profileId=2 WHERE id=2;

INSERT INTO Posts (content, authorid, postdate) VALUES
    ('I''m a white-hat hacking my wonky Twonky server.', 1, '2013-05-09'),
    ('My wonky Twonky server conked out.', 1, '2014-06-23'),
    ('I see good reason not to configure Twonky.', 2, '2013-11-01');

