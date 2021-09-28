--remove foreign links so we can drop the tables 
Alter Table narusadmin.Profiles DROP userid;
Alter Table narusadmin.Achievements DROP userid;
Alter Table narusadmin.Posts DROP authorid;

DROP TABLE Posts;
DROP TABLE Profiles;
DROP TABLE Followers;
DROP TABLE Achievements;
DROP TABLE Users;

--must create users first, since it is foreign key for other tables
CREATE TABLE Users (
    username VARCHAR(15) NOT NULL UNIQUE,
    password VARCHAR(64) NOT NULL,
    badgelevel int,
    badgehabit int,
    userid INT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY
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
    picture BLOB(250K),
    pictype VARCHAR(30),
    emailsubscribe BOOLEAN,
    textsubscribe BOOLEAN,
    profileid INT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY
);

Alter Table narusadmin.PROFILES
Add FOREIGN KEY (USERID)
References narusadmin.USERS (userid);  


--many followers can track many leaders, and vice versa.  This is a linking table
--resolving the many to many relationship.  Note composite primary key helps the dbase
--ensure no duplicate entries of follower -> leader records.
CREATE TABLE Followers (
    beingfollowedid INT NOT NULL, 
    followerid INT NOT NULL, PRIMARY KEY (beingfollowedid, followerid)
);

--setup foreign key relationship for followerid and leaderid
--This is a linking table for a many to many relationship
Alter Table narusadmin.Followers
Add FOREIGN KEY (beingfollowedid)
References narusadmin.USERS (userid); 
Alter Table narusadmin.FOLLOWERS
Add Foreign Key (followerid)
References narusadmin.USERS (userid); 

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
    achievementid INT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY
);

Alter Table narusadmin.Achievements
Add FOREIGN KEY (userid)
References narusadmin.Users(userid);


CREATE TABLE Posts (
    content VARCHAR(280) NOT NULL,
    authorid INT NOT NULL,
    postdate BIGINT NOT NULL,
    postid INT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY
);

Alter Table narusadmin.Posts
Add FOREIGN KEY (authorid)
References narusadmin.USERS (userid);

----> Start here with the adds

INSERT INTO Users (username, password, badgelevel, badgehabit) VALUES
    ('johndoe', '$5$/vFJStJ5$yGZTx0JQnCIb8eVvuu.1OBUULeTpe.YzQ2dHAKEIaP1',0,0),
    ('jilljack', '$5$/q4Gxd2l$qyLiJAPQJFLU0xwjOYstKzLA/FpTiCHUZdrChA0.0Y.',0,0),
    ('monkeyman', '$5$5XjPpYUY$WmrJQYEHPPNYCyJpYUr.uVaXAS6hUXR4J6lJoQRrnb3',0,0);

INSERT INTO Profiles (joindate, firstname, lastname, email, userid) VALUES
    (1339200000000, 'John', 'Doe', 'jd@example.com',1),
    (1383177600000, 'Jill', 'Jack', 'jj@nowhere.com',2),
    (1358208000000, 'Curious', 'George', 'monkey@tree.net',3);


INSERT INTO Achievements (exercise, duration, intensity, score, notes, userid, dateoccurred, daterecorded) VALUES
    ('Running', 45, 'Strenuous', 0.33, 'Really hard today, very warm',1,1341360000000,1341360000000),
    ('Walking', 120, 'Light', 0.5, 'Looking for a banana',2,1378252800000,1378252800000),
    ('Yoga', 20, 'Moderate', 0.3, 'Hit up the hot yoga and sweated a ton!',3,1397174400000,1397174400000);

INSERT INTO Posts (content, authorid, postdate) VALUES
    ('I''m super tired of exercising!', 1, 1339200000000),
    ('Suck it up and get out there!', 3, 1358294400000),
    ('Anybody want to run at brushy creek tomorrow?', 2, 1383177600000);

