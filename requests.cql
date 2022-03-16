// Requête 1 - possibleSpreaders
MATCH (p1:Person {healthstatus:"Sick"})-[visit1:VISITS]->(:Place)<-[visit2:VISITS]-(p2:Person {healthstatus:"Healthy"})
WHERE visit1.starttime > p1.confirmedtime AND visit2.starttime > p1.confirmedtime
RETURN p1.name

// Requête 2 - possibleSpreadCounts
MATCH (p1:Person {healthstatus:"Sick"})-[visit1:VISITS]->(:Place)<-[visit2:VISITS]-(p2:Person {healthstatus:"Healthy"})
WHERE visit1.starttime > p1.confirmedtime AND visit2.starttime > p1.confirmedtime
RETURN p1.name, COUNT(p2) AS nbHealthy

// Requête 3 - carelessPeople
MATCH (person:Person {healthstatus:"Sick"})-[:VISITS]->(place:Place)
WITH person, count(place.name) AS nbPlaces
WHERE nbPlaces > 10
RETURN person.name AS sickName, nbPlaces ORDER BY nbPlaces DESC

// Requête 4 - sociallyCareful
match ()-[:ACTED_]-

// Requête 5 - peopleToInform
match ()-[:ACTED_]-

// Requête 6 - setHighRisk
match ()-[:ACTED_]-

// Requête 7 - healthyCompanionsOf
match ()-[:ACTED_]-

// Requête 8 - topSickSite
match ()-[:ACTED_]-

// Requête 9 - sickFrom
match ()-[:ACTED_]-
