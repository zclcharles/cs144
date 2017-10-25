CREATE TABLE Users (
	user_id varchar(40) not null,
	bidder_rating integer default null,
	seller_rating integer default null,
	location varchar(100),
	country varchar(40),
	primary key (user_id)
);
CREATE TABLE Items (
	id integer not null,
	name varchar(100),
	currently DECIMAL(8,2),
	buy_price DECIMAL(8,2),
	first_bid DECIMAL(8,2),
	n_bids integer,
	location varchar(100),
	latitude double,
	longitude double,
	country varchar(40),
	started TIMESTAMP,
	ends TIMESTAMP,
	seller_id varchar(40),
	description VARCHAR(4000),
	primary key(id),
	foreign key (seller_id) references Users(user_id)
);
CREATE TABLE Bids (
	item_id integer,
	bidder_id varchar(40),
	time TIMESTAMP,
	amount DECIMAL(8,2),
	primary key(item_id, bidder_id, time),
	foreign key(item_id) references Items(id),
	foreign key(bidder_id) references Users(user_id)
);
CREATE TABLE ItemCategory (
	item_id integer,
	category varchar(40),
	primary key (item_id, category),
	foreign key (item_id) references Items(id)
);
