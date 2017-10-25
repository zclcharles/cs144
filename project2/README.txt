# Relations

- Users(user_id, bidder_rating, seller_rating, location, country)

  - Key: user_id

- Items(id, name, currently, buy_price, first_bid, n_bids, location, latitude, longitude, country, started, ends, seller_id, description)

  - Key: id

  - Foreign Key: seller_id REFERENCES Users(user_id)

- Bids(item_id, bidder_id, time, amount)

  - Key: item_id, bidder_id, time

  - Foreign Key: item_id REFERENCES Items(id), bidder_id REFERENCES Users(user_id)

- ItemCategory(item_id, category)

  - Key: item_id, category

  - Foreign Key: item_id REFERENCES Items(id)

# Completely nontrivial functional dependencies

There is no such functional dependencies if those that effectively specify keys are excluded.

# Are relations in BCNF?

Yes.

# Are relations in 4NF?

Yes.