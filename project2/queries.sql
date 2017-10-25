SELECT COUNT(*) FROM Users;
SELECT COUNT(*) FROM Items WHERE BINARY location="New York";
SELECT COUNT(*) FROM (SELECT count(*) FROM ItemCategory group by item_id having count(category)=4)f;
SELECT id FROM Items WHERE currently=(SELECT MAX(currently) FROM Items WHERE ends>20011220000001 AND n_bids>=1) AND n_bids>=1; 
SELECT COUNT(*) FROM Users WHERE seller_rating>1000;
SELECT COUNT(*) FROM Users WHERE seller_rating IS NOT NULL AND bidder_rating IS NOT NULL;
SELECT COUNT(DISTINCT c.category) FROM ItemCategory c, Items i WHERE c.item_id=i.id AND i.currently>100 AND i.n_bids>=1;
