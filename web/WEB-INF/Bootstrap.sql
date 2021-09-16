--remove foreign links so we can drop the tables 
Alter Table GNARUS.Profiles DROP userid;
Alter Table GNARUS.Followers DROP beingfollowedid;
Alter Table GNARUS.Followers DROP followerid;
Alter Table GNARUS.Achievements DROP userid;
Alter Table GNARUS.Posts DROP authorid;
Alter Table GNARUS.Users DROP profileId;

DROP TABLE Posts;
DROP TABLE Profiles;
DROP TABLE Followers;
DROP TABLE Achievements;
DROP TABLE Users;

--must create users first since you don't have to specify a profileId
CREATE TABLE Users (
    username VARCHAR(15) NOT NULL UNIQUE,
    password VARCHAR(15) NOT NULL,
    badgelevel int,
    badgehabit int,
    id INT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    profileId INT
);

--create profiles second, then after created put the key in the user dbase row
CREATE TABLE Profiles (
    joindate BIGINT NOT NULL,  --SQL date is banned because it ignores hours, minutes, seconds
    firstname VARCHAR(20) NOT NULL,
    lastname VARCHAR(30) NOT NULL,
    email VARCHAR(100),
    phone CHAR(12),
    userid INT NOT NULL,
    goal VARCHAR(200),
    reward VARCHAR(200),
    picture BLOB(5120K),
    pictype VARCHAR(30),
    emailsubscribe BOOLEAN,
    textsubscribe BOOLEAN,
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
    beingfollowedid INT NOT NULL,
    followerid INT NOT NULL,    
    ID INT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY
);

--setup foreign key relationship for followerid and leaderid
Alter Table GNARUS.Followers
Add FOREIGN KEY (beingfollowedid)
References GNARUS.USERS (id); 
Alter Table GNARUS.FOLLOWERS
Add Foreign Key (followerid)
References GNARUS.USERS (id); 

--one user to many achievements
CREATE TABLE Achievements (
    exercise VARCHAR(140) NOT NULL,
    duration INT NOT NULL,
    intensity VARCHAR (20) NOT NULL,
    score DOUBLE NOT NULL,
    notes VARCHAR (200),
    userid INT NOT NULL,
    dateoccurred BIGINT NOT NULL,
    daterecorded BIGINT NOT NULL,
    id INT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY
);

Alter Table GNARUS.Achievements
Add FOREIGN KEY (userid)
References GNARUS.Users(id);


CREATE TABLE Posts (
    content VARCHAR(280) NOT NULL,
    authorid INT NOT NULL,
    postdate BIGINT NOT NULL,
    id INT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY
);

Alter Table GNARUS.Posts
Add FOREIGN KEY (authorid)
References GNARUS.USERS (id);

----> Start here with the adds

INSERT INTO Users (username, password, badgelevel, badgehabit) VALUES
    ('johndoe', 'password',0,0),
    ('jilljack', 'password',0,0),
    ('monkeyman', 'banana',0,0);

INSERT INTO Profiles (joindate, firstname, lastname, email, userid) VALUES
    (1339200000000, 'John', 'Doe', 'jd@example.com',1),
    (1383177600000, 'Jill', 'Jack', 'jj@nowhere.com',2),
    (1358208000000, 'Curious', 'George', 'monkey@tree.net',3);

UPDATE USERS SET profileId=1 WHERE id=1;
UPDATE USERS SET profileId=2 WHERE id=2;
UPDATE USERS SET profileId=3 WHERE id=3;

INSERT INTO Achievements (exercise, duration, intensity, score, notes, userid, dateoccurred, daterecorded) VALUES
    ('Running', 45, 'Strenuous', 0.33, 'Really hard today, very warm',1,1341360000000,1341360000000),
    ('Walking', 120, 'Light', 0.5, 'Looking for a banana',2,1378252800000,1378252800000),
    ('Yoga', 20, 'Moderate', 0.3, 'Hit up the hot yoga and sweated a ton!',3,1397174400000,1397174400000);

INSERT INTO Posts (content, authorid, postdate) VALUES
    ('I''m super tired of exercising!', 1, 1339200000000),
    ('Suck it up and get out there!', 3, 1358294400000),
    ('Anybody want to run at brushy creek tomorrow?', 2, 1383177600000);

