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
	fan VARCHAR(4) NOT NULL,
	heater VARCHAR(4) NOT NULL,
	FOREIGN KEY (waggle_id) REFERENCES WaggleStack(waggle_id),
	PRIMARY KEY (waggle_id, created_time)
);

# latest DB create sentence
# Didn't Decide which elements are primary key
# Modified by Seungsoo 2018-01-31

create table WaggleStack(
	waggle_id VARCHAR(20) NOT NULL,
	longtitude FLOAT NOT NULL,
	latitude FLOAT NOT NULL,
	date_created DATETIME NOT NULL,
	PRIMARY KEY ( waggle_id )
);

create table BatteryStatus(
	waggle_id VARCHAR(20) NOT NULL,
	remain_battery FLOAT(5,2) NOT NULL,
	voltage FLOAT(5,2) NOT NULL,
	charging VARCHAR(4) NOT NULL,
	temperature FLOAT(5,2) NOT NULL,
	humidity FLOAT(5,2) NOT NULL,
	heater VARCHAR(4) NOT NULL,
	fan VARCHAR(4) NOT NULL,
	updated_time DATETIME NOT NULL,
	notice VARCHAR(20) NOT NULL,
	FOREIGN KEY (waggle_id) REFERENCES WaggleStack(waggle_id),
	#PRIMARY KEY (waggle_id)
);

create table BatteryStatus_log(
	waggle_id VARCHAR(20) NOT NULL,
	remain_battery FLOAT(5,2) NOT NULL,
	voltage FLOAT(5,2) NOT NULL,
	charging VARCHAR(4) NOT NULL,
	temperature FLOAT(5,2) NOT NULL,
	humidity FLOAT(5,2) NOT NULL,
	heater VARCHAR(4) NOT NULL,
	fan VARCHAR(4) NOT NULL,
	updated_time DATETIME NOT NULL,
	notice VARCHAR(20) NOT NULL,
	FOREIGN KEY (waggle_id) REFERENCES WaggleStack(waggle_id),
	#PRIMARY KEY (waggle_id)
);
