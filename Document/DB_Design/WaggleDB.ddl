create table WaggleStack(
	waggle_id VARCHAR(20) NOT NULL,
	longtitude FLOAT NOT NULL,
	latitude FLOAT NOT NULL,
	date_created DATETIME NOT NULL,
	PRIMARY KEY ( waggle_id )
);

create table Monitor(
	waggle_id VARCHAR(20) NOT NULL,
	battery FLOAT NOT NULL,
	charging VARCHAR(4) NOT NULL,
	heater VARCHAR(4) NOT NULL,
	fan VARCHAR(4) NOT NULL,
	update_time DATETIME NOT NULL,
	notice VARCHAR(60),
	PRIMARY KEY (waggle_id),
	FOREIGN KEY (waggle_id) REFERENCES WaggleStack(waggle_id)
);

create table WaggleEnv(
	waggle_id VARCHAR(20) NOT NULL,
	created_time DATETIME NOT NULL,
	temperature FLOAT NOT NULL,
	humidity FLOAT NOT NULL,
	voltage FLOAT NOT NULL,
	current FLOAT NOT NULL,
	FOREIGN KEY (waggle_id) REFERENCES WaggleStack(waggle_id),
	PRIMARY KEY (waggle_id, created_time)
);
