-- For Postgres

-- CREATE TABLE property (
-- 	id VARCHAR PRIMARY KEY,
-- 	description VARCHAR(100),
-- 	daily_rate NUMERIC(10,2)
-- );
--
-- CREATE TABLE schedule (
-- 	id VARCHAR PRIMARY KEY,
-- 	start_date TIMESTAMP,
-- 	end_date TIMESTAMP,
-- 	cancel_date TIMESTAMP,
-- 	total NUMERIC(10,2),
-- 	property_id VARCHAR NOT NULL,
-- 	type VARCHAR,
-- 	guests jsonb,
--
-- 	CONSTRAINT fk_schedule_property
-- 		FOREIGN KEY(property_id)
-- 			REFERENCES property(id)
-- );

-- For H2

CREATE TABLE property (
	id VARCHAR PRIMARY KEY,
	description VARCHAR(100),
	daily_rate NUMERIC(10,2)
);

CREATE TABLE schedule (
	id VARCHAR PRIMARY KEY,
	start_date TIMESTAMP,
	end_date TIMESTAMP,
	cancel_date TIMESTAMP,
	total NUMERIC(10,2),
	property_id VARCHAR NOT NULL,
	type VARCHAR,
	guests VARCHAR,

	CONSTRAINT fk_schedule_property
		FOREIGN KEY(property_id)
			REFERENCES property(id)
);





